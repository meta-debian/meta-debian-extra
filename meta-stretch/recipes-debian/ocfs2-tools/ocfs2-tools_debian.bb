SUMMARY = "tools for managing OCFS2 cluster filesystems"
DESCRIPTION = "\
OCFS2 is a general purpose cluster filesystem. Unlike the initial release \
of OCFS, which supported only Oracle database workloads, OCFS2 provides \
full support as a general purpose filesystem.  OCFS2 is a complete rewrite \
of the previous version, designed to work as a seamless addition to the \
Linux kernel. \
"
HOMEPAGE = "https://ocfs2.wiki.kernel.org/"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package

PR = "r0"
PV = "1.8.4"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
inherit autotools-brokensep pkgconfig

#follow debian/rules
EXTRA_OECONF += "--disable-debug --enable-dynamic-ctl --enable-dynamic-fsck"

DEPENDS += "e2fsprogs util-linux glib-2.0 libaio dlm"

oe_runconf_prepend() {
	#correct path to header file Python.h
	sed -i -e "s:I\${py_prefix}:I${STAGING_DIR_HOST}${prefix}:g" \
		${S}/configure
}

do_install_append() {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/vendor/common/o2cb.init \
		${D}${sysconfdir}/init.d/o2cb
	install -m 0755 ${S}/vendor/common/ocfs2.init \
		${D}${sysconfdir}/init.d/ocfs2

	install -d ${D}${base_libdir}/udev/rules.d
	install -m 0644 ${S}/vendor/common/51-ocfs2.rules \
		${D}${base_libdir}/udev/rules.d
}
PARALLEL_MAKE = ""
