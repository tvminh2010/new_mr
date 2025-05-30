$(document).ready(function() {
	const serialInput = $('input[name="scannedSerial"]');
	serialInput.focus();
	serialInput.on('keypress', function(e) {
		if (e.which === 13) {
			e.preventDefault();
			$('#saveButton').click(); // Giả lập click nút "Save"
		}
	});
	$('#saveButton').on('click', function() {
		// Xử lý tùy ý (AJAX hoặc submit form)
		console.log("Serial vừa quét:", serialInput.val());
		serialInput.val('').focus();
	});
});
