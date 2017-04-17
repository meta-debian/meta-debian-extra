SUMMARY = "Checksumming Copy on Write Filesystem utilities"
DESCRIPTION = "Btrfs is a new copy on write filesystem for Linux aimed at implementing \
advanced features while focusing on fault tolerance, repair and easy \
administration. \
This package contains utilities (mkfs, fsck) used to work with btrfs \
and an utility (btrfs-convert) to make a btrfs filesystem from an ext3 "
HOMEPAGE = "http://btrfs.wiki.kernel.org/"

DEBIAN_GIT_BRANCH = "jessie-backports-master"
inherit debian-package
PV = "4.7.3"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=fcb02dc552a041dee27e4b85c7396067"

DEBIAN_QUILT_PATCHES = ""
inherit autotools-brokensep pkgconfig

DEPENDS += "lzo e2fsprogs"
# follow debian/rules
EXTRA_OECONF += "--bindir=${base_bindir}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[manpages] = "--enable-documentation, --disable-documentation, asciidoc-native xmlto-native"
do_install(){
	oe_runmake install DESTDIR=${D}
	# Adding btrfs-calc-size
	install -D -m 0755 ${S}/btrfs-calc-size ${D}${base_bindir}
	# Adding initramfs-tools integration
	install -D -m 644 ${S}/debian/local/btrfs.hook \
	                  ${D}${datadir}/initramfs-tools/hooks/btrfs
	install -D -m 644 ${S}/debian/local/btrfs.local-premount \
	                  ${D}${datadir}/initramfs-tools/scripts/local-premount/btrfs
}
FILES_${PN} =+ "${datadir}/*"
