/* ---------------------------------------------------------------------- */
function applyNetWeightToQty() {
	const netWeight = parseFloat($('#netWeight').val()) || 0;
	const targetItemCode = $('#itemCode').val().trim();

	if (!targetItemCode) return;

	let matched = false;

	$('#dataTableBody tr').each(function () {
		const $row = $(this);
		const itemCodeInRow = ($row.data('itemcode') || '').toString().trim().toUpperCase();

		if (itemCodeInRow === targetItemCode) {
			const $input = $row.find('input[name="soLuong[]"]');
			const currentQty = parseFloat($input.val()) || 0;
			const newQty = currentQty + netWeight;
			$input.val(newQty.toFixed(6));
			matched = true;
			return false; 
		}
	});

	if (!matched) {
		console.warn("Không tìm thấy dòng nào có itemCode =", targetItemCode);
	}
}
/* ---------------------------------------------------------------------- */
function fetchLatestWeight() {
	fetch('/hoantra-nvl/weight-latest')
		.then(res => res.text())
		.then(weight => {
			if (weight) {
				document.getElementById("weight").value = weight;
				calculateNetWeight();
			}
		});
}
let lastNetWeight = null;

function calculateNetWeight() {
    const weight = parseFloat(document.getElementById("weight").value) || 0;
    const coreWeight = parseFloat(document.getElementById("coreWeight").value) || 0;
    const netWeight = (weight - coreWeight).toFixed(6);
    document.getElementById("netWeight").value = netWeight;

	if (netWeight !== lastNetWeight) {
	       lastNetWeight = netWeight;
	       applyNetWeightToQty();
	}
}

/* ---------------------------------------------------------------------- */
$(document).ready(function() {
	$('#lineSelect').change(function() {
		var selectedLine = $(this).val();
		if (selectedLine) {
			$.ajax({
				url: '/ycnvl/by-line',
				type: 'GET',
				data: { line: selectedLine },
				success: function(data) {
					let $woSelect = $('#workorderSelect');
					$woSelect.empty().append('<option value="">--Workorder--</option>');
					data.forEach(function(wo) {
						$woSelect.append('<option value="' + wo + '">' + wo + '</option>');
					});

					// Reset model, plan, materials
					$('#modelText').text('');
					$('#planText').text('');
					$('#dataTableBody').empty();
				}
			});
		}
	});

	// Khi chọn Workorder thì lấy Model, Plan và danh sách materials
	$('#workorderSelect').change(function() {
		const selectedWo = $(this).val();
		if (!selectedWo) {
			$('#modelText').text('');
			$('#planText').text('');
			$('#dataTableBody').empty();
			return;
		}

		$.ajax({
			url: '/ycnvl/workorder-info',
			type: 'GET',
			data: { woNumber: selectedWo },
			success: function(data) {
				$('#modelText').text(data.model || '');
				$('#planText').text(data.plan || '');

				const materials = data.materials || [];
				const $tbody = $('#dataTableBody');
				$tbody.empty(); // clear table trước

				materials.forEach(function(item, index) {
					const itemName = item.itemName || item.itemname || 'Không rõ';

					const isZeroStockOrPlanZero = item.instock === 0 || item.qtyPlan === 0;
					const rowClass = isZeroStockOrPlanZero ? 'text-faded' : '';
					const readonlyAttr = isZeroStockOrPlanZero ? 'readonly' : '';

					const row = `
				        <tr class="${rowClass}" data-itemcategory="${item.category}" data-itemcode="${item.itemCode}" data-itemname="${itemName}">
				            <td class="align-middle small">${item.category || ''}</td>
				            <td class="align-middle small">${item.itemCode || ''}</td>
				            <td class="align-middle small">${itemName}</td>
				            <td class="align-middle small text-left">${item.donggoi || ''}</td>
				            <td class="align-middle small text-left">${item.qtyPlan || '0'}</td>
				            <td class="align-middle small text-left">${item.qtyReceive || ''}</td>
				            <td class="align-middle">
				                <div class="input-group input-group-sm">
				                    <input type="text" class="form-control form-control-sm" name="soLuong[]" ${readonlyAttr} value="${item.qtyrequest || ''}">
				                </div>
				            </td>
				        </tr>
				    `;
					$tbody.append(row);
				});


				if (data.model) {
					$('#middleFormSection').show();

					if (!window.weightIntervalStarted) {
						window.weightIntervalStarted = true;
						setInterval(fetchLatestWeight, 1000);
					}

					setTimeout(function() {
						$('#serialNo').focus();
					}, 100);
				}

			},
			error: function(xhr, status, error) {
				console.error("Lỗi khi lấy thông tin WorkOrder:", error);
				alert("Không lấy được dữ liệu WorkOrder!");
			}
		});
	});


	/** Lấy dữ liệu đã nhập và đẩy lên server bằng post */
	$('#dataForm').submit(function(e) {
		e.preventDefault();
		const woNumber = $('#workorderSelect').val();
		const dataToSend = [];

		$('#dataTableBody tr').each(function() {
			const $row = $(this);
			const itemCategory = $row.data('itemcategory');
			const itemCode = $row.data('itemcode');
			const itemName = $row.data('itemname');
			const qtyrequest = $row.find('input[name="soLuong[]"]').val();

			// Bỏ qua nếu không có mã hoặc không nhập số lượng
			if (itemCode && qtyrequest) {
				dataToSend.push({
					itemCategory: itemCategory,
					itemCode: itemCode,
					itemName: itemName,
					qtyrequest: qtyrequest
				});
			}
		});


		const payload = {
			woNumber: woNumber,
			items: dataToSend
		};

		// Gửi lên server bằng AJAX POST
		$.ajax({
			url: '/ycnvl/workorder-info',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify(payload),
			success: function(response) {
				$('#saveMessage')
					.text(response)
					.removeClass('text-danger')
					.addClass('text-success')
					.show();
				setTimeout(() => $('#saveMessage').fadeOut(), 7000);
			},
			error: function(xhr, status, error) {
				$('#saveMessage')
					.text('Lỗi khi gửi dữ liệu: ' + (xhr.responseText || error))
					.removeClass('text-success')
					.addClass('text-danger')
					.show();
				setTimeout(() => $('#saveMessage').fadeOut(), 7000);
				console.error(error);
			}
		});
	});
	/** End lấy dữ liệu đã nhập và đẩy lên server bằng post */


	/* ---------------------------------------------------------------------- */
	/** Xử lý phần hiển thị quét mã serial của hàng trả về */
	$('#serialNo').focus();
	// Xử lý khi người dùng nhấn Enter trong ô quét mã
	$('#serialNo').on('keypress', function(e) {
		if (e.which === 13) {
			e.preventDefault();
			submitSerialNo();
		}
	});

	$('#serialNo').on('blur', function() {
		submitSerialNo();
	});

	function submitSerialNo() {
		let serial = $('#serialNo').val().trim();
		let workOrderCode = $('#workorderSelect').val(); // 🔥 lấy WO hiện tại
		if (!serial) return;

		$.ajax({
			url: '/hoantra-nvl/serial-scan',
			method: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({ serialNo: serial, workOrderCode: workOrderCode }),
			success: function(res) {
				$('#itemCategory').val(res.category || '');
				$('#itemCode').val(res.itemCode || '');
				$('#itemName').val(res.itemName || '');
				$('#lotNo').val(res.lotNo || '');
				$('#vendor').val(res.vendor || '');
				$('#unit').val(res.unit || '');
				$('#coreType').val(res.coreType || '');
				$('#receivingDate').val(res.receivingDate || '');
				$('#coreWeight').val(res.coreWeight || '');
				$('#rate').val(res.rate || '');

				const isSuccess = res.messageType === 'success';
				const icon = isSuccess ? '✅' : '❌';

				$('#serialNo').val('').focus();
				$('#status-message')
					.html(`${icon} ${res.message}`)
					.removeClass("text-success text-danger")
					.addClass(isSuccess ? "text-success" : "text-danger")
					.show();
			},
			error: function(xhr) {
				let message = "Có lỗi xảy ra!";
				if (xhr.responseJSON && xhr.responseJSON.message) {
					message = xhr.responseJSON.message;
				}

				$('#status-message')
					.html(`❌ ${message}`)
					.removeClass("text-success text-danger")
					.addClass("text-danger")
					.show();
			}
		});
	}

	if (data.model) {
		$('#middleFormSection').show(); // model khác null thì hiển thị form
	}
	/* ------------------------------------------------------------------------- */
	//$('#netWeight').on('input', applyNetWeightToQty);
	//console.log($('#netWeight').length);
});




