SUMMARY = "Global File System 2 - filesystem tools"
DESCRIPTION = "\
 The Global File System allows a cluster of machines to concurrently access\n\
 shared storage hardware like SANs or iSCSI and network block devices. GFS\n\
 can be deployed to build high-availability services without the single point\n\
 of failure of a file server.\n\
 .\n\
 This package contains tools for creating and managing global file systems.\n\
 GFS itself is a set of kernel modules."
LICENSE = "GPL-2+"
SECTION = "admin"
DEPENDS = "util-linux ncurses zlib"
LIC_FILES_CHKSUM = "file://doc/COPYING.applications;md5=751419260aa954499f7abaabaa882bbe"
HOMEPAGE = "https://pagure.io/gfs2-utils"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

PR = "r0"
inherit debian-package
PV = "3.1.9"

inherit autotools pkgconfig gettext

EXTRA_OECONF = "--with-udevdir=/lib/udev"

FILES_${PN} += "/lib/udev"

do_install_append() {
	# debian/gfs2-utils.links
	cd ${D}${sbindir}
	for f in fsck mkfs
	do
		ln -s ${f}.gfs2 gfs2_${f}
	done
	cd -
}

RDEPENDS_${PN} += "libblkid1 libncurses5 libtinfo5 python zlib"
