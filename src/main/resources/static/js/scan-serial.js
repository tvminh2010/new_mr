document.addEventListener('DOMContentLoaded', function () {
  const input = document.querySelector('input[name="scannedSerial"]');
  const statusDiv = document.getElementById('status-message');
  const saveBtn = document.getElementById('saveAllBtn');

  if (!input) return;

  const scannedSerials = new Set(); // để kiểm tra serial trùng
  const scannedSerialDetails = [];  // để lưu chi tiết gửi về controller

  /* ---------------------------------------------------------------- */
  input.addEventListener('keypress', function (e) {
    if (e.key !== 'Enter') return;
    e.preventDefault();

    const serial = input.value.trim();
    if (!serial) return;

    if (scannedSerials.has(serial)) {
      statusDiv.style.display = 'block';
      statusDiv.textContent = `Số Serial: "${serial}" đã quét rồi.`;
      statusDiv.className = 'text-danger';
      input.value = '';
      setTimeout(() => input.focus(), 100);
      return;
    }

    const itemCodes = Array.from(document.querySelectorAll('#orderTable tbody tr'))
      .map(row => row.cells[0]?.textContent?.trim())
      .filter(Boolean);

    fetch('/picking/scan-serial', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ serial, itemCodes })
    })
      .then(res => res.json())
      .then(data => {
        statusDiv.style.display = 'block';
        statusDiv.textContent = data.message || 'Đã xử lý';
        statusDiv.className = data.success ? 'text-success' : 'text-danger';

        input.value = '';
        setTimeout(() => input.focus(), 100);

        if (data.success && data.pickingSerialNo && updatePickingQty(data.pickingSerialNo)) {
          scannedSerials.add(serial);
          scannedSerialDetails.push(data.pickingSerialNo); // Lưu lại để gửi lên backend
        }
      })
      .catch(console.error);
  });

  /* ---------------------------------------------------------------- */
  function updatePickingQty(pickingSerialNo) {
    const table = document.getElementById('orderTable');
    const rows = table.querySelectorAll('tbody tr');
    let updated = false;

    rows.forEach((row) => {
      const itemCodeTd = row.children[0];
      const input = row.querySelector('input[type="number"]');

      if (itemCodeTd && input) {
        const itemCode = itemCodeTd.textContent.trim();
        if (itemCode === pickingSerialNo.itemCode) {
          const currentQty = parseFloat(input.value) || 0;
          const addedQty = parseFloat(pickingSerialNo.pickingqty) || 0;
          input.value = currentQty + addedQty;

          row.style.backgroundColor = '#d4edda';
          setTimeout(() => row.style.backgroundColor = '', 1000);

          updated = true;
        }
      }
    });

    return updated;
  }
  /* ---------------------------------------------------------------- */

  if (saveBtn) {
    saveBtn.addEventListener('click', function () {
      if (scannedSerialDetails.length === 0) {
        alert('Chưa có Serial nào để lưu!');
        return;
      }

      fetch('/picking/save-serials', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(scannedSerialDetails)
      })
        .then(res => res.json())
        .then(data => {
          alert(data.message || 'Đã lưu serial thành công');
          scannedSerials.clear(); // xóa để có thể quét lại nếu cần
          scannedSerialDetails.length = 0;
        })
        .catch(console.error);
    });
  }
});
