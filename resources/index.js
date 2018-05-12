window.onhashchange = loadHash;

function loadHash() {

	var hash = location.hash.substring(1);

	var topnav = document.getElementById("topnav");
	for ( var x in topnav.childNodes) {
		var classList = topnav.childNodes[x].classList;
		if (classList != null) {
			classList.remove("active");
		}
	}

	var selected = document.getElementById("type-" + hash);
	if (selected == null) {

		location.hash = "auto";
		return;
	}

	selected.setAttribute("class", "active");

	genCode();
}

function genCode() {

	var url = location.origin + "/api";

	var type = location.hash.substring(1);
	if (type == "") {
		type = "auto";
	}

	var text = document.getElementById("text").value;
	if (text == "") {

		url = "";
	} else {

		if (type != "auto") {

			url = url + "/" + type;
		}

		url += "/" + encodeURIComponent(text);
		console.log("Generating: " + url);
	}

	document.getElementById("barcode_output").src = url;
}
