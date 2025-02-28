//
// BarcodeAPI.org, 2017-2025
// index.js // index.html
//

/**
 * User Interface App Options
 */
const appState = {
	'selected': false,
	'current': false,
	'drawerOpen': false,
	'optionsOpen': false,
	'types': {}
};

const appOptions = {
	'language': 'en',
	'apiKey': false,
	'genDelay': 415,
	'default': {
		'colorFG': "000000",
		'colorBG': "FFFFFF",
		'dpi': 150,
		'height': 25,
		'hrt': 'bottom'
	},
	'render': {
		'colorFG': "000000",
		'colorBG': "FFFFFF",
		'dpi': 150,
		'height': 25,
		'hrt': 'bottom'
	},
	'trim': {
		'before': (window.localStorage.getItem("trimBefore") != "false"),
		'after': (window.localStorage.getItem("trimAfter") != "false")
	}
};

/**
 * Called when the page is initially loaded.
 */
async function init() {

	// Call our method when the URL hash changes.
	window.onhashchange = loadSelectedType;

	// Load previously configured API key
	appOptions.apiKey = window.localStorage.getItem("apiKey");

	// Load supported types
	loadBarcodeTypes();

	// Hide UI elements based on config
	uiShowHide("app-setup-more", //
		(appConfig.showLinkMulti || appConfig.showLinkBulk || appConfig.showLinkDecode));

	uiShowHide("app-link-multi", appConfig.showLinkMulti);
	uiShowHide("app-link-bulk", appConfig.showLinkBulk);
	uiShowHide("app-link-decode", appConfig.showLinkDecode);

	// Hide UI elements based on browser support
	uiShowHide("action-copy", appFeatures.copyImage);
	uiShowHide("action-url", appFeatures.copyURL);

	// Update states from appOptions
	document.getElementById("option-api-key").value = appOptions.apiKey;
	document.getElementById("option-trim-before").checked = appOptions.trim.before;
	document.getElementById("option-trim-after").checked = appOptions.trim.after;

	// Setup event handlers
	uiAddListener("app-setup-type", toggleOpenBarcodeTypes);
	uiAddListener("app-options-link", toggleShowRenderOptions);
	uiAddListener("barcode-text-input", checkInput, 'keyup');

	// Setup barcode action handler
	uiAddListener("action-clear", actionClearInput);
	uiAddListener("action-keyboard", actionShowKeyboard);
	uiAddListener("action-print", actionPrintImage);
	uiAddListener("action-copy", actionCopyImage);
	uiAddListener("action-url", actionCopyURL);
	uiAddListener("action-download", actionDownloadImage);

	// Close the keyboard when navigating away
	window.onbeforeunload = actionCloseKeyboard;

	// Log tracking event
	var setupMillis = ((new Date()) - timeStart);
	trackingEvent("AppEvents", "AppLoad", "Main", setupMillis);
}

/**
 * Calls the /types/ endpoint to load all supported formats.
 */
async function loadBarcodeTypes() {

	// Fetch /types/ endpoint
	const url = location.origin + "/types/";
	appState.types = await fetch(url)
		.then((response) => {
			return response.json();
		}).then((data) => {

			data.sort((a, b) => {
				if (a.name < b.name) {
					return -1;
				}
				if (a.name > b.name) {
					return 1;
				}
				return 0;
			});

			return data;
		});

	// Render loaded config
	renderTypeSelection();

	// Load the selected type
	loadSelectedType();
}

/**
 * Create list of supported barcode types.
 */
function renderTypeSelection() {

	const menu = document.getElementsByClassName("app-setup-types")[0];

	// Loop all supported types
	for (t in appState.types) {
		var type = appState.types[t];

		// Skip if not show
		if (!type.show && !appConfig.showHiddenTypes) {
			continue;
		}

		// Create type entry
		var node = document.createElement("a");
		node.setAttribute('rel', 'tooltip');
		node.setAttribute('id', "type-" + type.name);
		node.setAttribute('onclick', 'setType(\'' + type.targets[0] + '\')');
		node.innerHTML = type.display;

		// Build tooltip element
		addTooltip(node, appState.types[t].description[appOptions.language]);

		// Add to menu
		menu.appendChild(node);
	}
}

/**
 * Called each time we should read the hash of the URL.
 */
function loadSelectedType() {

	// Set to 'auto' if not set
	if (location.hash.length <= 1) {
		location.replace('#auto');
		return;
	}

	// Get current hash ( minus # )
	var hash = location.hash.substring(1);

	// Remove the 'active' class from all topnav objects
	var topnav = document.getElementsByClassName("app-setup-types")[0];
	for (var x in topnav.childNodes) {
		var classList = topnav.childNodes[x].classList;
		if (classList != null) {
			classList.remove("active");
		}
	}

	// Lookup in supported type map
	const codeType = getType(hash);
	appState.selected = codeType;

	// Get element for selected hash
	var selected = document.getElementById(//
		"type-" + ((codeType) ? codeType.name : "auto"));

	// Mark selected item as active
	selected.classList.add("active");

	// Update type dropdown selection
	document.getElementById("app-setup-type-picker").innerHTML = (codeType) ? codeType.display : 'Auto';

	// Update text field regex
	var text = document.getElementById("text");
	text.setAttribute("pattern", (codeType) ? codeType.pattern : '.*');

	// Show non-printing keyboard for supported formats
	uiShowHide("action-keyboard", (codeType) ? codeType.nonprinting : false);

	// Render options menu
	renderOptions(codeType);

	// Regenerate the code
	var url = buildBarcodeURL();
	if (url) {
		updateBarcodeImage(url);
	}

	// Focus text input
	text.focus();

	// Log tracking event
	trackingEvent("AppMain", "TypeChange", //
		(codeType) ? codeType.name : "Auto");
}

function renderOptions(type) {

	// Close drawer
	showRenderMenu(false);

	// Determine if showing options
	var showOptions = (type) && //
		(appConfig.showRenderOptions && //
			Object.keys(type.options).length);

	// Show / hide menu
	uiShowHide("app-setup-options", showOptions);
}

/**
 * Called when one of the UI option fields is changed.
 */
function optionsChange() {

	// Update user api key
	appOptions.apiKey = document.getElementById("option-api-key").value;
	window.localStorage.setItem("apiKey", appOptions.apiKey);

	// Parse text trimming options
	appOptions.trim.before = document.getElementById("option-trim-before").checked;
	window.localStorage.setItem("trimBefore", (appOptions.trim.before) ? "true" : false);
	appOptions.trim.after = document.getElementById("option-trim-after").checked;
	window.localStorage.setItem("trimAfter", (appOptions.trim.after) ? "true" : false);

	// Parse render options (DPI)
	var inDPI = document.getElementById("option-dpi");
	if (inDPI.checkValidity()) {
		appOptions.render.dpi = inDPI.value;
	} else {
		appOptions.render.dpi = appOptions.default.dpi;
		inDPI.value = appOptions.render.dpi;
	}

	// Parse render options (Height)
	var inHeight = document.getElementById("option-height");
	if (inHeight.checkValidity()) {
		appOptions.render.height = inHeight.value;
	} else {
		appOptions.render.height = appOptions.default.height;
		inHeight.value = appOptions.render.height;
	}

	// Parse render options (Color FG)
	var inFG = document.getElementById("option-color-fg");
	if (inFG.checkValidity()) {
		appOptions.render.colorFG = inFG.value;
	} else {
		appOptions.render.colorFG = appOptions.default.colorFG;
		inFG.value = appOptions.render.colorFG;
	}

	// Parse render options (Color BG)
	var inBG = document.getElementById("option-color-bg");
	if (inBG.checkValidity()) {
		appOptions.render.colorBG = inBG.value;
	} else {
		appOptions.render.colorBG = appOptions.default.colorBG;
		inBG.value = appOptions.render.colorBG;
	}

	// Parse render options (Text)
	var inHRT = document.getElementById("option-hrt");
	if (inHRT.checkValidity()) {
		appOptions.render.hrt = inHRT.value;
	} else {
		appOptions.render.hrt = appOptions.default.hrt;
		inHRT.value = appOptions.render.hrt;
	}

	checkInput();
}

/**
 * Called each time we should generate a new barcode.
 */
function buildBarcodeURL() {

	// Get the requested type
	var type = location.hash.substring(1);
	var codeType = getType(type);

	// Get the requested text
	const textInput = document.getElementById("text");
	let text = textInput.value;

	// Set default text
	if (text === "") {
		text = (codeType == null) ? "Try Me!" : codeType.example;
	}

	// Check trimming 
	var before = text;
	var trimmed = false;

	// Trim before
	if (appOptions.trim.before) {
		text = text.trimLeft();
		trimmed = (text != before);
	}

	// Trim after
	if (appOptions.trim.after) {
		text = text.trimRight();
		trimmed = (text != before);
	}

	// Rerender if trimmed
	if (trimmed) {
		textInput.value = text;
		return buildBarcodeURL();
	}

	// The API target
	var url = //
		location.origin + //
		"/api" + //
		"/" + type + //
		"/" + encodeURIComponent(text) + //
		"?" + buildOptionsString();

	return url;
}

/**
 * Update the barcode image with the specified URL.
 */
function updateBarcodeImage(url) {

	// Skip if unchanged
	if (url && appState.current == url) {
		return;
	}

	// Update current URL
	appState.current = url;

	// Default request headers
	var options = {
		method: "GET",
		cache: "no-store",
		headers: {
			"Accept": "image/png",
		}
	};

	// If user has API key
	if (appOptions.apiKey) {

		// Use API key in Authorization header
		options.headers.Authorization = ("Token=" + appOptions.apiKey);
	}

	// Request the image
	fetch(url, options)
		.then(response => {

			// Upate token count if not cached
			var tokens = response.headers.get('x-ratelimit-tokens');
			tokens = (tokens == -1) ? "Unlimited" : tokens;
			document.getElementById("barcode_tokens").innerHTML = tokens;

			// Update learn more link
			var codeType = response.headers.get('x-barcode-type');
			var displayType = codeType.replace("_", " ");
			var linkType = codeType.replace("_", "");

			// Update learn more link
			var link = document.getElementsByClassName("link-more")[0];
			link.innerHTML = "Learn more about " + displayType + " barcodes!";
			link.href = "/type.html#" + linkType;

			// Update the image blob
			response.blob().then(blob => {
				document.getElementById('barcode_output').src = URL.createObjectURL(blob);
			});
		});

	// Log tracking event
	trackingEvent("AppMain", "Generate");
}

/**
 * Validate user input before rendering.
 */
function checkInput() {

	// Fail if invalid input
	const textInput = document.getElementById("text");
	if (!textInput.checkValidity()) {
		return false;
	}

	// Call delayed update
	delayUpdateBarcode();
}

/**
 * Delay generation of Barcode
 * Allows typing into text field without requesting every change.
 */
function delayUpdateBarcode() {

	// Calculate new image URL
	const url = buildBarcodeURL();

	// Do nothing if unchanged
	if (!url || (appState.current == url)) {
		return;
	}

	// Delay for configured interval
	setTimeout(function() {

		// Do nothing if still changing
		if (buildBarcodeURL() != url) {
			return;
		}

		// Render the URL
		updateBarcodeImage(url);

	}, appOptions.genDelay);
}

/**
 * Build the URL options string for requesting a barcode from the API server.
 * Adds a webapp parameter for tracking purposes.
 * Optionally adds parameters if they are non-default.
 */
function buildOptionsString() {

	var options = "";

	// Override Color (FG)
	if (appOptions.render.colorFG != appOptions.default.colorFG) {
		options += "&fg=" + appOptions.render.colorFG;
	}

	// Override Color (BG)
	if (appOptions.render.colorBG != appOptions.default.colorBG) {
		options += "&bg=" + appOptions.render.colorBG;
	}

	// Override DPI
	if (appOptions.render.dpi != appOptions.default.dpi) {
		options += "&dpi=" + appOptions.render.dpi;
	}

	// Overrride Height
	if (appOptions.render.height != appOptions.default.height) {
		options += "&height=" + appOptions.render.height;
	}

	// Overrride Human Readable
	if (appOptions.render.hrt != appOptions.default.hrt) {
		options += "&text=" + appOptions.render.hrt;
	}

	return options;
}

/**
 * Change the view state of the type dropdown menu.
 */
function showTypesMenu(show) {

	if (show && !appState.drawerOpen) {
		appState.drawerOpen = true;
		document.getElementsByClassName("app-setup-type")[0].classList.add("open");
		document.getElementsByClassName("app-setup-types")[0].classList.add("open");
	} else {
		appState.drawerOpen = false;
		document.getElementsByClassName("app-setup-type")[0].classList.remove("open");
		document.getElementsByClassName("app-setup-types")[0].classList.remove("open");
	}
}

/**
 * Toggle the view state of the type dropdown menu.
 */
function toggleOpenBarcodeTypes() {

	// Toggle types dropdown state
	showTypesMenu(!appState.drawerOpen);
}

/**
 * Change the view state of the render options dropdown menu.
 */
function showRenderMenu(show) {

	if (show && !appState.optionsOpen) {
		appState.optionsOpen = true;
		document.getElementsByClassName("app-options-link")[0].classList.add("open");
		document.getElementsByClassName("app-barcode-options")[0].classList.add("open");
	} else {
		appState.optionsOpen = false;
		document.getElementsByClassName("app-options-link")[0].classList.remove("open");
		document.getElementsByClassName("app-barcode-options")[0].classList.remove("open");
	}
}

/**
 * Toggle the view state of the render options dropdown menu.
 */
function toggleShowRenderOptions() {

	// Determine opposite state
	var stateNew = !appState.optionsOpen;

	// Set new state
	showRenderMenu(stateNew);

	if (stateNew) {

		// Log tracking event
		trackingEvent("AppMain", "Options");
	}
}

/**
 * Called when the barcode tet input should be cleared.
 */
function actionClearInput() {
	var text = document.getElementById("text");

	text.value = "";
	updateBarcodeImage(buildBarcodeURL());
	text.focus()
}

/**
 * Called when the special character keyboard should be shown.
 */
function actionShowKeyboard() {

	var kbd = document.appKeyboard;
	if (typeof kbd === 'undefined' || kbd.closed) {
		kbd = createKeyboard();
	} else {
		kbd.focus();
	}

	// Log tracking event
	trackingEvent("AppMain", "Keyboard");
}

/**
 * Called when the special character keyboard should be closed.
 */
function actionCloseKeyboard() {
	if (document.appKeyboard) {
		document.appKeyboard.close();
	}
}

/**
 * Called when a user requests the barcode be printed.
 */
function actionPrintImage() {

	var content = document.getElementsByClassName("barcode_wrapper")[0].innerHTML;

	w = window.open();
	w.document.write(content);
	w.print();
	w.close();

	// Log tracking event
	trackingEvent("AppMain", "Print");
}

/**
 * Copy a link to the barcode to the user clipboard.
 */
function actionCopyURL() {

	// Fail if unsupported
	if (!appFeatures.copyURL) {
		console.log("Browser unsupported.");
		return false;
	}

	// Copy to the clipboard
	navigator.clipboard.writeText(appState.current);

	// Show copy popup
	const copyTextMessage = document.getElementById("message");
	copyTextMessage.setAttribute("class", "message-fade");

	// Hide popup after delay
	setTimeout(function() {
		copyTextMessage.setAttribute("class", "");
	}, 2500);
}

/**
 * Copy the barcode image to the user clipboard.
 */
async function actionCopyImage() {

	// Fail if unsupported
	if (!appFeatures.copyImage) {
		console.log("Browser unsupported.");
		return false;
	}

	try {
		const fetched = await fetch(appState.current);
		const blobInput = await fetched.blob();
		const clipboardItemInput = new ClipboardItem({ 'image/png': blobInput });
		await navigator.clipboard.write([clipboardItemInput]);
	} catch (e) {
		console.log("Failed to copy image.");
		console.log(e);
	}

	// Log tracking event
	trackingEvent("AppMain", "Copy");
}

/**
 * Download the image and save it as a file.
 */
function actionDownloadImage() {

	window.open(appState.current, '_blank');

	// Log tracking event
	trackingEvent("AppMain", "Download");
}

/**
 * Redirect the user to the nonprinting page.
 */
function actionNonprintingHelp() {

	window.location = "/nonprinting.html";
}

/**
 * Get a barcode type object from a target..
 */
function getType(code) {

	// Loop all supported types
	for (let i in appState.types) {
		let type = appState.types[i];

		// Loop all targets
		for (let t in type.targets) {

			// Check if target matches
			if (type.targets[t] === code) {

				return type;
			}
		}
	}

	return null;
}


/**
 * Set the selected barcode type.
 */
function setType(type) {

	location.replace('#' + type);
	showTypesMenu(false);
}

/**
 * Calculates the offset for a tooltip.
 */
function getTooltipOffset(elem) {
	var offsetLeft = 0, offsetTop = 0;
	do {
		if (!isNaN(elem.offsetLeft)) {
			offsetLeft += elem.offsetLeft;
			offsetTop += elem.offsetTop;
		}
	} while (elem = elem.offsetParent);

	return { left: offsetLeft, top: offsetTop };
}

/**
 * Adds the tooltip to a barcode type element.
 */
function addTooltip(target, message) {

	// Set event listeners
	target.addEventListener("mouseleave", removeTooltip);
	target.addEventListener("mouseenter", function() {

		// Create the tooltip element
		var tooltip = document.createElement("div");
		tooltip.id = "tooltip";
		tooltip.innerHTML = message;
		document.body.appendChild(tooltip);

		// Calculate position of the tooltip
		var offsets = getTooltipOffset(target);
		var posTop = offsets.top - tooltip.offsetHeight - 10;
		var posLeft = offsets.left + (target.offsetWidth / 2) - (tooltip.offsetWidth / 2);

		// Fix if off-screen left
		if (posLeft < 0) {
			posLeft = offsets.left + target.offsetWidth / 2 - 20;
			tooltip.classList.add("left");
		}

		// Fix if off-screen right
		if ((posLeft + tooltip.offsetWidth) > window.innerWidth) {
			posLeft = offsets.left - tooltip.offsetWidth + target.offsetWidth / 2 + 20;
			tooltip.classList.add("right");
		}

		// Set position of tooltip
		tooltip.style.left = (posLeft + "px");
		tooltip.style.top = (posTop + "px");
	});
}

/**
 * Remove the tooltip when done.
 */
var removeTooltip = function() {
	document.body.removeChild(//
		document.querySelector("#tooltip"));
}

/**
 * Add a character to the current input text.
 * 
 * This is called by the Keyboard window.
 */
function addCharacter(text) {

	document.getElementById("text").value += text;
	delayUpdateBarcode();
}

/**
 * Called when a new keyboard window should be created.
 */
function createKeyboard() {
	document.appKeyboard = window.open("keyboard.html", "Keyboard",
		"width=700px,height=425px,menubar=0,status=0,scrollbars=0");
	return document.appKeyboard;
}
