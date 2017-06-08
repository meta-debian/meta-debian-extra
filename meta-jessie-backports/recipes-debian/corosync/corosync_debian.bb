SUMMARY = "Standards-based cluster framework"
DESCRIPTION = "The Corosync Cluster Engine is a Group Communication System with \
additional features for implementing high availability within \
applications. The project provides four C Application Programming \
Interface features: \
 * A closed process group communication model with virtual synchrony \
   guarantees for creating replicated state machines. \
 * A simple availability manager that restarts the application process \
   when it has failed. \
 * A configuration and statistics in-memory database that provide the \
   ability to set, retrieve, and receive change notifications of \
   information. \
 * A quorum system that notifies applications when quorum is achieved \
   or lost."
HOMEPAGE = "http://corosync.github.io/corosync/"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package

PR = "r1"
PV = "2.4.2"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a85eb4ce24033adb6088dd1d6ffc5e5d"

DEPENDS = "groff-native nss net-snmp libqb"

inherit autotools pkgconfig

# Follow debian/rules
EXTRA_OECONF = " \
	--with-logdir=${localstatedir}/log/corosync \
	--enable-dbus \
	--enable-augeas \
	--enable-snmp \
	--enable-xmlconf \
	--enable-upstart \
	--enable-qdevices \
	--enable-qnetd \
	--disable-static \
"
PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"
PACKAGECONFIG[rdma]  = "--enable-rdma,--disable-rdma,librdmacm,"
PACKAGECONFIG[monitoring]  = "--enable-monitoring,--disable-monitoring,libstatgrab,"
PACKAGECONFIG[systemd]  = "--enable-systemd,--disable-systemd,systemd,"

do_install_append() {
	install -d ${D}${sysconfdir}/init.d \
	           ${D}${sysconfdir}/default \
	           ${D}${localstatedir}/log/corosync

	install -m 0755 ${S}/debian/corosync.init \
	                ${D}${sysconfdir}/init.d/corosync
	install -m 0644 ${S}/debian/corosync.default \
	                ${D}${sysconfdir}/default/corosync
	install -m 0644 ${S}/debian/corosync-notifyd.default \
	                ${D}${sysconfdir}/default/corosync-notifyd
	install -m 0755 ${S}/debian/corosync-notifyd.init \
	                ${D}${sysconfdir}/init.d/corosync-notifyd

	# Base on debian/corosync.install
	install -d ${D}${docdir}/corosync/examples
	mv ${D}${sysconfdir}/corosync/corosync.*example* \
	   ${D}${docdir}/corosync/examples

	install -m 0644 ${S}/debian/corosync.conf \
	                ${D}${sysconfdir}/corosync/corosync.conf

	# Base on debian/corosync-qdevice.init
	install -D -m 0755 ${S}/debian/corosync-qdevice.init \
	                   ${D}${sysconfdir}/init.d/${PN}-qdevice
	rm -f ${D}${datadir}/${PN}/corosync-qdevice

	# Base on debian/corosync-qdevice.install
	install -D -m 0644 ${S}/init/corosync-qdevice.sysconfig.example \
	                   ${D}${sysconfdir}/default/${PN}-qdevice

	# Base on debian/corosync-qnetd.init
	install -D -m 0755 ${S}/debian/corosync-qnetd.init \
	                   ${D}${sysconfdir}/init.d/${PN}-qnetd
	rm -f ${D}${datadir}/${PN}/corosync-qnetd

	# Base on debian/corosync-qnetd.install
	install -D -m 644 ${S}/init/corosync-qnetd.sysconfig.example \
	                  ${D}${sysconfdir}/default/corosync-qnetd

	# Base on debian/rules
	rm -rf ${D}${datadir}/corosync/corosync \
	       ${D}${datadir}/corosync/corosync-notifyd \
	       ${D}${docdir}/corosync/LICENSE
}
# Base on debian/corosync.postinst
pkg_postinst_${PN}() {
	echo 'This file is here to keep the log dir around after package removal
until #588515 is fixed.' >$D${localstatedir}/log/corosync/.empty
}

RDEPENDS_${PN} += "lsb-base adduser libxslt-bin"
RDEPENDS_${PN}-notifyd += "lsb-base ${PN}"

PACKAGES =+ "${PN}-notifyd libcfg libcfg-dev libcmap libcmap-dev lib${PN}-common \
             lib${PN}-common-dev libcpg libcpg-dev libquorum libquorum-dev libsam \
             libsam-dev libtotem-pg libtotem-pg-dev libvotequorum libvotequorum-dev \
             ${PN}-qdevice ${PN}-qnetd"

FILES_${PN}-notifyd = "\
	${sysconfdir}/dbus-1/system.d/corosync-signals.conf \
	${sysconfdir}/default/corosync-notifyd \
	${sysconfdir}/init.d/corosync-notifyd \
	${sysconfdir}/init/corosync-notifyd.conf \
	${systemd_system_unitdir}/corosync-notifyd.service \
	${sbindir}/corosync-notifyd \
	${datadir}/snmp/mibs/*"
FILES_libcfg  = "${libdir}/libcfg${SOLIBS}"
FILES_libcfg-dev = "\
	${includedir}/corosync/cfg.h \
	${libdir}/libcfg.so \
	${libdir}/pkgconfig/libcfg.pc"
FILES_libcmap = "${libdir}/libcmap${SOLIBS}"
FILES_libcmap-dev = "\
	${includedir}/corosync/cmap.h \
	${libdir}/libcmap.so \
	${libdir}/pkgconfig/libcmap.pc"
FILES_lib${PN}-common = "${libdir}/libcorosync_common${SOLIBS}"
FILES_lib${PN}-common-dev = "\
	${includedir}/corosync/corodefs.h \
	${includedir}/corosync/corotypes.h \
	${includedir}/corosync/hdb.h \
	${libdir}/libcorosync_common.so \
	${libdir}/pkgconfig/corosync.pc \
	${libdir}/pkgconfig/libcorosync_common.pc"
FILES_libcpg = "${libdir}/libcpg${SOLIBS}"
FILES_libcpg-dev = "\
	${includedir}/corosync/cpg.h \
	${libdir}/libcpg.so \
	${libdir}/pkgconfig/libcpg.pc"
FILES_libquorum = "${libdir}/libquorum${SOLIBS}"
FILES_libquorum-dev = "\
	${includedir}/corosync/quorum.h \
	${libdir}/libquorum.so \
	${libdir}/pkgconfig/libquorum.pc"
FILES_libsam = "${libdir}/libsam${SOLIBS}"
FILES_libsam-dev = "\
	${includedir}/corosync/sam.h \
	${libdir}/libsam.so \
	${libdir}/pkgconfig/libsam.pc"
FILES_libtotem-pg = "${libdir}/libtotem_pg${SOLIBS}"
FILES_libtotem-pg-dev = "\
	${includedir}/corosync/list.h \
	${includedir}/corosync/totem/* \
	${libdir}/libtotem_pg.so \
	${libdir}/pkgconfig/libtotem_pg.pc"
FILES_libvotequorum = "${libdir}/libvotequorum${SOLIBS}"
FILES_libvotequorum-dev = "\
	${includedir}/corosync/votequorum.h \
	${libdir}/libvotequorum.so \
	${libdir}/pkgconfig/libvotequorum.pc"
FILES_${PN}-qdevice = "\
	${sysconfdir}/init.d/${PN}-qdevice \
	${sysconfdir}/${PN}/qdevice \
	${sysconfdir}/default/${PN}-qdevice \
	${base_libdir}/systemd/system/${PN}-qdevice.service \
	${sbindir}/${PN}-qdevice* \
	/run/${PN}-qdevice"
FILES_${PN}-qnetd = "\
	${sysconfdir}/init.d/${PN}-qnetd \
	${sysconfdir}/${PN}/qnetd \
	${sysconfdir}/default/${PN}-qnetd \
	${base_libdir}/systemd/system/${PN}-qnetd.service \
	${bindir}/${PN}-qnetd* \
	/run/${PN}-qnetd"
FILES_${PN} += "\
	${datadir}/augeas/* \
	${systemd_system_unitdir}/corosync.service"
