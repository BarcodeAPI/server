
function init() {

	document.getElementsByClassName("link-csv")[0].addEventListener('click', actionDownloadCSV);
}

function actionDownloadCSV() {

	var format = 'text/csv';

	var file = new Blob([csvExample], { type: format });
	var a = document.createElement("a");
	a.href = URL.createObjectURL(file);
	a.download = 'test.csv';
	document.body.appendChild(a);
	a.click();
	document.body.removeChild(a);
}

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

var csvExample = //
	"Aztec Barcode,aztec\n" + //
	"1234567890,codabar\n" + //
	"Try Me!,128\n" + //
	"TRY 39 ME,39\n" + //
	"Data Matrix Barcode,dm\n" + //
	"1234567890128,13\n" + //
	"01234565,8\n" + //
	"98765432109213,14\n" + //
	"PDF - 417,417\n" + //
	"QR Barcode,qr\n" + //
	"11212345612345678,royal\n" + //
	"123456789012,a\n" + //
	"01023459,e\n" + //
	"0123456709498765432101234567891,usps\n";
