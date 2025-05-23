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
        var selectedWo = $(this).val();
        if (selectedWo) {
            $.ajax({
                url: '/ycnvl/workorder-info',
                type: 'GET',
                data: { woNumber: selectedWo },
                success: function (data) {
                    $('#modelText').text(data.model || '');
                    $('#planText').text(data.plan || '');

                    // Xử lý materials
                    let materials = data.materials || [];
                    let $tbody = $('#dataTableBody');
                    $tbody.empty(); // clear table trước

                    materials.forEach(function (item) {
						const isZeroStockOrPlanZero = item.instock === 0 || item.plan === 0;
						const rowClass = isZeroStockOrPlanZero ? 'text-faded' : '';
						const readonlyAttr = (item.instock === 0 || item.plan === 0) ? 'readonly' : '';
							
                        const row = `
                             <tr class="${rowClass}">
                                <td class="align-middle small">${item.category}</td>
								<td class="align-middle small">${item.itemCode}</td>
                                <td class="align-middle small">${item.model}</td>
                                <td class="align-middle small text-left">${item.donggoi}</td>
                                <td class="align-middle small text-left">${item.plan || '0'}</td>
                                <td class="align-middle small text-left">${item.receive || ''}</td>
                                <td class="align-middle small text-left">${item.instock || '0'}</td>
                                <td class="align-middle">
                                    <div class="input-group input-group-sm">
                                        <input type="text" class="form-control form-control-sm"  name="soLuong[]" ${readonlyAttr} >
                                    </div>
                                </td>
                            </tr>
                        `;
                        $tbody.append(row);
                    });
                }
            });
        } else {
            $('#modelText').text('');
            $('#planText').text('');
            $('#dataTableBody').empty();
        }
    });
});
