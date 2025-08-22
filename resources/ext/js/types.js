//
// BarcodeAPI.org, 2017-2025
// types.js // types.html
//

window.onhashchange = function() {
	location.reload();
}

function init() {

	fetch('/types/')
		.then(response => {
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
		}).then(loadTypes);
}

function loadTypes(data) {

	for (var x in data) {
		addType(data[x]);
	}

	delTemplate();

	// Log tracking event
	var setupMillis = ((new Date()) - timeStart);
	trackingEvent("AppEvents", "AppLoad", "Types", setupMillis);
}

function addType(type) {

	var target = type.targets[0];

	var info = document.getElementById("barcode-template").cloneNode(true);
	info.setAttribute("id", "barcode-type-" + target);

	var link = ("/api/" + target + "/" + type.example);
	info.querySelector(".type-example").src = link;

	info.querySelector(".type-name").innerHTML = type.display;
	info.querySelector(".type-target").innerHTML = ('/' + target + '/');
	info.querySelector(".type-cost-basic").innerHTML = type.costBasic;
	info.querySelector(".type-cost-custom").innerHTML = type.costCustom;

	var homeLink = ("index.html#" + target);
	info.querySelector(".type-gen").href = homeLink;

	var moreLink = ("type.html#" + target);
	info.querySelector(".type-more").href = moreLink;
	info.querySelector(".type-example-link").href = moreLink;

	document.getElementById("barcode-types").append(info);
}

function delTemplate() {
	document.getElementById("barcode-types")//
		.removeChild(document.getElementById("barcode-template"));
}
