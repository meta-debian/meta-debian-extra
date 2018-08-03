FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://0001-wayland-protocol-locate-the-correct-libraries-with-p.patch \
"
SUMMARY = "Series of wayland compositor protocols"
DESCRIPTION = "\
The wayland-protocols package provides us with the functionalities\n\
that are not available with the wayland core protocol.\
"
HOMEPAGE = "http://wayland.freedesktop.org/"
PR = "r0"
# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"


inherit debian-package
PV = "1.7-1"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=1d4476a7d98dd5691c53d4d43a510c72"
inherit autotools pkgconfig

DEPENDS_${PN} += "wayland"
DEBIAN_PATCH_TYPE = "nopatch"

BBCLASSEXTEND = "native"
