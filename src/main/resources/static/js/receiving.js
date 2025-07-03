
/* -------------------- kết thúc nhận hàng ----------------- */
function endReceiving() {
  if (!selectedOrderId) {
    alert("❌ Không tìm thấy Order để kết thúc!");
    return;
  }

  if (!confirm("📦 Bạn có chắc chắn muốn kết thúc nhận hàng?\nDữ liệu sẽ được cố định và không thể sửa sau đó!")) {
    return;
  }

  const $messageDiv = $('#receiving-complete-message');

  // Gửi yêu cầu kết thúc
  $.ajax({
    url: '/receiving/complete',
    method: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({ orderId: selectedOrderId }),

    beforeSend: function () {
      $messageDiv
        .text("⏳ Đang gửi yêu cầu kết thúc nhận hàng...")
        .removeClass()
        .addClass('text-left mt-2 text-info')
        .show();
    },

    success: function (response) {
      $messageDiv
        .text("✅ " + response)
        .removeClass()
        .addClass('text-left mt-2 text-success');

      // ✅ Reload sau 1.5 giây để cập nhật danh sách order
      setTimeout(() => location.reload(), 1500);
    },

    error: function (xhr) {
      console.error("❌ Error response:", xhr);
      $messageDiv
        .text("❌ Lỗi khi kết thúc nhận hàng! Vui lòng thử lại.")
        .removeClass()
        .addClass('text-left mt-2 text-danger');
    }
  });
}

/*
function endReceiving() {
    if (confirm("Bạn có chắc chắn muốn kết thúc nhận hàng? Dữ liệu sẽ được cố định.")) {
      alert("✔️ Đã gửi yêu cầu kết thúc nhận hàng!");
      $.ajax({
        url: '/receiving/complete',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ orderId: selectedOrderId }),
		success: function (response) {
		  $('#receiving-complete-message')
		    .text(response)
		    .removeClass()
		    .addClass('text-left mt-2 text-success')
		    .show();
		},
		error: function () {
		  $('#receiving-complete-message')
		    .text("❌ Lỗi kết thúc nhận hàng!")
		    .removeClass()
		    .addClass('text-left mt-2 text-danger')
		    .show();
		}
      });
    }
  }
  */
/* ---------------------------------------------------------- */

$(document).ready(function() {
	
	/* ------------ Tự động chuyển tab sang quét để nhận hàng */
	var urlParams = new URLSearchParams(window.location.search);
	var orderId = urlParams.get('id');
	if (orderId) {
		activateTab('scan');
	}

	function activateTab(tabId) {
		$('#pickingTabs a[href="#' + tabId + '"]').tab('show');
	}
	console.log("✅ receiving.js đã load xong");
});
