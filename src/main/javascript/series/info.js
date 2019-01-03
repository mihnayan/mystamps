//
// IMPORTANT:
// You must update Url.RESOURCES_VERSION each time whenever you're modified this file!
//

function initPage(importSeriesSaleUrl, csrfHeaderName, csrfTokenValue) {
	if (importSeriesSaleUrl != null) {
		$('#import-series-sale-form').on('submit', function sendImportRequest(event) {
			event.preventDefault();
			
			var url = $('#series-sale-url').val();
			if (url == null) {
				return;
			}
			
			disableImportSeriesSaleForm();
			
			// XXX: handle errors gracefully
			// XXX: add JS code for prototype
			var data = JSON.stringify({
				url: url
			});
			var headers = {};
			headers[csrfHeaderName] = csrfTokenValue;
			
			$.ajax({
				url: importSeriesSaleUrl,
				method: 'POST',
				contentType: 'application/json; charset=UTF-8',
				headers: headers,
				data: data
					
			}).always(function enableSubmitButton() {
				enableImportSeriesSaleForm();
			
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

function disableImportSeriesSaleForm() {
	$('#series-sale-submit-btn').prop('disabled', true);
	$('#series-sale-url').prop('disabled', true);
}

function enableImportSeriesSaleForm() {
	$('#series-sale-submit-btn').prop('disabled', false);
	$('#series-sale-url').prop('disabled', false);
}

function populateTransactionDateWithTodayDate() {
	var today = DateUtils.formatDateToDdMmYyyy(new Date());
	$('#date').val(today);
}
