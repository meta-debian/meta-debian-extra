SUMMARY = "Bootstrap a basic Debian system"
DISCRIPTION = "\
	Debootstrap is used to create a Debian base system from scratch, \
	without requiring the availability of dpkg or apt. It does this by \
	downloading .deb files from a mirror site, and carefully unpacking them \
	into a directory which can eventually be chrooted into"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=1e68ced6e1689d4cd9dac75ff5225608"

PR = "r0"

inherit debian-package
PV = "1.0.81~bpo8+1"

DEPENDS += "wget"
RDEPENDS_${PN} += "gnupg"

#Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "jessie-backports-master"

SRC_URI += "file://devices.tar.gz;unpack=0"

# All Makefile does is creation of devices.tar.gz, which fails in OE build, we use
# static devices.tar.gz as work around
# | NOTE: make -j 8 -e MAKEFLAGS=
# | rm -rf dev
# | mkdir -p dev
# | chown 0:0 dev
# | chown: changing ownership of `dev': Operation not permitted
# | make: *** [devices.tar.gz] Error 1
# | WARNING: exit code 1 from a shell command.

do_compile() {
	cp ${WORKDIR}/devices.tar.gz ${B}
	oe_runmake
}

do_install() {
	oe_runmake 'DESTDIR=${D}' install
	chown -R root:root ${D}${datadir}/debootstrap
}
