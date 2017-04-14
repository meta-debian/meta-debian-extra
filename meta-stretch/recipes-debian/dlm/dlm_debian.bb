SUMMARY = "Distributed Lock Manager"
DESCRIPTION = "DLM is a symmetric general-purpose distributed lock manager. \
 The lock manager itself is a kernel module."
HOMEPAGE = "https://fedorahosted.org/cluster/wiki/HomePage"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package

PR = "r0"
PV = "4.0.7"
LICENSE = "LGPLv2.1+ & GPLv2+"
LIC_FILES_CHKSUM = "\
	file://libdlm/libdlm.c;endline=8;md5=f658d00d6e166ae488100bc6acf2b111 \
	file://dlm_controld/rbtree.h;endline=31;md5=a32bd0a22a8d84f33f5d312b1f0a2e28 \
	"
inherit autotools-brokensep pkgconfig
DEPENDS += "corosync pacemaker systemd libxml2"

do_install() {
	oe_runmake install DESTDIR=${D} LIBNUM=${base_libdir}

	# Base on debian/dlm-controld.install
	install -d ${D}${base_libdir}
	install -d ${D}${libdir}/modules-load.d
	mv ${D}${libdir}/udev ${D}${base_libdir}
	install -m 0644 ${S}/debian/configfs.conf \
	                ${D}${libdir}/modules-load.d/configfs.conf

	# Base on debian/rules
	install -d ${D}${systemd_system_unitdir}
	install -d ${D}${sysconfdir}/default
	cp ${S}/init/dlm.sysconfig ${D}${sysconfdir}/default/dlm
	cp ${S}/init/dlm.service ${D}${systemd_system_unitdir}
}

PACKAGES =+ "libdlm libdlmcontrol-dev libdlmcontrol3"
FILES_libdlm = "${libdir}/libdlm${SOLIBS} ${libdir}/libdlm_lt${SOLIBS}"
FILES_libdlmcontrol-dev = "${includedir}/libdlmcontrol.h ${libdir}/libdlmcontrol.so"
FILES_libdlmcontrol3 = "${libdir}/libdlmcontrol${SOLIBS}"
FILES_${PN} += "${systemd_system_unitdir} ${libdir}/modules-load.d"

# follow debian/control
RDEPENDS_${PN} += "corosync"
RDEPENDS_${PN}-dev += "libdlm"

RPROVIDES_${PN} = "${PN}-controld"
RPROVIDES_libdlm = "libdlm3"
RPROVIDES_${PN}-dev = "libdlm-dev"
DEBIANNAME_${PN} = "${PN}-controld"
DEBIANNAME_libdlm = "libdlm3"
DEBIANNAME_${PN}-dev = "libdlm-dev"
