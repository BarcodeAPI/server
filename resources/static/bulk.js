function checkIfFileSelected() {

	var submitButton = document.getElementById("generate-bc");

	document.getElementById('csvFile').addEventListener('change', function() {
		if (this.value.length > 0) {
			submitButton.removeAttribute("disabled");
		} else {
			submitButton.addAttribute("disabled");
		}
	});
}
