SUMMARY = "GStreamer plugins from the "good" set"
DESCRIPTION = "GStreamer is a streaming media framework, based on graphs of filters \
which operate on media data.  Applications using this library can do \
anything from real-time sound processing to playing videos, and just \
about anything else media-related.  Its plugin-based architecture means \
that new data types or processing capabilities can be added simply by \
installing new plug-ins."
HOMEPAGE = "http://gstreamer.freedesktop.org/modules/gst-plugins-good.html"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package

PV = "1.10.4"
DPN = "gst-plugins-good1.0"

LICENSE = "LGPLv2.1+ & LGPLv2+ & BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://ext/flac/gstflac.c;endline=18;md5=a752c35267d8276fd9ca3db6994fca9c \
                    file://gst/monoscope/monoscope.c;endline=37;md5=aaed07c4ed4a6ba509ca92bb500b6473 \
                    file://sys/osxaudio/gstosxaudiosink.h;endline=48;md5=68728dafada7c20a3a661b24d81da734"

inherit autotools gettext pkgconfig

#DEBIAN_SRC_URI = "file://gst-plugins-good-1.10.4.tar.gz"

#DEBIAN_UNPACK_DIR = "${WORKDIR}/gst-plugins-good-1.10.4"

DEPENDS += "alsa-lib cairo libpng libjpeg-turbo flac pulseaudio \
	glib-2.0 bzip2 libvisual gstreamer gst-plugins-base libraw1394 libavc1394 libiec61883"
EXTRA_OECONF += "\
	--disable-debug --disable-examples \
	--enable-experimental \
	--with-default-audiosink=autoaudiosink \
	--with-default-audiosrc=autoaudiosrc \
	--with-default-videosink=autovideosink \
	--with-default-videosrc=v4l2src \
	--with-default-visualizer=goom"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"

PACKAGECONFIG[orc] = "--enable-orc,--disable-orc,orc"
PACKAGECONFIG[x11] = "--enable-x,--disable-x,virtual/libx11 libxext libxfixes libxv"
PACKAGECONFIG[valgrind] = "--enable-valgrind,--disable-valgrind,valgrind,"

do_install_append() {
	rm -rf ${D}/${libdir}/gstreamer-1.0/*.la
}
PACKAGES =+ "gstreamer1.0-pulseaudio"

FILES_gstreamer1.0-pulseaudio = "${libdir}/gstreamer-1.0/libgstpulse.so"
FILES_${PN} += "${libdir}/gstreamer-1.0/*.so ${datadir}/gstreamer-1.0/presets/*"

PKG_${PN} = "gstreamer1.0-plugins-good"
PKG_${PN}-doc = "gstreamer1.0-plugins-good-doc"
PKG_${PN}-dbg = "gstreamer1.0-plugins-good-dbg"

RPROVIDES_${PN} += "gstreamer1.0-plugins-good"
RPROVIDES_${PN}-doc = "gstreamer1.0-plugins-good-doc"
RPROVIDES_${PN}-dbg = "gstreamer1.0-plugins-good-dbg"

RDEPENDS_${PN} += "libraw1394 libavc1394 libiec61883"
