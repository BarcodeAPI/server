//
// BarcodeAPI.org
// multi.js
//

var index = false;
var barcodes = false;
window.onhashchange = init;

/**
 * Called when the page body is loaded.
 */
function init() {

	multiClear();
	var share = window.location.hash.substring(1);
	((share) ? loadShare(share) : loadArgs());
	document.getElementById("input").focus();
}

/**
 * Load share hash from server.
 */
function loadShare(share) {

	fetch("/share/?key=" + share)
		.then(response => {
			return response.json();
		})
		.then(function(data) {
			renderRequests(//
				JSON.parse(data.data));
		});
}

/**
 * Load page arguments as request string.
 */
function loadArgs() {
	var root = window.location.href;
	var start = root.indexOf("?");
	if (start >= 0) {

		// parse page arguments as barcodes to render
		var params = root.substring(start + 1);
		var requests = params.split("&");

		renderRequests(requests);
	}
}

/**
 * Render a list of requests.
 */
function renderRequests(requests) {
	for (var request in requests) {
		addFromText(requests[request]);
	}
}

/**
 * Send the user back to the homse page.
 */
function goHome() {
	window.location = "/index.html";
}

/**
 * Add user text from the input field.
 */
function addFromInput() {
	var input = document.getElementById("input");
	var inStr = input.value.split("\n");

	for (var x in inStr) {
		addFromText(inStr[x]);
	}

	input.value = "";
	input.focus();
}

/**
 * Render a barcode request.
 */
function addFromText(text) {
	if (!text.length) {
		return;
	}

	var img = generateImage(text);
	document.getElementById("barcodes").appendChild(img);
}

/**
 * Handle key-down events in text field. 
 */
function onKeyDown(e) {

	// Handle {Enter} keypress
	if (e.keyCode === 13) {

		// Add input text
		addFromInput();
		e.preventDefault();
		return;
	}
}

/**
 * Handle hey-up events in text field.
 */
function onKeyUp(e) {

	// Clear input text {ESC}
	if (e.keyCode === 27) {
		var input = document.getElementById("input");
		input.value = "";
		input.focus();
		e.preventDefault();
		return;
	}

	// Previous entry on Up arrow
	if (e.keyCode === 38 && index > 0) {
		var input = document.getElementById("input");
		input.value = barcodes[--index];
		e.preventDefault();
		return;
	}

	// Next entry on Down arrow
	if (e.keyCode == 40 && index < (barcodes.length - 1)) {
		var input = document.getElementById("input");
		input.value = barcodes[++index];
		e.preventDefault();
		return;
	}

	// Handle scan or paste event
	if (e.target.value.endsWith("\n")) {
		addFromInput();
	}
}

/**
 * Generate a barcode image from a request.
 */
function generateImage(request) {

	var url = "";
	if (!request.startsWith("/api/")) {
		url += '/api/';
	}
	url += request;

	var img = document.createElement("img");
	img.classList.add("multis-barcode");
	img.src = (location.origin + url);

	barcodes.push(url);
	index = barcodes.length;

	return img;
}

/**
 * Clear all rendered images.
 */
function multiClear() {
	index = 0;
	barcodes = [];
	document.getElementById("barcodes").innerHTML = "";
	document.getElementById("input").value = "";
	document.getElementById("input").focus();
}

/**
 * Get request code for requests.
 */
function multiShare() {

	fetch('/share/', {
		method: "post",
		headers: {
			'Accept': 'application/json',
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(barcodes)
	}).then(response => {

		// Get the body of the response
		return response.text();

	}).then(function(shareCode) {

		// Update URL hash to share code
		window.location = //
			(window.location.pathname + '#' + shareCode);
	});
}

/**
 * Get URL for requests. (legacy share)
 */
function multiShareLegacy() {

	var url = "/multi.html?";
	for (var x in barcodes) {
		document.getElementById("barcodes").innerHTML = "";
		url += barcodes[x] + "&";
	}
	window.location = url;
}

/**
 * Print the page.
 */
function printPage() {
	window.print();
}
