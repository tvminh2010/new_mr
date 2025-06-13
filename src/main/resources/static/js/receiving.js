$(document).ready(function() {
	var urlParams = new URLSearchParams(window.location.search);
	var orderId = urlParams.get('id');
	if (orderId) {
		activateTab('scan');
	}

	function activateTab(tabId) {
		$('#pickingTabs a[href="#' + tabId + '"]').tab('show');
	}
});
