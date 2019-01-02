//
// IMPORTANT:
// You must update Url.RESOURCES_VERSION each time whenever you're modified this file!
//

function initPage(importSeriesSaleUrl) {
	if (importSeriesSaleUrl != null) {
		$('#import-series-sale-form').on('submit', function sendImportRequest(event) {
			event.preventDefault();
			
			var url = $('#series-sale-url').val();
			if (url == null) {
				return;
			}
			
			// XXX: handle errors gracefully
			// XXX: handle CSP
			// XXX: pass CSRF-token
			// XXX: disable form during submission
			// XXX: add JS code for prototype
			var data = JSON.stringify({
				url: url
			});
			$.ajax({
				url: importSeriesSaleUrl,
				method: 'POST',
				contentType: 'application/json; charset=UTF-8',
				data: data
					
			}).done(function populateAddSeriesSaleForm(result) {
				var urlField = $('#series-sale-url');
				var url = urlField.val();
				urlField.val('');
				
				populateTransactionDateWithTodayDate();
				if (result.sellerId != null) {
					$('#seller').val(result.sellerId);
				}
				$('#url').val(url);
				$('#price').val(result.price);
				$('#currency').val(result.currency);
			});
		});
	}
}

function populateTransactionDateWithTodayDate() {
	var today = DateUtils.formatDateToDdMmYyyy(new Date());
	$('#date').val(today);
}
