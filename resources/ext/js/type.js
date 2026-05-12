//
// BarcodeAPI.org, 2017-2025
// type.js // type.html
//

window.onhashchange = function() {
	location.reload();
}

function init() {

	// Load the requested type target
	var target = window.location.hash.substring(1);

	// Redirect if not set
	if (!target) {
		window.location = "types.html";
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
		window.location = "types.html";
		return;
	}

	// Determine language	
	var language = appConfig.userLanguage;

	// Set the page title
	document.title = //
		document.title.replace("$TYPE$", type.display);

	// Determine default target
	var target = type.targets[0];

	// Update generate now link
	var linkGen = document.getElementById("link-gen");
	linkGen.href = "index.html#" + target;

	// Get the DOM template object
	var info = document.getElementById("barcode-template");
	info.setAttribute("id", "barcode-type-" + target);

	// Render each of the examples
	for (var x in type.examples) {

		var apiURL = ("api/" + target + "/" + type.examples[x]);

		var table = document.createElement("table");

		var row0 = document.createElement("tr");
		var col0 = document.createElement("td");

		// Create the image element
		var example = document.createElement("img");
		example.src = apiURL;

		col0.appendChild(example);
		row0.appendChild(col0);
		table.appendChild(row0);

		var row1 = document.createElement("tr");
		var col1 = document.createElement("td");

		var lnk = document.createElement("a");
		lnk.innerHTML = apiURL;
		lnk.href = apiURL;

		col1.appendChild(lnk);
		row1.appendChild(col1);
		table.appendChild(row1);

		// Add it to the examples block
		info.querySelector(".barcode-type-example").appendChild(table);
	}

	// Update type basic details
	info.querySelector(".type-name").innerHTML = type.display;
	info.querySelector(".type-target").innerHTML = ('/' + target + '/');

	// Update barcode costs
	info.querySelector(".type-cost-base").innerHTML = type.cost.base;
	info.querySelector(".type-cost-char").innerHTML = type.cost.char;
	info.querySelector(".type-cost-mult").innerHTML = type.cost.mult;

	// Update type extended details
	info.querySelector(".type-format").innerHTML = type.pattern;
	info.querySelector(".type-description").innerHTML = type.description[language].replaceAll("\n", "<br/>");
	info.querySelector(".type-decode").innerHTML = (type.decode ? "Yes" : "No");
	info.querySelector(".type-checksum").innerHTML = (type.checksum ? "Yes" : "No");
	info.querySelector(".type-nonprinting").innerHTML = (type.nonprinting ? "Yes" : "No");
	info.querySelector(".type-wiki-name").innerHTML = type.display;
	info.querySelector(".type-wiki-link").href = type.wiki[language];

	// Render the parameters
	buildOptions(type);

	// Log tracking event
	var setupMillis = ((new Date()) - timeStart);
	trackingEvent("AppEvents", "AppLoad", "Type", setupMillis);
}

function buildOptions(type) {

	// Build the exmaple URL root
	var exampleURL = "api/" + type.targets[0] + "/" + type.examples[0] + "?";

	// Loop each of the parameters
	for (var x in type.options) {
		var optionRow = document.createElement("tr");

		// Parameter Name
		var col0 = document.createElement("td");
		col0.innerHTML = type.options[x].name;
		optionRow.appendChild(col0);

		// Parameter Key
		var col1 = document.createElement("td");
		col1.innerHTML = x;
		optionRow.appendChild(col1);

		// Parameter Type
		var col2 = document.createElement("td");
		col2.innerHTML = type.options[x].type;
		optionRow.appendChild(col2);

		// Default Value
		var col3 = document.createElement("td");
		col3.innerHTML = type.options[x].default;
		optionRow.appendChild(col3);

		// Allowed Values
		var col4 = document.createElement("td");
		var optionValues;
		switch (type.options[x].type) {
			case "text":
				optionValues = //
					("(" + type.options[x].pattern + ")");
				break;

			case "number":
				optionValues = //
					("{" + type.options[x].min + ".." + type.options[x].max + "}");
				break;

			case "option":
				optionValues = //
					("(" + type.options[x].options + ")");
				break;
		}
		col4.innerHTML = optionValues;
		optionRow.appendChild(col4);

		// Add the parameter to the example URL
		exampleURL += (x + "=" + optionValues + "&");

		// Add the parameter row to the table
		document.querySelector(".type-options").appendChild(optionRow);
	}

	// Update the example URL
	document.querySelector(".type-options-string").innerHTML = exampleURL;
}
