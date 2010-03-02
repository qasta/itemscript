itemscript-standard-x.x.jar contains the version of the Itemscript
library for the standard Java environment. Unless you are using GWT,
use this JAR.

itemscript-gwt-x.x.jar contains the version of the Itemscript library
for GWT. It contains everything in the standard JAR, plus some classes
for GWT. It depends on the GWT libraries. If you are using GWT, use this
version.

You should only need to use one of the libraries, not both. The non-GWT
classes in both are identical.