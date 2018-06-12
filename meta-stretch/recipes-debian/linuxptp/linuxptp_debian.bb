PV = "1.8"
PR = "r0"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit debian-package

DEBIAN_GIT_BRANCH = "stretch-master"

# no debian/patches
DEBIAN_QUILT_PATCHES = ""

# for systemd service files
KEEP_NONARCH_BASELIB = "1"

DEPENDS = ""

EXTRA_OEMAKE = "prefix=${prefix} mandir=${mandir} 'CC=${CC}'"

do_configure() {
	:
}

do_install() {
	oe_runmake DESTDIR=${D} install

	# follow debian/linuxptp.install
	install -d ${D}/${base_libdir}/systemd/system
	for s in ptp4l phc2sys timemaster; do
		install -m 0644 ${S}/debian/${s}.service \
			${D}/${base_libdir}/systemd/system
	done
	install -d ${D}/${sysconfdir}/linuxptp
	install -m 0644 ${S}/default.cfg ${D}/${sysconfdir}/linuxptp/ptp4l.conf
	install -m 0644 ${S}/debian/timemaster.conf ${D}/${sysconfdir}/linuxptp
}

FILES_${PN} += "${base_libdir}/*"
