SUMMARY = "Wrapper library based on GObject for libudev"
DESCRIPTION = "This library makes the programs \n\
which use GObject very simply to use libudev."

HOMEPAGE = "https://download.gnome.org/sources/libgudev/"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"
DEBIAN_QUILT_PATCHES = ""

LICENSE = "LGPL-2"
inherit debian-package
inherit autotools pkgconfig

DEPENDS += "glib-2.0 udev"

PR = "r0"
PV = "230"
LIC_FILES_CHKSUM = "file://COPYING;md5=40a21fffb367c82f39fd91a3b137c36e4"

DEBIANNAME_${PN} = "${PN}-1.0-0"
