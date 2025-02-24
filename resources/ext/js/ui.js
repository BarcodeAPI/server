//
// BarcodeAPI.org
// ui.js
//


/**
 * App Display Options
 */
const appConfig = {
	'bulkPages': true,
	'tokenCount': true,
	'limitsNotice': true,
	'renderOptions': true,
	'showHidden': false,
	'logEvents': false,
	'trackingToken': false
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

// Used by GTag
const dataLayer = [];

/**
 * Register handler to setup page when loaded.
 */
window.addEventListener("load", function() {

	// Check and load analytics
	if (appConfig.trackingToken) {
		initGTagTracking();
	}

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

function initGTagTracking() {

	var tag = appConfig.trackingToken;

	gtag('js', new Date());
	gtag('config', tag);

	var gtagJS = document.createElement("script");
	gtagJS.src = "https://www.googletagmanager.com/gtag/js?" + tag;
	gtagJS.async = true;
	document.head.appendChild(gtagJS);
}

/**
 * Initialize page header.
 */
function initHeader() {

	uiAddListener("header-logo", actionHome);
	uiAddListener("action-email", actionContact);
	uiAddListener("header-support", actionSupport)
}

/**
 * Initialize page notice.
 */
function initNotice() {

	uiShowHide("notice-limits", appConfig.limitsNotice);
}

/**
 * Initialize page footer.
 */
function initFooter() {

	uiShowHide("footer-tokens", appConfig.tokenCount);

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

/**
 * Tracking event handler
 */
function trackingEvent(event, details) {
	if (appConfig.logEvents) {
		var msg = ("Event: " + event);
		if (details) {
			msg += (" :: " + JSON.stringify(details));
		}
		console.log(msg);
	}

	gtag("event", event, details);
}

/**
 * GTag analytics handler
 */
function gtag() {
	dataLayer.push(arguments);
}
