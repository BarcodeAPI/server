//
// BarcodeAPI.org, 2017-2025
// ui.js (Community)
//

// Time page load began
const timeStart = new Date();

/**
 * App Display Options
 */
const appConfig = {
	'showLinkMulti': true,
	'showRenderOptions': true,
	'showHiddenTypes': false,
	'userLanguage': 'en'
};

/**
 * App Supported Features
 */
const appFeatures = {

	// Firefox unsupported
	'copyImage': false,

	// Must be secure context
	'copyURL': window.isSecureContext,

	// Analytics event tracking
	'matomoTracking': {
		'enabled': false,
		'server': "",
		'appID': ""
	}
}

/**
 * Register handler to setup page when loaded.
 */
window.addEventListener("load", function() {

	// Check and load header
	if (document.getElementsByClassName("header")[0]) {
		initHeader();
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
	uiAddListener("header-support", actionContact)
}

/**
 * Initialize page footer.
 */
function initFooter() {

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
	if (!obj) {
		return;
	}
	obj.style.display = ((show) ? '' : 'none');
}

/**
 * Add an event listener to a UI element.
 */
function uiAddListener(elem, handler, event) {
	var obj = document.getElementsByClassName(elem)[0];
	if (obj) {
		event = (event) ? event : "click";
		obj.addEventListener(event, handler);
	}
}
