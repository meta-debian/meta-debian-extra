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
DEBIAN_PATCH_TYPE = "nopatch"

PR = "r1"
PV = "0.97"
DPN = "open-isns"

EXTRA_OECONF += "--enable-shared --enable-static --without-security --without-slp"

DEPENDS += "openssl"

do_compile () {
    oe_runmake DESTDIR="${D}" SOLIB=libisns-nocrypto.so.0 \
    SOLIB_SONAME=libisns-nocrypto.so.0 LIB=libisns-nocrypto.a \
    libisns-nocrypto.a libisns-nocrypto.so.0
}

do_install() {
    install -d ${D}${libdir}
    install -m 644 ${S}/libisns-nocrypto.a ${D}${libdir}/
    install -m 644 ${S}/libisns-nocrypto.so.0 ${D}${libdir}/
    cd ${D}${libdir}/
    ln -sf  libisns-nocrypto.so.0 libisns-nocrypto.so
}
