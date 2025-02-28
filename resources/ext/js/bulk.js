//
// BarcodeAPI.org, 2017-2025
// bulk.js // bulk.html
//

var submitButton;

function init() {

	submitButton = document.getElementById("generate-bc");

	document.getElementsByClassName("link-csv")[0].addEventListener('click', actionDownloadCSV);
	document.getElementById("csvFile").addEventListener('change', checkIfFileSelected);

	// Log tracking event
	var setupMillis = ((new Date()) - timeStart);
	trackingEvent("AppEvents", "AppLoad", "Bulk", setupMillis);
}

function actionDownloadCSV() {

	var blob = "";
	for (var line in csvExample) {
		blob += (csvExample[line] + "\n");
	}

	// Create download link
	var file = new Blob([blob], { type: 'text/csv' });
	var a = document.createElement("a");
	a.href = URL.createObjectURL(file);
	a.download = 'bulk-barcodes-example.csv';
	document.body.appendChild(a);
	a.click();
	document.body.removeChild(a);

	// Log tracking event
	trackingEvent("Bulk", "Download", "Example");
}

function checkIfFileSelected(obj) {

	const fileInput = obj.target;
	const file = fileInput.files[0];

	if (file) {
		const fileName = file.name.toLowerCase();
		const isCSV = fileName.endsWith('.csv');

		if (isCSV) {
			submitButton.removeAttribute("disabled");
		} else {
			submitButton.setAttribute("disabled", "true");
			alert("Please select a CSV file.");
		}
	} else {
		submitButton.setAttribute("disabled", "true");
	}
}

function onUpload(e) {

	// Log tracking event
	trackingEvent("Bulk", "Upload");

	return false;
}

var csvExample = [
	"Aztec Barcode,aztec",
	"1234567890,codabar",
	"Try Me!,128",
	"TRY 39 ME,39",
	"Data Matrix Barcode,dm",
	"1234567890128,13",
	"01234565,8",
	"98765432109213,14",
	"PDF - 417,417",
	"QR Barcode,qr",
	"11212345612345678,royal",
	"123456789012,a",
	"01023459,e",
	"0123456709498765432101234567891,usps",
	"test-128-colors,128,fg=206060,bg=c0c0c0",
	"test-128-notext,128,height=8,text=none",
	"test-dm-color,dm,fg=602060,bg=d0d0d0",
	"test-dm-noqz,dm,qz=0",
	"00000000,8,text=none,fg=ff0000"
];
