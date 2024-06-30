//
// BarcodeAPI.org
// api.js
//

var language = "en";

function init(mode) {
	var filter = window.location.hash.substring(1);

	fetch('/types/')
		.then(response => {
			return response.json();
		})
		.then(data => {
			for (var x in data) {
				if (!filter) {
					addType(mode, data[x]);
				} else if (data[x].name == filter) {
					addType(mode, data[x]);
				}
			}

			delTemplate();
		});
}

function addType(mode, type) {

	var target = type.targets[0];

	var info = document.getElementById("barcode-template").cloneNode(true);
	info.setAttribute("id", "barcode-type-" + target);

	var link = ("/api/" + target + "/" + type.example);
	info.querySelector(".type-example").src = link;

	info.querySelector(".type-name").innerHTML = type.display;
	info.querySelector(".type-target").innerHTML = ('/' + target + '/');
	info.querySelector(".type-cost-basic").innerHTML = type.costBasic;
	info.querySelector(".type-cost-custom").innerHTML = type.costCustom;

	switch (mode) {
		case "single":
			document.title = document.title.replace("$TYPE$", type.display);
			info.querySelector(".type-example-link").href = "index.html#" + target;
			info.querySelector(".type-format").innerHTML = type.pattern;
			info.querySelector(".type-description").innerHTML = type.description[language];
			info.querySelector(".type-wiki").href = type.wiki[language];
			info.querySelector(".type-checksum").innerHTML = (type.checksum ? "Yes" : "No");
			info.querySelector(".type-nonprinting").innerHTML = (type.nonprinting ? "Yes" : "No");
			//		info.querySelector(".type-parameters").innerHTML = type.parameters;
			break;
		case "all":
			var moreLink = ("type.html#" + type.name);
			info.querySelector(".type-example-link").href = moreLink;
			info.querySelector(".type-more").href = moreLink;
			break;
	}

	document.getElementById("barcode-types").append(info);
}

function delTemplate() {
	document.getElementById("barcode-types")//
		.removeChild(document.getElementById("barcode-template"));
}
