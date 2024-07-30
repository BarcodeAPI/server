/**
 * App Display Options
 */
const appDisplay = {
	'about': false,
	'tokenCount': false,
	'bulkPages': true,
	'limitsNotice': false,
	'renderOptions': false,
	'helpType': false,
	'helpManual': false
};

/**
 * App Supported Features
 */
const appFeatures = {
	// Firefox unsupported
	'copyImage': (navigator.userAgent.indexOf("Firefox") == -1),
	// Must be secure context
	'copyURL': window.isSecureContext
}

/**
 * Register handler to setup page when loaded.
 */
window.addEventListener("load", function() {

	// Check and load header
	if (document.getElementsByClassName("header")[0]) {
		initHeader();
	}

	// Check and load notice
	if (document.getElementsByClassName("notice")[0]) {
		initNotice();
	}

	// Check and load footer
	if (document.getElementsByClassName("footer")[0]) {
		initFooter();
	}
});

/**
 * Initialize page header.
 */
function initHeader() {

	document.getElementsByClassName("header-logo")[0].addEventListener('click', actionGoHome);
	document.getElementsByClassName("action-email")[0].addEventListener('click', actionContactUs);
}

/**
 * Initialize page notice.
 */
function initNotice() {

	uiShowHide("notice-limits", appDisplay.limitsNotice);
	uiShowHide("notice-tokens", appDisplay.tokenCount);
}

/**
 * Initialize page footer.
 */
function initFooter() {

	uiShowHide("footer-link", appDisplay.about);
	uiShowHide("footer-docs", appDisplay.helpManual);

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
	var obj = document.getElementsByClassName(elem)[0];
	if (obj) {
		obj.style.display = ((show) ? '' : 'none');
	}
}
