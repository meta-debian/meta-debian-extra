SUMMARY = "Wacom model feature query library"
DESCRIPTION = "libwacom is a library to identify \
wacom tablets and their model-specific features."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libinput/"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

LICENSE = "MIT"
inherit debian-package
inherit autotools pkgconfig

PACKAGES += "${PN}-bin ${PN}-common" 

DEPENDS += "glib-2.0 libgudev"
DEPENDS_${PN}-dev += "${PN} glib-2.0"

PR = "r0"
PV = "0.22"
LIC_FILES_CHKSUM = "file://COPYING;md5=40a21fffb367c82f39fd91a3b137c36e4"

do_install_append() {
	rm -f ${D}${libdir}/libwacom.la
}

FILES_${PN}-dev = "${includedir}/${PN}-1.0/${PN}/libwacom.h \
	${libdir}/${PN}.a \
	${libdir}/${PN}.so \
	${libdir}/pkgconfig/${PN}.pc \
"
FILES_${PN} = "${libdir}/${PN}.so.*"
FILES_${PN}-bin = "${bindir}/libwacom-list-local-devices"
FILES_${PN}-common = "/lib/udev/rules.d/65-libwacom.rules \
	${datadir}/${PN}/*.tablet \
	${datadir}/${PN}/layouts/*.svg \
	${datadir}/${PN}/${PN}.stylus \
"
DEBIANNAME_${PN} = "${PN}2"
BBCLASSEXTEND = "native"
