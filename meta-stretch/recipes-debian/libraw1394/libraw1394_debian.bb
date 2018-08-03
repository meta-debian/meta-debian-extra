SUMMARY = "library for direct access to IEEE 1394 bus"
DESCRIPTION = "libraw1394 is the only supported interface to the kernel side raw1394 \
of the Linux IEEE-1394 subsystem, which provides direct access to the \
connected 1394 buses to user space.  Through libraw1394/raw1394, \
applications can directly send to and receive from other nodes without \
requiring a kernel driver for the protocol in question."

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package

PV = "2.1.2"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=d8045f3b8f929c1cb29a1e3fd737b499"

inherit autotools

#DEBIAN_SRC_URI = "file://libraw1394-2.1.2.tar.gz"

#DEBIAN_UNPACK_DIR = "${WORKDIR}/libraw1394-2.1.2"

do_debian_patch_prepend() {
	# Remove patches to avoid error: "series is empty, but some patches found"
	[ -s ${DEBIAN_QUILT_PATCHES}/series ] || rm  -rf ${DEBIAN_QUILT_PATCHES}/*.patch

	rm -rf ${D}/${libdir}/*.la
}

PACKAGES =+ "${PN}-tools"

FILES_${PN}-tools = "${bindir}/*"
DEBIANNAME_${PN} = "${PN}-11"
RPROVIDES_${PN} += "${PN}-11"
