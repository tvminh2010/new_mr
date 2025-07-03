
/* ------------------------------------------------------------ */

$(document).ready(function() {
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

	input.addEventListener('keypress', function(e) {
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

	/* ------------------------------------------------------------ */
	function updateReceivingStatusByRow($input) {
		const receivingQty = parseInt($input.val()) || 0;
		const pickingQty = parseInt($input.data('pickingqty')) || 0;

		const $row = $input.closest('tr');
		const $statusIcon = $row.find('.status-icon');
		const $statusCell = $row.find('.status-cell');

		$statusIcon.empty(); // Xoá biểu tượng cũ

		if (receivingQty === pickingQty) {
			$statusIcon.append('<i class="fas fa-check-circle text-success" title="Đã nhận đủ"></i>');
			$row.addClass('table-success');
		} else {
			$statusIcon.append('<i class="fas fa-hourglass-half text-warning" title="Chưa nhận đủ"></i>');
			$row.removeClass('table-success');
		}
	}

	$(document).ready(function() {
		$('.receiving-qty').on('change', function() {
			updateReceivingStatusByRow($(this));
		});

		// Tự cập nhật lúc load ban đầu
		$('.receiving-qty').each(function() {
			updateReceivingStatusByRow($(this));
		});
	});


	/* ------------------------------------------------------------ */
	function updateReceivingStatusByTable() {
		$('#orderTable tbody tr').each(function() {
			const $row = $(this);
			const pickingQty = parseInt($row.find('td:eq(3)').text()) || 0;
			const receivingQty = parseInt($row.find('td:eq(4) input').val()) || 0;
			const $statusCell = $row.find('td:eq(5)');

			$statusCell.empty(); // Xoá icon cũ

			if (receivingQty >= pickingQty) {
				$statusCell.append('<i class="fas fa-check-circle text-success" title="Đã nhận đủ"></i>');
				$row.addClass('table-success');
			} else {
				$statusCell.append('<i class="fas fa-hourglass-half text-warning" title="Chưa nhận đủ"></i>');
				$row.removeClass('table-success');
			}
		});
	}

	/* ------------------------------------------------------------ */

	if (saveAllBtn) {
		saveAllBtn.addEventListener('click', function() {
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

	/* ------------------------------------------------------------ */
	function updateReceivingQty(receivingSerialNo) {
		const rows = document.querySelectorAll('#orderTable tbody tr');
		let updated = false;

		rows.forEach((row) => {
			const itemCodeTd = row.children[0];  // ✅ Lấy cột Mã NVL
			const itemCode = itemCodeTd.textContent.trim();
			const input = row.querySelector('.receiving-qty');

			console.log("🟡 So sánh:", itemCode, "==?", receivingSerialNo.itemcode);

			if (itemCode === receivingSerialNo.itemcode && input) {
				const currentQty = parseFloat(input.value) || 0;
				const addedQty = parseFloat(receivingSerialNo.receivedQty) || 0;  // ✅ đúng tên key theo JSON Postman
				const newQty = currentQty + addedQty;

				input.value = newQty;

				updateReceivingStatusByRow($(input));

				row.style.backgroundColor = '#d1ecf1';
				setTimeout(() => row.style.backgroundColor = '', 5000);

				updated = true;
			}
		});

		if (!updated) {
			console.warn("⚠️ Không tìm thấy dòng nào khớp với itemCode:", receivingSerialNo.itemcode);
		}

		return updated;
	}
	/* ------------------------------------------------------------ */
	console.log("✅ receiving-scan-serial.js đã load xong");
});
