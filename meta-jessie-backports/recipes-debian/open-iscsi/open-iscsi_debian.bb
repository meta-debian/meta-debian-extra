PR = "r1"

DESCRIPTION = "Open-iSCSI project is a high performance, transport \
independent, multi-platform implementation of RFC3720."
HOMEPAGE = "http://www.open-iscsi.org/"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

# Use jessie-backports-master
DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package autotools-brokensep
PV = "2.0.874"

DEPENDS += "libisns-dev libisns-nocrypto tree-native util-linux"

TARGET_CC_ARCH += "${LDFLAGS}"

do_compile_prepend () {
    sed -i "s/\tcd iscsiuio; .\/configure/\tcd iscsiuio; .\/configure --host=${HOST_SYS}/g" ${S}/Makefile
}

do_install_append () {

    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${sysconfdir}/default
    install -d ${D}${systemd_unitdir}/system
    install -d ${D}${base_libdir}/open-iscsi
    install -d ${D}${base_libdir}/modules-load.d

    install -m 0755 ${S}/usr/iscsistart ${D}${base_sbindir}/
    install -m 0755 ${S}/usr/iscsid ${D}${base_sbindir}/
    install -m 0755 ${S}/iscsiuio/src/unix/iscsiuio ${D}${base_sbindir}/

    install -m 0644 ${S}/debian/iscsid.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${S}/debian/open-iscsi.service ${D}${systemd_unitdir}/system/
    install -m 0644 ${S}/debian/iscsiuio.service ${D}${systemd_unitdir}/system/

    install -m 0755 ${S}/debian/extra/activate-storage.sh ${D}${base_libdir}/open-iscsi/
    install -m 0755 ${S}/debian/extra/logout-all.sh ${D}${base_libdir}/open-iscsi/
    install -m 0755 ${S}/debian/extra/startup-checks.sh ${D}${base_libdir}/open-iscsi/
    install -m 0755 ${S}/debian/extra/umountiscsi.sh ${D}${base_libdir}/open-iscsi/

    install -m 0755 ${S}/debian/iscsid.init ${D}${sysconfdir}/init.d/iscsid
    install -m 0755 ${S}/debian/open-iscsi.init ${D}${sysconfdir}/init.d/open-iscsi
    install -m 0755 ${S}/debian/iscsiuio.init ${D}${sysconfdir}/init.d/iscsiuio

    install -m 0644 ${S}/debian/open-iscsi.kmod ${D}${base_libdir}/modules-load.d/open-iscsi.conf
    install -m 0644 ${S}/debian/open-iscsi.default ${D}${sysconfdir}/default/open-iscsi

    # symbolic link /usr/bin/iscsiadm to /sbin/iscsiam
    install -d ${D}${bindir}
    cd ${D}${bindir}
    ln -sf /sbin/iscsiadm iscsiadm

    rm -rf ${D}${sysconfdir}/iscsi/ifaces
    rm -rf ${D}${sysconfdir}/iscsi/initiatorname.iscsi

    tree ${D}${sysconfdir}/init.d
}

PACKAGES =+ "iscsiuio"

FILES_iscsiuio += "${base_sbin}/iscsiuio"
FILES_iscsiuio += "${sysconfdir}/init.d/iscsiuio"
FILES_iscsiuio += "${systemd_unitdir}/system/iscsiuio.service"

FILES_${PN} += "${base_sbindir}/iscsid"
FILES_${PN} += "${base_sbindir}/iscsistart"
FILES_${PN} += "${sysconfdir}/init.d/iscsid"
FILES_${PN} += "${sysconfdir}/init.d/open-iscsi"
FILES_${PN} += "${sysconfdir}/default/open-iscsi"
FILES_${PN} += "${systemd_unitdir}/system/iscsid.service"
FILES_${PN} += "${base_libdir}/open-iscsi/activate-storage.sh"
FILES_${PN} += "${base_libdir}/open-iscsi/logout-all.sh"
FILES_${PN} += "${base_libdir}/open-iscsi/startup-checks.sh"
FILES_${PN} += "${base_libdir}/open-iscsi/umountiscsi.sh"
FILES_${PN} += "${base_libdir}/modules-load.d/open-iscsi.conf"

inherit ${@bb.utils.contains('VIRTUAL-RUNTIME_init_manager','systemd','systemd','',d)}

SYSTEMD_PACKAGES = "${PN} iscsiuio"
SYSTEMD_SERVICE_${PN} = "open-iscsi.service iscsid.service"
SYSTEMD_SERVICE_iscsiuio = "iscsiuio.service"
SYSTEMD_AUTO_ENABLE = "enable"
