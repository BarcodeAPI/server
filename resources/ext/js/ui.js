/**
 * App Display Options
 */
const appDisplay = {
	'about': false,
	'support': false,
	'tokenCount': false,
	'bulkPages': true,
	'limitsNotice': false,
	'renderOptions': true,
	'helpType': false,
	'helpManual': false,
	'showHidden': false
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

	uiAddListener("header-logo", actionHome);
	uiAddListener("action-email", actionContact);
	if (appDisplay.support) {
		uiAddListener("header-support", actionSupport)
	}
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

	uiAddListener("footer-docs-link", actionShowDocs);
}

/**
 * Called when a user should be sent home page.
 */
function actionHome() {
	window.location.href = "/index.html";
}

/**
 * Called when a user clicks contact via email.
 */
function actionContact() {
	window.location.href = "mailto:support@barcodeapi.org";
}

/**
 * Called when a user should be sent to the support page.
 */
function actionSupport() {
	window.location.href = "/support.html";
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

/**
 * Add an event listener to a UI element.
 */
function uiAddListener(elem, h, e) {
	e = (e) ? e : "click";
	var obj = document.getElementsByClassName(elem)[0];
	if (obj) {
		obj.addEventListener(e, h);
	}
}
