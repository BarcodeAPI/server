//
// BarcodeAPI.org, 2017-2025
// decode.js // decode.html
//

// 2MB max upload file size
const MAX_SIZE = 2 * 1024 * 1024;

// Supported file types for decoding
const VALID_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/bmp'];

function init() {
	const fileSelect = document.getElementById("decode-image-select");
	fileSelect.addEventListener('change', handleFileSelection);

	const decodeButton = document.getElementById("decode-image-button");
	decodeButton.addEventListener('click', submitForDecode);

	const clearButton = document.getElementById("decode-preview-clear");
	clearButton.addEventListener("click", clearPreview);

	// Log tracking event
	var setupMillis = ((new Date()) - timeStart);
	trackingEvent("AppEvents", "AppLoad", "Decode", setupMillis);
}

function handleFileSelection() {
	const file = document.getElementById("decode-image-select").files[0];

	if (!isFileSupported(file)) {
		clearPreview(); // Clear the preview if the file is unsupported
		return;
	}

	previewImage(file);
	toggleSubmitButton(!!this.value.length);
}

function isFileSupported(file) {
	if (!file) {
		console.warn("No file selected.");
		return false;
	}

	// Check file type
	if (!VALID_IMAGE_TYPES.includes(file.type)) {
		console.error("Unsupported file type.");
		return false;
	}

	// Check file size
	if (file.size > MAX_SIZE) {
		console.error("Image exceeds maximum size.");
		return false;
	}

	return true;
}

function previewImage(file) {
	const img = document.getElementById('decode-image-preview');
	img.src = ""; // Clear previous preview

	const reader = new FileReader();
	reader.onload = (e) => {
		img.src = e.target.result;
		img.style.display = 'block';
	};
	reader.readAsDataURL(file);

	// Log tracking event
	trackingEvent("Decode", "Preview");
}

function clearPreview() {
	const img = document.getElementById('decode-image-preview');
	img.src = "";
	img.style.display = 'none';
	document.getElementById("decode-image-select").value = "";
	toggleSubmitButton(false);
}

function toggleSubmitButton(enabled) {
	const submitButton = document.getElementById("decode-image-button");
	submitButton.disabled = !enabled;
}

function submitForDecode(e) {
	console.log("Submit for decoding:", e);
	const fileInput = document.getElementById("decode-image-select");
	const file = fileInput.files[0];

	if (!file) {
		console.error("No file selected for decoding.");
		return;
	}

	const formData = new FormData();
	formData.append('image', file);

	fetch('/decode/', {
		method: 'POST',
		body: formData,
	}).then(response => {

		if (!response.ok) {
			throw new Error(`Error: ${response.statusText}`);
		}
		return response.json();
	}).then(data => {

		console.log("Decoded data:", data);
	}).catch(error => {

		console.error("Error during file upload:", error);
	});

	// Log tracking event
	trackingEvent("Decode", "Upload");
}
