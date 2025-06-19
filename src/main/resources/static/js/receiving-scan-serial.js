$(document).ready(function () {
  const input = document.querySelector('input[name="scannedSerial"]');
  const statusDiv = document.getElementById('status-message');
  const saveAllBtn = document.getElementById('saveAllBtn');
  const endReceivingOrder = $('#endReceivingOrder')[0];

  if (!input) return;

  const scannedSerials = new Set();
  const scannedSerialDetails = [];

  function showStatus(message, type = 'success', duration = 0) {
    statusDiv.textContent = message;
    statusDiv.className = (type === 'success' ? 'text-success' : 'text-danger') + ' mt-2';
    statusDiv.style.display = 'block';
    if (duration > 0) {
      setTimeout(() => (statusDiv.style.display = 'none'), duration);
    }
  }

  input.addEventListener('keypress', function (e) {
    if (e.key !== 'Enter') return;
    e.preventDefault();

    const serial = input.value.trim();
    if (!serial) return;

    if (scannedSerials.has(serial)) {
      showStatus(`Số Serial: "${serial}" đã được quét rồi.`, 'danger');
      input.value = '';
      setTimeout(() => input.focus(), 100);
      return;
    }

    const itemCodes = Array.from(document.querySelectorAll('#orderTable tbody tr'))
      .map(row => row.cells[0]?.textContent?.trim())
      .filter(Boolean);

    fetch('/receiving/scan-serial', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ serial, itemCodes })
    })
      .then(res => res.json())
      .then(data => {
		console.log('==> JSON response:', data); // <== Thêm dòng này
        showStatus(data.message || 'Serial hợp lệ', data.success ? 'success' : 'danger');
        input.value = '';
        setTimeout(() => input.focus(), 100);

        if (data.success && data.receivingSerialNo && updateReceivingQty(data.receivingSerialNo)) {
          scannedSerials.add(serial);
          scannedSerialDetails.push(data.receivingSerialNo);
        }
      })
      .catch(error => {
        console.error(error);
        showStatus('Lỗi khi quét serial!', 'danger');
      });
  });

  
  
  function updateReceivingQty(receivingSerialDto) {
	console.log('📦 Đang cập nhật SL nhận cho:', receivingSerialDto.itemcode);
    const inputs = document.querySelectorAll('input[name^="receivingQty"]');
    let updated = false;

    inputs.forEach((input) => {
      const itemCode = input.getAttribute("data-itemcode");

      if (itemCode === receivingSerialDto.itemcode) {
        const currentValue = parseFloat(input.value) || 0;
        const addedValue = parseFloat(receivingSerialDto.receivedQty) || 0;
        input.value = currentValue + addedValue;

        // Highlight hàng
        const row = input.closest("tr");
        if (row) {
          row.style.backgroundColor = "#d1ecf1";
          setTimeout(() => (row.style.backgroundColor = ""), 3000);
        }

        updated = true;
      }
    });

    return updated;
  }






  if (saveAllBtn) {
    saveAllBtn.addEventListener('click', function () {
      if (scannedSerialDetails.length === 0) {
        showStatus('Chưa quét serial nào!', 'danger');
        return;
      }
	  
	  
      fetch('/receiving/save-serials', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(scannedSerialDetails)
      })
        .then(res => res.json())
        .then(data => {
          showStatus(data.message || 'Lưu serial thành công!', data.success ? 'success' : 'danger');
          if (data.success) {
            scannedSerials.clear();
            scannedSerialDetails.length = 0;
          }
        })
        .catch(error => {
          console.error(error);
          showStatus('Lỗi khi lưu serial!', 'danger');
        });

      endReceivingOrder.disabled = false;
    });
  }
  
  
  console.log("✅ receiving-scan-serial.js đã load xong");
});
