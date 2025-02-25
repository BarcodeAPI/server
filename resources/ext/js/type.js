//
// BarcodeAPI.org
// api.js
//

window.onhashchange = function() {
	location.reload();
}

function init() {

	// Load the requested type target
	var target = window.location.hash.substring(1);

	// Redirect if not set
	if (!target) {
		window.location = "/types.html";
		return;
	}

	// Load the type details
	fetch(("/type/?type=" + target))
		.then(response => {
			return (response.status == 200) ? response.json() : false;
		}).then(loadType);

}

function loadType(type) {

	// Redirect if not loaded
	if (!type) {
		window.location = "/types.html";
		return;
	}

	// Set the page title
	document.title = //
		document.title.replace("$TYPE$", type.display);

	// Determine default target
	var target = type.targets[0];

	// Determine language	
	var language = appConfig.userLanguage;

	// Get the DOM template object
	var info = document.getElementById("barcode-template");
	info.setAttribute("id", "barcode-type-" + target);

	// Update example link
	var link = ("/api/" + target + "/" + type.example);
	info.querySelector(".type-example").src = link;

	// Update type basic details
	info.querySelector(".type-name").innerHTML = type.display;
	info.querySelector(".type-target").innerHTML = ('/' + target + '/');
	info.querySelector(".type-cost-basic").innerHTML = type.costBasic;
	info.querySelector(".type-cost-custom").innerHTML = type.costCustom;

	// Update type extended details
	info.querySelector(".type-example-link").href = "index.html#" + target;
	info.querySelector(".type-format").innerHTML = type.pattern;
	info.querySelector(".type-description").innerHTML = type.description[language];
	info.querySelector(".type-wiki").href = type.wiki[language];
	info.querySelector(".type-checksum").innerHTML = (type.checksum ? "Yes" : "No");
	info.querySelector(".type-nonprinting").innerHTML = (type.nonprinting ? "Yes" : "No");
	info.querySelector(".type-options").innerHTML = buildOptions(type.options);

	// Log tracking event
	trackingEvent("app_type_load", { "type": type.name });
}

function buildOptions(options) {

	var response = "";
	for (var x in options) {
		response += (x + "=" + options[x].default + "<br/>");
	}
	return response;
}
