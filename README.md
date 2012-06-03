# A OSGi Declarative Service Viewer
The plugin will add a action called "View Declarative Services..." to the Tools menu. The action will open a new ToolWindow called "Declarative Services"
on the bottom. The ToolWindow will the project for Service Components.

The Viewer does support currently:
* XML Service Component declaration
    All service components which are registered in the MANIFEST.MF will be scanned and displayed.
* Felix Service Components
    All services which are annotated with Felix SCR annotations will be scanned and displayed.

Screenshot:
![Declarative Service Viewer](http://github.com/chilicat/declarative-services-viewer/raw/master/images/ds-viewer.png)


Planned Work:
* Support standard OSGi SCR annotations.
* Show Graph for all (selected) Service Components.
* Scan libraries for Service Components.


# Instalation
Search for intellij plugins for "OSGi Declarative Service Viewer".

# License
Copyright (c) 2012 chilicat.dev

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.