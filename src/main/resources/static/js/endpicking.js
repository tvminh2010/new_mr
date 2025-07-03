function endPicking() {
	if (!confirm("❗Bạn có chắc chắn muốn kết thúc nhặt hàng không?")) return;

	let hasInvalidQty = false;
	$("input[name^='pickingQty']").each(function () {
		const val = parseFloat($(this).val());
		if (isNaN(val) || val <= 0) {
			hasInvalidQty = true;
			return false;
		}
	});

	if (hasInvalidQty) {
		if (!confirm("⚠️ CHƯA NHẶT XONG.\nBạn vẫn muốn kết thúc nhặt hàng?")) {
			return;
		}
	}

	const orderId = $('#orderId').val(); 
	if (!orderId) {
		alert("❌ Không tìm thấy Order ID!");
		return;
	}

	$.ajax({
		url: '/orders/' + orderId + '/end-picking',
		type: 'POST',
		success: function () {
			alert("✅ Đã kết thúc nhặt hàng và chuyển trạng thái thành 'Đang giao'.");
			location.reload();
		},
		error: function (xhr) {
			alert("❌ Có lỗi xảy ra: " + xhr.responseText);
		}
	});
}



/* ---------------------------------------------------------- */
$(document).ready(function () {
	const endPickingOrderBtn = $('#endPickingOrder')[0];
	//endPickingOrderBtn.disabled = true;
	function updateEndPickingOrderBtnStatus() {
	       let hasValidInput = false;
	       $("input[name^='pickingQty']").each(function () {
	           const val = parseFloat($(this).val());
	           if (!isNaN(val) && val > 0) {
	               hasValidInput = true;
	               return false; 
	           }
	       });
	       endPickingOrderBtn.disabled = !hasValidInput;
	   }
	   
	$('#endPickingOrder').on('click', function () {
		let hasInvalidQty = false;
		$("input[name^='pickingQty']").each(function () {
			const val = parseFloat($(this).val());
			if (isNaN(val) || val <= 0) {
				hasInvalidQty = true;
				return false; 
			}
		});
		if (hasInvalidQty) {
			if (!confirm("⚠️ CHƯA NHẶT XONG.\nBạn vẫn muốn kết thúc nhặt hàng?")) {
				return; 
			}
		}
		const orderId = $('#orderId').val(); 
		$.ajax({
			url: '/orders/' + orderId + '/end-picking',
			type: 'POST',
			success: function () {
				alert("✅ Kết thúc nhặt hàng, đã chuyển sang trạng thái 'Đang giao'.");
				location.reload();
			},
			error: function (xhr) {
				alert("❌ Có lỗi xảy ra: " + xhr.responseText);
			}
		});
	});
	
	updateEndPickingOrderBtnStatus();
});
