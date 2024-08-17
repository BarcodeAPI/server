//
// BarcodeAPI.org
// api.js
//

var language = "en";

window.onhashchange = function() {
	location.reload();
}

function init() {

	var target = window.location.hash.substring(1);

	if (!target) {
		window.location = "/types.html";
		return;
	}

	fetch(("/type/?type=" + target))
		.then(response => {
			return response.json();
		})
		.then(loadType);
}

function loadType(type) {

	document.title = //
		document.title.replace("$TYPE$", type.display);

	var target = type.targets[0];

	var info = document.getElementById("barcode-template");
	info.setAttribute("id", "barcode-type-" + target);

	var link = ("/api/" + target + "/" + type.example);
	info.querySelector(".type-example").src = link;

	info.querySelector(".type-name").innerHTML = type.display;
	info.querySelector(".type-target").innerHTML = ('/' + target + '/');
	info.querySelector(".type-cost-basic").innerHTML = type.costBasic;
	info.querySelector(".type-cost-custom").innerHTML = type.costCustom;

	info.querySelector(".type-example-link").href = "index.html#" + target;
	info.querySelector(".type-format").innerHTML = type.pattern;
	info.querySelector(".type-description").innerHTML = type.description[language];
	info.querySelector(".type-wiki").href = type.wiki[language];
	info.querySelector(".type-checksum").innerHTML = (type.checksum ? "Yes" : "No");
	info.querySelector(".type-nonprinting").innerHTML = (type.nonprinting ? "Yes" : "No");
	//		info.querySelector(".type-parameters").innerHTML = type.parameters;
}
