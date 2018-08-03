SUMMARY = "Introspection data for glib, GObject, Gio and GModule"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

LICENSE = "MIT"
inherit debian-package
inherit autotools pkgconfig

DEBIAN_TYPE_PATCH = "quilt"
PR = "r0"
PV = "1.50"
LIC_FILES_CHKSUM = "file://COPYING;md5=bb3d4b05b7549e0038fad80ef6940844"

PACKAGES += "gir-glib libgirepository"
PROVIDES += "gir-glib libgirepository"
DEPENDS_libdirepository += "glib-2.0 libffi"

FILES_gir-glib = "${libdir}/girepository-1.0/*.typelib"
FILES_libgirepository = "${libdir}/libgirepository-1.0.so.*"

DEBIANNAME_gir-glib = "gir1.2-glib-2.0"
DEBIANNAME_libgirepository = "libgirepository-1.0-1"

BBCLASSEXTEND = "native"
