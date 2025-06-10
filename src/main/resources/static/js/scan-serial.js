document.addEventListener('DOMContentLoaded', function () {
  const input = document.querySelector('input[name="scannedSerial"]');
  const statusDiv = document.getElementById('status-message');

  if (!input) return;
  /* ---------------------------------------------------------------- */
  input.addEventListener('keypress', function (e) {
    if (e.key !== 'Enter') return;
    e.preventDefault();

    const serial = input.value.trim();
    if (!serial) return;

    fetch('/picking/scan-serial', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ serial })
    })
      .then(res => res.json())
      .then(data => {
        statusDiv.style.display = 'block';
        statusDiv.textContent = data.message || 'Đã xử lý';
        statusDiv.className = data.success ? 'text-success' : 'text-danger';

        input.value = '';
        setTimeout(() => input.focus(), 100);

        if (data.pickingSerialNo) {
          updatePickingQty(data.pickingSerialNo);
        }
      })
      .catch(console.error);
  });
  /* ---------------------------------------------------------------- */
  function updatePickingQty(pickingSerialNo) {
    const table = document.getElementById('orderTable');
    const rows = table.querySelectorAll('tbody tr');

    rows.forEach((row) => {
      const itemCodeTd = row.children[0]; // cột mã NVL
      const input = row.querySelector('input[type="number"]'); // chọn ô input trong dòng đó

      if (itemCodeTd && input) {
        const itemCode = itemCodeTd.textContent.trim();
        if (itemCode === pickingSerialNo.itemCode) {
          const currentQty = parseFloat(input.value) || 0;
          const addedQty = parseFloat(pickingSerialNo.pickingqty) || 0;
          input.value = currentQty + addedQty;

          // Gợi ý: highlight dòng được cập nhật
          row.style.backgroundColor = '#d4edda'; // xanh nhạt
          setTimeout(() => row.style.backgroundColor = '', 1000);
        }
      }
    });
  }
  /* ---------------------------------------------------------------- */
  
});
