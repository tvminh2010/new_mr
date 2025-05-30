function confirmAndUpdateStatus(el) {
    if (!confirm("Bạn có chắc muốn chuyển sang trạng thái 'Picking'?")) return false;

    const orderId = $(el).data("id");
    const row = $('tr[data-id="' + orderId + '"]');

    $.post("/order/update", { id: orderId }, function(response) {
        row.removeClass("bg-warning table-warning bg-success table-secondary")
           .addClass("table-warning");

        row.find(".status-text").html("<span>2. Picking...</span>");

        row.find(".status-cell").html(`
            <a href="/order/picking?id=${orderId}">
                <i class="fas fa-tasks icon-status" title="Đang nhặt hàng"></i>
            </a>
        `);

        showMessage("Cập nhật trạng thái thành công!", false);
    }).fail(function() {
        showMessage("Có lỗi xảy ra khi cập nhật!", true);
    });

    return false;
}
