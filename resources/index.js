/**
 * Call our method when the URL hash changes.
 */
window.onhashchange = loadHash;

/**
 * Called each time we should read the hash of the URL.
 * 
 * @returns
 */
const sUsrAg = navigator.userAgent;

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
	var text = document.getElementById("text").value;
	if (text == "") {
		text = "Try Me!";
	}

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
	document.getElementById("barcode_output").src = url;
}

function printCode() {

	var content = document.getElementById("barcode_wrapper").innerHTML;

	w = window.open();
	w.document.write(content);
	w.print();
	w.close();
}

function copyBarcodeLink() {
	/* Get the text field */
	const copyText = document.getElementById("barcode_image_link");
	const copyTextMessage = document.getElementById("message");
	copyTextMessage.setAttribute("class", "message-fade");

	/* Select the text field */
	copyText.select();
	copyText.setSelectionRange(0, 99999); /*For mobile devices*/

	/* Copy the text inside the text field */
	document.execCommand("copy");

	console.log("hmm", copyText.value)

	/* Alert the copied text */
	// alert("Copied the text: " + copyText.value);

	setTimeout(function(){ copyTextMessage.setAttribute("class", ""); }, 2500);
}

async function loadBlob(fileName) {
	const fetched = await fetch(fileName);
	return await fetched.blob();
}


async function copyImageToClipboard() {

	const url = document.getElementById("barcode_image_link").getAttribute("value");

	if (sUsrAg.indexOf("Firefox") > -1) {
		console.log("firefox");

	} else {

		try {
			const blobInput = await loadBlob(url);
			const clipboardItemInput = new ClipboardItem({'image/png': blobInput});
			await navigator.clipboard.write([clipboardItemInput]);

			console.log('Image copied.');
		} catch (e) {
			console.log(e);
		}
	}
}

function init() {
	// hide copy image button in FF
	if (sUsrAg.indexOf("Firefox") > -1) {
		var imageCopyButton = document.getElementById("barcode_image");
		imageCopyButton.style.display = "none";
	}
}
