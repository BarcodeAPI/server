const appOptions = {
	'display': {
		'about': false,
		'helpManual': false
	}
}

function init() {
	initUI();

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
	"1234567890,codabar\n" + //
	"PDF - 417,417\n" + //
	"TRY 39 ME,39\n" + //
	"Data Matrix Barcode,dm\n" + //
	"Try Me!,128\n" + //
	"QR Barcode,qr\n" + //
	"Aztec Barcode,aztec\n" + //
	"98765432109213,14\n" + //
	"123456789012,a\n" + //
	"01023459,e\n" + //
	"1234567890128,13";
