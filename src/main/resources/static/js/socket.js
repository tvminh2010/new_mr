let stompClient = null;

/* ------------------------------------------------------------------- */
function applyNetWeightToQty() {
	const netWeight = parseFloat($('#netWeight').val()) || 0;
	const targetItemCode = $('#itemCode').val().trim();

	if (!targetItemCode) return;

	let matched = false;

	$('#dataTableBody tr').each(function() {
		const $row = $(this);
		const itemCodeInRow = ($row.data('itemcode') || '').toString().trim().toUpperCase();

		if (itemCodeInRow === targetItemCode) {
			const $input = $row.find('input[name="soLuong[]"]');
			const currentQty = parseFloat($input.val()) || 0;
			const newQty = currentQty + netWeight;
			$input.val(parseFloat(newQty.toFixed(6)));
			matched = true;
			return false;
		}
	});

	if (!matched) {
		console.warn("Không tìm thấy dòng nào có itemCode =", targetItemCode);
	}
}
/* ------------------------------------------------------------------- */

function connect() {
    const socket = new SockJS('/ws'); 
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/scale-weight', function (message) {
            const weight = message.body;
			const coreWeight = parseFloat(document.getElementById("coreWeight").value) || 0;
			const netWeight = parseFloat((weight - coreWeight).toFixed(6));
			document.getElementById("weight").value = parseFloat(weight);
			document.getElementById("netWeight").value = netWeight;
			document.getElementById("quantity").value = netWeight;
			applyNetWeightToQty();
        });
    });
}
/* ------------------------------------------------------------------- */
window.onload = connect;
