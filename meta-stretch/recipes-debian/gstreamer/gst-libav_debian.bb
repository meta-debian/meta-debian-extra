SUMMARY = "libav plugin for GStreamer"
DESCRIPTION = "This GStreamer plugin supports a large number of audio and video compression \
 formats through the use of the libav library.  The plugin contains GStreamer \
 elements for encoding 40+ formats (MPEG, DivX, MPEG4, AC3, DV, ...), decoding \
 elements for decoding 90+ formats (AVI, MPEG, OGG, Matroska, ASF, ...), \
 demuxing 30+ formats and colorspace conversion."
HOMEPAGE = "http://gstreamer.freedesktop.org/modules/gst-libav.html"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package

PV = "1.10.4"
DPN = "gst-libav1.0"

LICENSE = "GPLv2+ & LGPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=6762ed442b3822387a51c92d928ead0d \
                    file://gst-libs/ext/libav/ffplay.c;endline=19;md5=190d729377f81555ee1651052cd23fe9"

inherit autotools pkgconfig

DEPENDS += "bzip2 glib-2.0 gstreamer gst-plugins-base xz-utils yasm-native"

do_install_append() {
	rm -rf ${D}/${libdir}/gstreamer-1.0/*.la
}
PACKAGECONFIG[orc] = "--enable-orc,--disable-orc,orc"
PACKAGECONFIG[valgrind] = "--enable-valgrind,--disable-valgrind,valgrind,"

FILES_${PN} += "${libdir}/gstreamer-*/libgstlibav.so"

# ffmpeg/libav disables PIC on some platforms (e.g. x86-32)
INSANE_SKIP_${PN} = "textrel"
