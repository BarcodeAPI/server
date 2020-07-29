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
	for ( var x in topnav.childNodes) {
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

function closeMenu() {
	const menu = document.getElementById("topnav");
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

	// Get the requested text
	const textInput = document.getElementById("text");
	let text = textInput.value;

	if (text === "") {
		text = "Try Me!";
	}

	const isValidCode = textInput.checkValidity()

	// Build URL with type
	url = url + "/" + type;

	// Build URL with encoded request
	url += "/" + encodeURIComponent(text);

	// Update download button
	document.getElementById("barcode_download_button").setAttribute("href", url);

	if (sUsrAg.indexOf("Firefox") === -1) {
		document.getElementById("barcode_download_button").setAttribute("download", url);
	}
	document.getElementById("barcode_image_link").setAttribute("value", url);

	// Update IMG element source
	if(isValidCode) {
		document.getElementById("barcode_output").src = url;
	}
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

	if(isOpen) {
		dropdown.setAttribute("class", "select-type open");
		menu.setAttribute("class", "barcode-types open");
	} else {
		dropdown.setAttribute("class", "select-type");
		menu.setAttribute("class", "barcode-types");
	}
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

	setTimeout(function(){ copyTextMessage.setAttribute("class", ""); }, 2500);
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
			const clipboardItemInput = new ClipboardItem({'image/png': blobInput});
			await navigator.clipboard.write([clipboardItemInput]);
		} catch (e) {
			console.log(e);
		}
	}
}

var types = getTypes();
function getTypes() {

	const url = location.origin + "/types/";
	const t = fetch(url).then((response) => {
		return response.json();
	})
	.then((data) => {
		return data;
	});
	return t;
}

function getCode(code) {

	if(code === 'auto') {
		return null;
	}

	for(let i in types){
		if(types[i].target === code) {
			return types[i];
		}
	}
}

function showCodeDescription(code) {

	 console.log(getCode(code))
}

async function setPattern(hash) {

	const code = getCode(hash);
	const textInput = document.getElementById("text");

	if(code !== null) {
		textInput.setAttribute("pattern", code.pattern);
	} else {
		textInput.setAttribute("pattern", '.*');
	}
}

async function init() {

	types = await getTypes();
	hash = location.hash.substring(1);
	await setPattern(hash);

	// hide copy image button in FF
	if (sUsrAg.indexOf("Firefox") > -1) {
		var imageCopyButton = document.getElementById("barcode_image");
		imageCopyButton.style.display = "none";
	}
}

function setType(type) {

	location.hash =type;
	closeMenu();
	setPattern(type);
}

function checkIfFileSelected() {

	var submitButton = document.getElementById("generate-bc");

	document.getElementById('csvFile').addEventListener('change', function () {
		if(this.value.length > 0) {
			console.log("file selected");
			submitButton.removeAttribute("disabled");
		} else {
			console.log("no file");
			submitButton.addAttribute("disabled");
		}
	});
}
