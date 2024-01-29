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

	var info = document.getElementById("barcode-template").cloneNode(true);
	info.setAttribute("id", "barcode-type-" + type.targets[0]);

	var link = ("/api/" + type.targets[0] + "/" + type.example);
	info.querySelector(".type-example").src = link;

	info.querySelector(".type-name").innerHTML = type.name.replace("_", "-");
	info.querySelector(".type-target").innerHTML = type.targets[0];
	info.querySelector(".type-cost-basic").innerHTML = type.cost;

	switch (mode) {
		case "single":
			//info.querySelector(".type-format").innerHTML = type.pattern;
			info.querySelector(".type-description").innerHTML = type.description[language];
			//info.querySelector(".type-cost-custom").innerHTML = type.cost;
			//		info.querySelector(".type-parameters").innerHTML = type.parameters;
			break;
		case "all":
			info.querySelector(".type-more").href = "type.html#" + type.name;
			break;
	}



	document.getElementById("barcode-types").append(info);
}

function delTemplate() {
	document.getElementById("barcode-types")//
		.removeChild(document.getElementById("barcode-template"));
}
