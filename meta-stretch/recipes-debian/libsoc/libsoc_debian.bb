SUMMARY = "C library to interface with common peripherals (runtime)"
DESCRIPTION = "libsoc is a C library to interface with \
common peripherals (gpio, i2c, spi) found on SoC"

inherit debian-package
inherit autotools pkgconfig
DEBIAN_GIT_BRANCH = "stretch-master"
LICENSE  = "LGPLv2"
LIC_FILES_CHKSUM = "file://LICENCE;md5=e0bfebea12a718922225ba987b2126a5"
DEBIAN_QUILT_PATCHES = ""

do_install_append() {
	rm -f ${D}${libdir}/libsoc.la
}

PV = "0.8.2"

FILES_${PN} = "${libdir}/libsoc.so.2 \
               ${libdir}/libsoc.so.2.4.2 \
              "
FILES_${PN}-dev = "${includedir}/libsoc*.h \
                   ${libdir}/libsoc.a \
                   ${libdir}/libsoc.so \
                   ${libdir}/pkgconfig/libsoc.pc \
                  "

