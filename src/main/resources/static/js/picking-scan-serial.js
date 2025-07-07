document.addEventListener('DOMContentLoaded', function () {
  const input = document.querySelector('input[name="scannedSerial"]');
  const statusDiv = document.getElementById('status-message');
  const saveAllBtn = document.getElementById('saveAllBtn');
  const endPickingOrder = $('#endPickingOrder')[0];

  if (!input) return;

  const scannedSerials = new Set();
  const scannedSerialDetails = [];

  function showStatus(message, type = 'success', duration = 0) {
    statusDiv.textContent = message;
    statusDiv.className = (type === 'success' ? 'text-success' : 'text-danger') + ' mt-2';
    statusDiv.style.display = 'block';
    if (duration > 0) {
      setTimeout(() => statusDiv.style.display = 'none', duration);
    }
  }

  input.addEventListener('keypress', function (e) {
    if (e.key !== 'Enter') return;
    e.preventDefault();

    const serial = input.value.trim();
    if (!serial) return;

    if (scannedSerials.has(serial)) {
      showStatus(`Số Serial: "${serial}" đã quét rồi.`, 'danger');
      input.value = '';
      setTimeout(() => input.focus(), 100);
      return;
    }

    const itemCodes = Array.from(document.querySelectorAll('#orderTable tbody tr'))
      .map(row => row.cells[1]?.textContent?.trim())
      .filter(Boolean);

    fetch('/picking/scan-serial', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ serial, itemCodes })
    })
      .then(res => res.json())
      .then(data => {
        showStatus(data.message || 'Đã được quét', data.success ? 'success' : 'danger');
        input.value = '';
        setTimeout(() => input.focus(), 100);

        if (data.success && data.pickingSerialNo && updatePickingQty(data.pickingSerialNo)) {
          scannedSerials.add(serial);
          scannedSerialDetails.push(data.pickingSerialNo);
        }
      })
      .catch(error => {
        console.error(error);
        showStatus('Lỗi hệ thống khi quét serial!', 'danger');
      });
  });

  /* ---------------------------------------------------------------- */
  function updatePickingQty(pickingSerialNo) {
      const rows = document.querySelectorAll('#orderTable tbody tr');
      let updated = false;

      rows.forEach((row) => {
          const itemCodeTd = row.children[1]; // Cột Mã SP
          const input = row.querySelector('input[type="number"]'); // Cột SL nhặt

          if (itemCodeTd && input) {
              const itemCode = itemCodeTd.textContent.trim();
              const serialItemCode = pickingSerialNo.itemCode?.trim();

              if (itemCode === serialItemCode) {
                  // Lấy số lượng hiện tại và số lượng thêm
                  const currentQty = parseFloat(input.value) || 0;
                  const addedQty = parseFloat(pickingSerialNo.pickingqty) || 0;
                  const newQty = currentQty + addedQty;

                  // Đảm bảo định dạng lại số lượng nhặt
                  input.value = newQty;

                  // Lấy SL yêu cầu từ cột thứ 4 (index = 3)
                  const requestQtyText = row.children[4]?.textContent?.trim();
                  const requestQty = parseFloat(requestQtyText) || 0;

                  // Lấy lại SL nhặt thực tế
                  const pickedQty = parseFloat(input.value) || 0;

                  // Cập nhật trạng thái
                  const statusCell = row.children[6];
                  statusCell.innerHTML = '';

                  // Kiểm tra trạng thái
                  if (pickedQty >= requestQty) {
                      statusCell.innerHTML = '<i class="fas fa-check-circle text-success" title="Đã nhặt đủ hoặc dư"></i>';
                  } else {
                      statusCell.innerHTML = '<i class="fas fa-hourglass-half text-warning" title="Chưa nhặt đủ"></i>';
                  }

                  // Highlight dòng tạm thời
                  row.classList.add('row-highlight');
                  setTimeout(() => row.classList.remove('row-highlight'), 10000);

                  updated = true;
              }
          }
      });

      return updated;
  }
  /* ---------------------------------------------------------------- */

  if (saveAllBtn) {
    saveAllBtn.addEventListener('click', function () {
      if (scannedSerialDetails.length === 0) {
        showStatus('Chưa quét số Serial!', 'danger');
        return;
      }

      fetch('/picking/save-serials', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(scannedSerialDetails)
      })
        .then(res => res.json())
        .then(data => {
          showStatus(data.message || 'Đã lưu serial thành công', data.success ? 'success' : 'danger');
          if (data.success) {
            scannedSerials.clear();
            scannedSerialDetails.length = 0;
          }
        })
        .catch(error => {
          console.error(error);
          showStatus('Lỗi khi lưu serial!', 'danger');
        });
		
		endPickingOrder.disabled = false;
    });
  }
});
