## BarcodeAPI.org

The barcode server was designed to serve an east to use API via web protocol for use in mobile applications.

When served behind a simple Apache web server providing a UI the barcode server can be used for a wide range of tasks.

### Caching

The server implements a basic cache for rendered images. Provided the image has not expired and evicted from cache, a render request will first attempt a cache lookup before being rendered. Only requests matching certain criteria will be cached.

### Sessions

A simple session cache will track a users render requests. This can be used to provide a UI that will show a user a list of their most used codes.

## Third-Party

### Jetty

Project built around the Jetty server framework.

https://www.eclipse.org/jetty/

Apache 2.0
https://www.eclipse.org/jetty/licenses.html

### Barcode4J

All barcode generation is possible with the use of Barcode4J.

Apache 2.0
http://barcode4j.sourceforge.net/

## License

```
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
