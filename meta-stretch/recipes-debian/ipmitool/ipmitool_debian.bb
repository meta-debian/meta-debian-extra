SUMMARY = "utility for IPMI control"
DESCRIPTION = "ipmitool is a utility for managing and configuring devices \
that support the Intelligent Platform Management Interface"
HOMEPAGE = "http://ipmitool.sourceforge.net"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package

PR = "r0"
PV = "1.8.18"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=9aa91e13d644326bf281924212862184"

inherit autotools

do_install_append() {
	#Create folders
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${systemd_system_unitdir}

	#Install etc/default/ipmievd
	install -m 0644 ${S}/debian/ipmitool.ipmievd.default \
			${D}${sysconfdir}/default/ipmievd
	#Install etc/init.d/ipmievd
	install -m 0755 ${S}/debian/ipmitool.ipmievd.init \
			${D}${sysconfdir}/init.d/ipmievd
	#Install lib/systemd/system/ipmievd.service
	install -m 0644 ${S}/debian/systemd/ipmitool.ipmievd.service \
			${D}${systemd_system_unitdir}/ipmievd.service
}

FILES_${PN} += "${systemd_system_unitdir}/* ${libdir}/*"
