//
// BarcodeAPI.org
// index.js
//

/**
 * Trim options for pasting text
 */
const renderOptions = {
	'trimBefore': (window.localStorage.getItem("trimBefore") != "false"),
	'trimAfter': (window.localStorage.getItem("trimAfter") != "false"),
	'colorFG': "000000",
	'colorBG': "FFFFFF"
};

/**
 * User-Agent string
 */
const sUsrAg = navigator.userAgent;

/**
 * Call our method when the URL hash changes.
 */
window.onhashchange = loadHash;

/**
 * Called each time we should read the hash of the URL.
 */
function loadHash() {

	// Get current hash ( minus # )
	var hash = location.hash.substring(1);

	// Remove the 'active' class from all topnav objects
	var topnav = document.getElementById("topnav");
	for (var x in topnav.childNodes) {
		var classList = topnav.childNodes[x].classList;
		if (classList != null) {
			classList.remove("active");
		}
	}

	// Get element for selected hash
	var selected = document.getElementById("type-" + hash);

	// If no element for type
	if (selected == null) {

		// Default to AUTO
		location.hash = "auto";
		return;
	}

	// Mark selected item as active
	selected.setAttribute("class", "active");

	// Regenerate the code
	genCode();
}

/**
 * Create list of supported barcode types
 */
function createBarcodeTypes(types) {
	const menu = document.getElementById("topnav");

	for (t in types) {
		var node = document.createElement("a");
		node.setAttribute('id', "type-" + types[t].target);
		node.setAttribute('rel', 'tooltip');
		node.classList.add('top');
		node.innerHTML = types[t].name;
		node.setAttribute('onclick', 'setType("' + types[t].target + '")');
		node.setAttribute('title', types[t].description);
		menu.appendChild(node);
	}
	addTooltips();
}

/**
 * Delay generation of Barcode
 * Allows typing into text field without requesting every change.
 */
function delayGenCode() {
	const textInput = document.getElementById("text");
	const textValue = textInput.value;

	setTimeout(function() {
		if (textInput.value == textValue) {
			genCode();
		}
	}, 350);
}

function setupOptions() {
	document.getElementById("option-trim-before").checked = renderOptions.trimBefore;
	document.getElementById("option-trim-after").checked = renderOptions.trimAfter;
}

/**
 * Called when one of the UI options field is changed.
 */
function optionsChange() {

	// parse text trimming options
	renderOptions.trimBefore = document.getElementById("option-trim-before").checked;
	window.localStorage.setItem("trimBefore", (renderOptions.trimBefore) ? "true" : false);
	renderOptions.trimAfter = document.getElementById("option-trim-after").checked;
	window.localStorage.setItem("trimAfter", (renderOptions.trimAfter) ? "true" : false);


	var inFG = document.getElementById("option-color-fg");
	if (inFG.checkValidity()) {
		renderOptions.colorFG = inFG.value;
	}

	var inBG = document.getElementById("option-color-bg");
	if (inBG.checkValidity()) {
		renderOptions.colorBG = inBG.value;
	}

	// regenerate the barcode
	genCode();
}

/**
 * Called each time we should generate a new barcode.
 * 
 * @returns
 */
function genCode() {

	// The API target
	var url = location.origin + "/api";

	// Get the requested type
	var type = location.hash.substring(1);
	var codeType = getCode(type);

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
	if (renderOptions.trimBefore) {
		text = text.trimLeft();
		trimmed = (text != before);
	}

	// Trim after
	if (renderOptions.trimAfter) {
		text = text.trimRight();
		trimmed = (text != before);
	}

	// Rerender if trimmed
	if (trimmed) {
		textInput.value = text;
		genCode();
		return;
	}

	// Fail if invalid.
	if (!textInput.checkValidity()) {
		return;
	}

	// Build URL with type
	url = url + "/" + type;

	// Build URL with encoded request
	url += "/" + encodeURIComponent(text);

	// Add options
	url += "?fg=" + renderOptions.colorFG + "&bg=" + renderOptions.colorBG;

	// Update download button
	document.getElementById("barcode_download_button").setAttribute("href", url);

	if (sUsrAg.indexOf("Firefox") === -1) {
		document.getElementById("barcode_download_button").setAttribute("download", url);
	}
	document.getElementById("barcode_image_link").setAttribute("value", url);

	// Update IMG element source
	document.getElementById("barcode_output").src = url;
}

function printCode() {

	var content = document.getElementById("barcode_wrapper").innerHTML;

	w = window.open();
	w.document.write(content);
	w.print();
	w.close();
}

var isOpen = false;

function closeMenu() {
	const dropdown = document.getElementById("select-type");
	const menu = document.getElementById("topnav");
	dropdown.setAttribute("class", "select-type");
	menu.setAttribute("class", "barcode-types");
	isOpen = false;
}

function toggleOpenBarcodeTypes() {

	const dropdown = document.getElementById("select-type");
	const menu = document.getElementById("topnav");
	isOpen = !isOpen;

	if (isOpen) {
		dropdown.setAttribute("class", "select-type open");
		menu.setAttribute("class", "barcode-types open");
	} else {
		dropdown.setAttribute("class", "select-type");
		menu.setAttribute("class", "barcode-types");
	}
}

function toggleShowRenderOptions() {

	const menu = document.getElementById("barcode-options-input");
	menu.style.display = (menu.style.display != "grid") ? "grid" : "none";
}

function copyBarcodeLink() {

	/* Get the text field */
	const copyText = document.getElementById("barcode_image_link");
	const copyTextMessage = document.getElementById("message");
	copyTextMessage.setAttribute("class", "message-fade");

	/* Select the text field */
	copyText.select();
	copyText.setSelectionRange(0, 99999); /* For mobile devices */

	/* Copy the text inside the text field */
	document.execCommand("copy");

	setTimeout(function() {
		copyTextMessage.setAttribute("class", "");
	}, 2500);
}

async function loadBlob(fileName) {

	const fetched = await fetch(fileName);
	return await fetched.blob();
}

async function copyImageToClipboard() {

	const url = document.getElementById("barcode_image_link").getAttribute("value");

	if (!sUsrAg.indexOf("Firefox") > -1) {

		try {
			const blobInput = await loadBlob(url);
			const clipboardItemInput = new ClipboardItem({ 'image/png': blobInput });
			await navigator.clipboard.write([clipboardItemInput]);
		} catch (e) {
			console.log("Failed to copy image.");
			console.log(e);
		}
	}
}

var types = getTypes();
function getTypes() {

	const url = location.origin + "/types/";
	const t = fetch(url)
		.then((response) => {
			return response.json();
		}).then((data) => {
			return data;
		});
	return t;
}

function getCode(code) {

	if (code === 'auto') {
		return null;
	}

	for (let i in types) {
		if (types[i].target === code) {
			return types[i];
		}
	}
}

async function setPattern(hash) {

	const code = getCode(hash);
	const textInput = document.getElementById("text");

	if (code !== null) {
		textInput.setAttribute("pattern", code.pattern);
		document.getElementById("select-picker").innerHTML = code.name;
	} else {
		textInput.setAttribute("pattern", '.*');
	}
}

async function init() {

	setupOptions();

	types = await getTypes();
	hash = location.hash.substring(1);
	await setPattern(hash);

	createBarcodeTypes(types);

	// hide copy image button in FF
	if (sUsrAg.indexOf("Firefox") > -1) {
		var imageCopyButton = document.getElementById("barcode_image");
		imageCopyButton.style.display = "none";
	}
}

function setType(type) {

	location.hash = type;
	closeMenu();
	setPattern(type);
}

function checkIfFileSelected() {

	var submitButton = document.getElementById("generate-bc");

	document.getElementById('csvFile').addEventListener('change', function() {
		if (this.value.length > 0) {
			submitButton.removeAttribute("disabled");
		} else {
			submitButton.addAttribute("disabled");
		}
	});
}

function addTooltips() {

	function getOffset(elem) {
		var offsetLeft = 0, offsetTop = 0;
		do {
			if (!isNaN(elem.offsetLeft)) {
				offsetLeft += elem.offsetLeft;
				offsetTop += elem.offsetTop;
			}
		} while (elem = elem.offsetParent);

		return { left: offsetLeft, top: offsetTop };
	}

	var targets = document.querySelectorAll('[rel=tooltip]'),
		target = false,
		tooltip = false,
		tip = false;

	for (var i = 0; i < targets.length; i++) {
		targets[i].addEventListener("mouseenter", function() {
			target = this;
			tip = target.getAttribute("title");
			tooltip = document.createElement("div");
			tooltip.id = "tooltip";

			if (!tip || tip == "") {
				return false;
			}

			target.removeAttribute("title");
			tooltip.style.opacity = 0;
			tooltip.innerHTML = tip;
			document.body.appendChild(tooltip);

			var init_tooltip = function() {
				// set width of tooltip to half of window width
				if (window.innerWidth < tooltip.offsetWidth * 1.5) {
					tooltip.style.maxWidth = window.innerWidth / 2;
				} else {
					tooltip.style.maxWidth = 340;
				}

				var pos_left = getOffset(target).left + (target.offsetWidth / 2) - (tooltip.offsetWidth / 2),
					pos_top = getOffset(target).top - tooltip.offsetHeight - 10;
				if (pos_left < 0) {
					pos_left = getOffset(target).left + target.offsetWidth / 2 - 20;
					tooltip.classList.add("left");
				} else {
					tooltip.classList.remove("left");
				}

				if (pos_left + tooltip.offsetWidth > window.innerWidth) {
					pos_left = getOffset(target).left - tooltip.offsetWidth + target.offsetWidth / 2 + 20;
					tooltip.classList.add("right");
				} else {
					tooltip.classList.remove("right");
				}

				if (pos_top < 0) {
					var pos_top = getOffset(target).top + target.offsetHeight + 15;
					tooltip.classList.add("top");
				} else {
					tooltip.classList.remove("top");
					// adding "px" is very important
					tooltip.style.left = pos_left + "px";
					tooltip.style.top = pos_top + "px";
					tooltip.style.opacity = 1;
				}
			};

			init_tooltip();
			window.addEventListener("resize", init_tooltip);

			var remove_tooltip = function() {
				tooltip.style.opacity = 0;
				document.querySelector("#tooltip") && document.body.removeChild(document.querySelector("#tooltip"));
				target.setAttribute("title", tip);
			};

			target.addEventListener("mouseleave", remove_tooltip);
			tooltip.addEventListener("click", remove_tooltip);
		});
	}
}