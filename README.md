# BarcodeAPI.org

The BarcodeAPI.org web server was designed to provide an easy to use API via HTTP protocol for use in mobile applications.


## Mirrors

[Master Repo](https://git.mclarkdev.com/BarcodeAPI.org/server)<br/>
[GitHub Mirror](https://github.com/BarcodeAPI/server)

## About

### Web Server

The server comes with a set of static HTML and Javascript files that allow users to navigate to a simple UI and generate barcodes in their web browser with ease.

### API Server

The server will generate a barcode for any content passed to the `/api` endpoint; this can be done using a web browser, fetched through a user script, or even simply with cURL.

```
curl https://barcodeapi.org/api/A_Barcode > gen.png
```

#### Automatic Code Type Detection

When simply calling the api endpoint without specifying an eplitit code type, the server will make its best judgement as to which code type will be best suited for the supplied data.

#### Defined Code Type

Also available at the api endpoint, a user may optionally define their required code type:

```
# UPC-E (e, upc-e, upce)
curl https://barcodeapi.org/api/e/00000000

# UCP-A (a, upc-a, upca, upc)
curl https://barcodeapi.org/api/a/000000000000

# EAN-8 (8, ean-8, ean8)
curl https://barcodeapi.org/api/8/00000000

# EAN-13 (13, ean-13, ean13)
curl https://barcodeapi.org/api/13/0000000000000

# Code 39 (39, code39)
curl https://barcodeapi.org/api/39/CODE39

# Code 128 (128, code128)
curl https://barcodeapi.org/api/128/Code-128

# QR Code (qr, qrcode)
curl https://barcodeapi.org/api/qr/QR_Code

# Data Matrix (matrix, datamatrix, dm)
curl https://barcodeapi.org/api/matrix/Data_Matrix

# Codabar (codabar)
curl https://barcodeapi.org/api/codabar/000000
```

### Server Statistics

The server will keep counters for most basic actions, such as total number of hits, generation requests, and render times for each type of code.

Server statistic are available at the `/stats` endpoint.

### Caching

The server implements a basic cache for rendered images. Provided the image has not expired and evicted from cache, a render request will first attempt a cache lookup before being rendered. Only requests matching certain criteria will be cached.

All cache details are available at the `/cache` endpoint.

### Sessions

A simple session cache will track a users render requests. This can be used to provide a UI that will show a user a list of their most used codes.

A user's session details are available at the `/session` endpoint.

## Third-Party

BarcodeAPI.org is only made possible with the use of third-party software.

### Jetty, [Apache 2.0](https://www.eclipse.org/jetty/licenses.html)

The BarcodeAPI server was built around the [Jetty](https://www.eclipse.org/jetty/) web server framework.

### Barcode4J, [Apache 2.0](http://barcode4j.sourceforge.net/#Introduction)

[Barcode4J](http://barcode4j.sourceforge.net/) is an open source barcode generator; it is used for the generation of the following code types:

### ZXing, [Apache 2.0](https://github.com/zxing/zxing/blob/master/LICENSE)

[ZXing](https://github.com/zxing/zxing/) is a barcode processing library that makes QR code generation possible.

## License

```text
Copyright 2017 BarcodeAPI.org

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

