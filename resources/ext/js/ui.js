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
	'showLinkBulk': true,
	'showLinkMulti': true,
	'showLinkDecode': false,
	'showTokenCount': false,
	'showLimitsNotice': false,
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

	// Check and load analytics
	if (appFeatures.matomoTracking.enabled) {
		initAnalytics();
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

/**
 * Initialize analytics tracking.
 */
function initAnalytics() {

	var u = appFeatures.matomoTracking.server;

	var _paq = window._paq = window._paq || [];
	_paq.push(['trackPageView']);
	_paq.push(['enableLinkTracking']);
	_paq.push(['setTrackerUrl', u + 'matomo.php']);
	_paq.push(['setSiteId', appFeatures.matomoTracking.appID]);

	var js = document.createElement("script");
	js.src = u + 'matomo.js';
	js.async = true;
	document.head.appendChild(js);
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

	uiShowHide("notice-limits", appConfig.showLimitsNotice);
}

/**
 * Initialize page footer.
 */
function initFooter() {

	uiShowHide("footer-tokens", appConfig.showTokenCount);

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
function trackingEvent(category, action, event, value) {

	// Check if enabled and push to matomo
	if (appFeatures.matomoTracking.enabled) {
		window._paq.push(['trackEvent', category, action, event, value]);
	}
}
