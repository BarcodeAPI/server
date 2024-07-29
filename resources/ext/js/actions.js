function initUI() {
	uiShowHide("footer-link", appOptions.display.about);
	uiShowHide("footer-docs", appOptions.display.helpManual);

	document.getElementsByClassName("header-logo")[0].addEventListener('click', actionGoHome);
	document.getElementsByClassName("action-email")[0].addEventListener('click', actionContactUs);
	document.getElementsByClassName("footer-docs-link")[0].addEventListener('click', actionShowDocs);
}

/**
 * Called when a user should be sent home page.
 */
function actionGoHome() {
	window.location = "/index.html";
}

/**
 * Called when a user should be sent to support page.
 */
function actionContactUs() {
	window.location.href = "mailto:support@barcodeapi.org";
}

/**
 * Called when the user should be shown the guide.
 */
function actionShowDocs() {
	window.location = '/api.html';
}

/**
 * Show or Hide UI elements based on configured options.
 */
function uiShowHide(elem, show) {
	document.getElementsByClassName(elem)[0].style.display = ((show) ? '' : 'none');
}
