SUMMARY = "GStreamer helper programs from the "base" set"
DESCRIPTION = "GStreamer is a streaming media framework, based on graphs of filters \
which operate on media data.  Applications using this library can do \
anything from real-time sound processing to playing videos, and just \
about anything else media-related.  Its plugin-based architecture means \
that new data types or processing capabilities can be added simply by \
installing new plug-ins."
HOMEPAGE = " http://gstreamer.freedesktop.org/modules/gst-plugins-base.html"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package

PV = "1.10.4"
DPN = "gst-plugins-base1.0"

LICENSE = "GPLv2+ & LGPLv2+ & BSD-2-Clause & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c54ce9345727175ff66d17b67ff51f58 \
                    file://COPYING.LIB;md5=6762ed442b3822387a51c92d928ead0d \
                    file://gst-libs/gst/fft/_kiss_fft_guts_f32.h;endline=13;md5=4657095441fd74f5fb7ff420cad852c8 \
                    file://gst-libs/gst/rtsp/gstrtspdefs.h;endline=41;md5=e1cbe6e8f9fab8f492b19b6134ed00d8"

inherit autotools gettext pkgconfig

#DEBIAN_SRC_URI = "file://gst-plugins-base-1.10.4.tar.gz"

#DEBIAN_UNPACK_DIR = "${WORKDIR}/gst-plugins-base-1.10.4"

DEPENDS += "alsa-lib libogg pango libvorbis libtheora util-linux \
	glib-2.0-native cdparanoia libvisual gstreamer"
EXTRA_OECONF += "\
	--disable-debug --disable-examples \
	--enable-experimental"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} pango"

PACKAGECONFIG[orc] = "--enable-orc,--disable-orc,orc"
PACKAGECONFIG[pango] = "--enable-pango,--disable-pango,pango"
PACKAGECONFIG[x11] = "--enable-x --enable-xvideo,--disable-x --disable-xvideo,virtual/libx11 libxv libsm libice"
PACKAGECONFIG[valgrind] = "--enable-valgrind,--disable-valgrind,valgrind,"

do_install_append() {
	rm ${D}${libdir}/*.la ${D}${libdir}/gstreamer-*/*.la
	oe_runmake -C ${B}/po install-data-yes DESTDIR=${D}
}

PACKAGES =+ "\
	gstreamer1.0-alsa libgstreamer-plugins-base1.0 \
	gstreamer1.0-plugins-base-apps gstreamer1.0-x"

FILES_gstreamer1.0-alsa = "${libdir}/gstreamer-1.0/libgstalsa.so"
FILES_libgstreamer-plugins-base1.0 = "${libdir}/*.so.* \
	${datadir}/gst-plugins-base/*/license-translations.dict"
FILES_gstreamer1.0-plugins-base-apps = "${bindir}/*"
FILES_gstreamer1.0-x = "\
	${libdir}/gstreamer-1.0/libgstpango.so \
	${libdir}/gstreamer-1.0/libgstximagesink.so \
	${libdir}/gstreamer-1.0/libgstxvimagesink.so"
FILES_${PN} += "${libdir}/gstreamer-1.0/*.so"

PKG_${PN} = "gstreamer1.0-plugins-base"
PKG_${PN}-doc = "gstreamer1.0-plugins-base-doc"
PKG_${PN}-dbg = "gstreamer1.0-plugins-base-dbg"
PKG_${PN}-dev = "libgstreamer-plugins-base1.0-dev"
PKG_libgstreamer-plugins-base1.0 = "libgstreamer-plugins-base1.0-0"
