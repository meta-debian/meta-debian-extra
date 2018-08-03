SUMMARY = "control IEEE 1394 audio/video devices"
DESCRIPTION = "libavc1394 is a programming interface for the 1394 Trade Association AV/C \
(Audio/Video Control) Digital Interface Command Set. It allows you to \
remote control camcorders and similar devices connected to your computer \
via an IEEE 1394 (aka Firewire) link."
HOMEPAGE = "http://sourceforge.net/projects/libavc1394/"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package

PV = "0.5.4"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=dcf3c825659e82539645da41a7908589"

inherit autotools

#DEBIAN_SRC_URI = "file://libavc1394-0.5.4.tar.gz"

#DEBIAN_UNPACK_DIR = "${WORKDIR}/libavc1394-0.5.4"

DEPENDS += "libraw1394"

do_install_append() {
	rm -rf ${D}/${libdir}/*.la
}

PACKAGES =+ "${PN}-tools"

FILES_${PN}-tools = "${bindir}/*"

DEBIANNAME_${PN} = "${PN}-0"
RPROVIDES_${PN} += "${PN}-0"
