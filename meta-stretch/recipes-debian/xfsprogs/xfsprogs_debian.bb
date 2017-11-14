SUMMARY = "Utilities for managing the XFS filesystem"
DESCRIPTION = "A set of commands to use the XFS filesystem, including mkfs.xfs.\n\
.\n\
XFS is a high performance journaling filesystem which originated\n\
on the SGI IRIX platform.  It is completely multi-threaded, can\n\
support large files and large filesystems, extended attributes,\n\
variable block sizes, is extent based, and makes extensive use of\n\
Btrees (directories, extents, free space) to aid both performance\n\
and scalability.\n\
.\n\
Refer to the documentation at http://oss.sgi.com/projects/xfs/\n\
for complete details."
HOMEPAGE = "http://xfs.org/"
LICENSE = "GPL-1.0 & LGPL-2.1"
SECTION = "admin"
DEPENDS = "util-linux util-linux-native readline"
DEPENDS_class-target += "xfsprogs-native"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=eadc251fa991f48784178aae7081e1e1"

PR = "r0"
DEBIAN_GIT_BRANCH = "stretch-master"
inherit debian-package
PV = "4.9.0+nmu1"
DEBIAN_PATCH_TYPE = "nopatch"

SRC_URI_append_class-target = " file://0001-libxfs-Makefile-skip-building-programs-run-on-host.patch"

inherit autotools-brokensep

EXTRA_OECONF += "--enable-readline=yes --enable-blkid=yes"
do_configure_prepend() {
	export AUTOHEADER=/bin/true
	export INSTALL_USER=root
	export INSTALL_GROUP=root
}

do_install_append() {
	oe_runmake 'DESTDIR=${D}' install-dev
}

do_install_append_class-native() {
	install -d ${D}${bindir}
	install -m 755 ${B}/libxfs/gen_crc32table \
		${B}/libxfs/crc32selftest \
		${D}${bindir}
}

PACKAGES =+ "xfslibs-dev"
FILES_xfslibs-dev = " \
	${base_libdir}/libhandle.so \
	${includedir}/xfs/handle.h \
	${includedir}/xfs/jdm.h \
	${includedir}/xfs/linux.h \
	${includedir}/xfs/xfs.h \
	${includedir}/xfs/xfs_arch.h \
	${includedir}/xfs/xfs_da_format.h \
	${includedir}/xfs/xfs_format.h \
	${includedir}/xfs/xfs_fs.h \
	${includedir}/xfs/xfs_log_format.h \
	${includedir}/xfs/xfs_types.h \
	${includedir}/xfs/xqm.h \
	"

BBCLASSEXTEND =+ "native"
