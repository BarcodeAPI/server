//
// BarcodeAPI.org, 2017-2025
// nonprinting.js // nonprinting.html
//

window.addEventListener("load", function() {

	document.supported = document.getElementById("types_supported");

	fetch('/types/')
		.then(response => {
			return response.json();
		})
		.then(data => {
			for (var x in data) {
				if (data[x].nonprinting) {
					addSupported(data[x]);
				}
			}
		});
});

function addSupported(type) {

	var dType = document.createElement("a");
	dType.innerHTML = type.display;
	dType.href = ("index.html#" + type.targets[0]);

	var iType = document.createElement("li");
	iType.appendChild(dType);

	document.supported.appendChild(iType);
}
