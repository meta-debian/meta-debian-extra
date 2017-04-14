SUMMARY = "high performance client server features library"
DESCRIPTION = "\
 libqb provides a set of high performance client-server reusable features. It \
 offers high performance logging, tracing, IPC and poll. Its initial features \
 were spun off the Corosync cluster communication suite to make them accessible \
 for other projects."
HOMEPAGE = "http://github.com/clusterlabs/libqb/wiki"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package

PR = "r0"
PV = "1.0"
LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"

inherit autotools pkgconfig

EXTRA_OECONF += "--disable-static"

PKG_${PN} = "${PN}0"
PKG_${PN}-dbg = "${PN}0-dbg"
