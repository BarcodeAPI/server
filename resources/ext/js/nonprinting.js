window.addEventListener("load", function() {
	fetch('/types/')
		.then(response => {
			return response.json();
		})
		.then(data => {
			console.log(data);
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

	document.getElementById("types_supported").appendChild(dType);
}
