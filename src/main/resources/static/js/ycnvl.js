$(document).ready(function () {
    // Khi chọn Line thì gọi API để đổ Workorder (đã có)
    $('#lineSelect').change(function () {
        var selectedLine = $(this).val();
        if (selectedLine) {
            $.ajax({
                url: '/ycnvl/by-line',
                type: 'GET',
                data: { line: selectedLine },
                success: function (data) {
                    let $woSelect = $('#workorderSelect');
                    $woSelect.empty().append('<option value="">--Workorder--</option>');
                    data.forEach(function (wo) {
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
	$('#workorderSelect').change(function () {
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
	        success: function (data) {
	            $('#modelText').text(data.model || '');
	            $('#planText').text(data.plan || '');

	            const materials = data.materials || [];
	            const $tbody = $('#dataTableBody');
	            $tbody.empty(); // clear table trước

				materials.forEach(function (item, index) {
				    const itemName = item.itemName || item.itemname || 'Không rõ';

				    const isZeroStockOrPlanZero = item.instock === 0 || item.qtyPlan === 0;
				    const rowClass = isZeroStockOrPlanZero ? 'text-faded' : '';
				    const readonlyAttr = isZeroStockOrPlanZero ? 'readonly' : '';

				    const row = `
				        <tr class="${rowClass}" data-itemcode="${item.itemCode}" data-itemname="${itemName}">
				            <td class="align-middle small">${item.category || ''}</td>
				            <td class="align-middle small">${item.itemCode || ''}</td>
				            <td class="align-middle small">${itemName}</td>
				            <td class="align-middle small text-left">${item.donggoi || ''}</td>
				            <td class="align-middle small text-left">${item.qtyPlan || '0'}</td>
				            <td class="align-middle small text-left">${item.qtyReceive || ''}</td>
				            <td class="align-middle small text-left">${item.instock || '0'}</td>
				            <td class="align-middle">
				                <div class="input-group input-group-sm">
				                    <input type="text" class="form-control form-control-sm" name="soLuong[]" ${readonlyAttr} value="${item.qtyrequest || ''}">
				                </div>
				            </td>
				        </tr>
				    `;
				    $tbody.append(row);
				});

	        },
	        error: function (xhr, status, error) {
	            console.error("Lỗi khi lấy thông tin WorkOrder:", error);
	            alert("Không lấy được dữ liệu WorkOrder!");
	        }
	    });
	});

	
	/** Lấy dữ liệu đã nhập và đẩy lên server bằng post */
	$('#dataForm').submit(function (e) {
	    e.preventDefault();
		const woNumber = $('#workorderSelect').val();
	    const dataToSend = [];

	    $('#dataTableBody tr').each(function () {
	        const $row = $(this);
	        const itemCode = $row.data('itemcode');
			const itemName = $row.data('itemname');
	        const qtyrequest = $row.find('input[name="soLuong[]"]').val();

	        // Bỏ qua nếu không có mã hoặc không nhập số lượng
	        if (itemCode && qtyrequest) {
	            dataToSend.push({
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
	        success: function (response) {
				$('#saveMessage')
	                .text(response)
	                .removeClass('text-danger')
	                .addClass('text-success')
	                .show();
					setTimeout(() => $('#saveMessage').fadeOut(), 7000);
	        },
	        error: function (xhr, status, error) {
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
	
});


