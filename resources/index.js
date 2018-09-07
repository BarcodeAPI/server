/**
 * Call our method when the URL hash changes.
 */
window.onhashchange = loadHash;

/**
 * Called each time we should read the hash of the URL.
 * 
 * @returns
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
