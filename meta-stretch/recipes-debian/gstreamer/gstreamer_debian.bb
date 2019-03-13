#
# base recipe: meta/recipes-multimedia/gstreamer/gstreamer1.0_1.8.3.bb
# base branch: morty
#

SUMMARY = "Core GStreamer libraries and elements"
DESCRIPTION = "GStreamer is a streaming media framework, based on graphs of filters \
which operate on media data.  Applications using this library can do \
anything from real-time sound processing to playing videos, and just \
about anything else media-related.  Its plugin-based architecture means \
that new data types or processing capabilities can be added simply by \
installing new plug-ins."
HOMEPAGE = "http://gstreamer.freedesktop.org"

inherit debian-package

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

PR = "r0"

PV = "1.10.4"

#DEBIAN_SRC_URI = "file://gstreamer-1.10.4.tar.gz"

#DEBIAN_UNPACK_DIR = "${WORKDIR}/gstreamer-1.10.4"

DPN = "gstreamer1.0"

LICENSE = "GPLv2+ & LGPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "\
 file://COPYING;md5=6762ed442b3822387a51c92d928ead0d \
 file://common/coverage/coverage-report-entry.pl;beginline=3;endline=17;md5=6341a9adee93a1cb48835b74ea48b15f \
 file://libs/gst/check/libcheck/check.c;endline=18;md5=2b10f9369f89f35d1f17e42b6415a52b"

DEPENDS = "glib-2.0 libcap libxml2 bison-native flex-native"

inherit autotools pkgconfig gettext

GSTREAMER_DEBUG ?= "--disable-debug"
EXTRA_OECONF = "--disable-docbook --disable-gtk-doc \
            --disable-dependency-tracking --disable-check \
            --disable-examples --disable-tests \
            --disable-valgrind ${GSTREAMER_DEBUG} \
            --libexecdir=${libdir}/${DPN} \
            "

CACHED_CONFIGUREVARS += "ac_cv_header_valgrind_valgrind_h=no"

do_install_append() {
	rm -rf ${D}${datadir}/bash-completion \
	       ${D}${libdir}/*.la \
	       ${D}${libdir}/gstreamer*/*.la \
	       ${D}${bindir}/gst-stats-*
	rm -rf ${D}${datadir}/doc \
               ${D}${datadir}/gir-1.0 \
               ${D}${datadir}/man 

	${CC} -o ${D}${bindir}/gst-codec-info-1.0 ${S}/debian/gst-codec-info.c \
	         ${CFLAGS} ${CPPFLAGS} ${LDFLAGS} `${STAGING_BINDIR_NATIVE}/pkg-config \
	         --libs --cflags glib-2.0 gthread-2.0 gmodule-no-export-2.0 gobject-2.0` \
	         ${D}${libdir}/libgstreamer-1.0.so -I${D}${includedir}/gstreamer-1.0 \
	         -I${D}${libdir}/gstreamer-1.0/include
}

RRECOMMENDS_${PN}_qemux86    += "kernel-module-snd-ens1370 kernel-module-snd-rawmidi"
RRECOMMENDS_${PN}_qemux86-64 += "kernel-module-snd-ens1370 kernel-module-snd-rawmidi"

PACKAGES = "${PN} lib${PN} lib${PN}-dev"

FILES_${PN} += "\
	${bindir}/gst-inspect-1.0 \
	${bindir}/gst-launch-1.0 \
	${bindir}/gst-typefind-1.0 \
	"
FILES_lib${PN} += "\
	${libdir}/gstreamer1.0/gstreamer-1.0/* \
	${libdir}/gstreamer-1.0/*.so \
	${libdir}/*.${SOLIBS} \
	${datadir}/locale/*/LC_MESSAGES/gstreamer-1.0.mo \
	"
FILES_lib${PN}-dev += "\
	${bindir}/dh_gstscancodecs \
	${bindir}/gst-codec-info-* \
	${datadir}/aclocal/* \
	${includedir}/gstreamer-1.0/gst/*.h \
	${includedir}/gstreamer-1.0/gst/base/*.h \
	${includedir}/gstreamer-1.0/gst/check/*.h \
	${includedir}/gstreamer-1.0/gst/controller/*.h \
	${includedir}/gstreamer-1.0/gst/net/*.h \
	${libdir}/*${SOLIBSDEV} \
	${libdir}/pkgconfig/*.pc"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

PKG_${PN} = "gstreamer1.0-tools"
PKG_lib${PN} = "libgstreamer1.0-0"
PKG_lib${PN}-dev = "libgstreamer1.0-dev"
RPROVIDES_${PN} = "gstreamer1.0-tools"
RPROVIDES_lib${PN} = "libgstreamer1.0-0"
RPROVIDES_lib${PN}-dev = "libgstreamer1.0-dev"

RDEPENDS_lib${PN} += "libcap libcap-bin"
