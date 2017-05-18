inherit debian-package autotools-brokensep

DESCRIPTION = "Open-iSNS is an implementation of \
the Internet Storage Name Service (iSNS), according to RFC 4171, \
which facilitates automated discovery, management, \
and configuration of iSCSI and Fibre Channel devices on a TCP/IP network."
HOMEPAGE = "https://github.com/open-iscsi/open-isns"
LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"

# Use jessie-backports-master
DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package autotools-brokensep

# debina/source/format is 3.0 (quilt)
# but it doesn't include debian patches.
DEBIAN_QUILT_PATCHES = ""

PR = "r1"
PV = "0.97"
DPN = "open-isns"

DEPENDS += "openssl"

EXTRA_OECONF += "--enable-shared --enable-static --with-security --without-slp"

do_compile () {
    oe_runmake DESTDIR="${D}" all
}

do_install_append () {
    oe_runmake DESTDIR="${D}" install -- install_hdrs install_lib

    install -d ${D}${sysconfdir}
    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${base_libdir}
    install -d ${D}${systemd_unitdir}/system
    install -m 0755 ${S}/debian/open-isns-discoveryd.isnsdd.init ${D}${sysconfdir}/init.d/isnsdd
    install -m 0755 ${S}/debian/open-isns-server.isnsd.init ${D}${sysconfdir}/init.d/isnsd
    install -m 0755 ${S}/debian/extra/isnsdd.service ${D}${systemd_unitdir}/system/isnsdd.service
    install -m 0755 ${S}/debian/extra/isnsd.service ${D}${systemd_unitdir}/system/isnsd.service

    rm -rf ${D}${libdir}/systemd
}

PACKAGES =+ "libisns0 libisns-dev ${PN}-discoveryd ${PN}-server ${PN}-utils"

FILES_libisns0 = "${libdir}/libisns.so.0"

FILES_libisns-dev = "${libdir}/libisns.* ${includedir}/libisns"
RDEPENDS_libisns-dev += "libisns0"
DEPENDS_libisns-dev += "libisns-nocrypto"
INSANE_SKIP_libisns-dev += "staticdev"

FILES_${PN}-discoveryd = "${sbindir}/isnsdd ${sysconfdir}/init.d/isnsdd ${sysconfdir}/isns/isnsdd.conf ${base_libdir}/systemd/system/isnsdd.service"
RDEPENDS_${PN}-discoveryd += "libisns0 ${PN}-utils"

FILES_${PN}-server = "${sbindir}/isnsd ${sysconfdir}/isns/isnsd.conf ${sysconfdir}/init.d/isnsd ${base_libdir}/systemd/system/isnsd.service"
RDEPENDS_${PN}-server += "libisns0 ${PN}-utils"

FILES_${PN}-utils = "${sysconfdir}/isns/isnsadm.conf ${sbindir}/isnsadm"
RDEPENDS_${PN}-utils += "libisns0"

inherit ${@bb.utils.contains('VIRTUAL-RUNTIME_init_manager','systemd','systemd','',d)}
SYSTEMD_PACKAGES = "${PN}-discoveryd ${PN}-server"
SYSTEMD_SERVICE_${PN}-discoveryd = "isnsdd.service"
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE_${PN}-server = "isnsd.service"
SYSTEMD_AUTO_ENABLE = "enable"

PROVIDES += "libisns-dev"
