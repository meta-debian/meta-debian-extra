SUMMARY = "GStreamer documentation for plugins from the \"bad\" set"
DESCRIPTION = "GStreamer is a streaming media framework, based on graphs of filters \
which operate on media data.  Applications using this library can do \
anything from real-time sound processing to playing videos, and just \
about anything else media-related.  Its plugin-based architecture means \
that new data types or processing capabilities can be added simply by \
installing new plug-ins."
HOMEPAGE = "http://gstreamer.freedesktop.org/modules/gst-plugins-bad.html"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package

PV = "1.10.4"
DPN = "gst-plugins-bad1.0"

LICENSE = "GPLv2++ & LGPLv2+ & BSD-3-Clause & BSD-2-Clause & MPL-1.1 & MIT"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=73a5855a8119deb017f5f13cf327095d \
	file://COPYING.LIB;md5=21682e4e8fea52413fd26c60acb907e5 \
	file://sys/linsys/include/sdivideo.h;endline=36;md5=440dfe19832c12259a3c6577d538c978 \
	file://gst/inter/gstintertest.c;endline=25;md5=51eec4204521b51c180d91f74a9afef0 \
	file://gst/mpegtsmux/tsmux/tsmux.h;endline=78;md5=6cdb9c9e6af940ef275346b08501a071"

inherit autotools gettext

DEPENDS += "wayland wayland-protocols gstreamer gst-plugins-base alsa-lib \
            bzip2 cairo libdrm glib-2.0 gnutls libmpc libpng libxml2"
EXTRA_OECONF += "\
	--disable-debug --disable-examples \
	--enable-experimental --disable-qt \
	--disable-pvr"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gles2', '', d)} \
                   curl sbc sndfile"

PACKAGECONFIG[flite] = "--enable-flite,--disable-flite,flite"
PACKAGECONFIG[dc1394] = "--enable-dc1394,--disable-dc1394,libdc1394-22"
PACKAGECONFIG[uvch264] = "--enable-uvch264,--disable-uvch264,libusb1 libgudev"
PACKAGECONFIG[bluez] = "--enable-bluez,--disable-bluez,bluez"
PACKAGECONFIG[sbc] = "--enable-sbc,--disable-sbc,sbc"
PACKAGECONFIG[ladspa] = "--enable-ladspa,--disable-ladspa,ladspa-sdk"
PACKAGECONFIG[assrender] = "--enable-assrender,--disable-assrender,libass"
PACKAGECONFIG[bs2b] = "--enable-bs2b,--disable-bs2b,libbs2b"
PACKAGECONFIG[chromaprint] = "--enable-chromaprint,--disable-chromaprint,chromaprint"
PACKAGECONFIG[libde265] = "--enable-libde265,--disable-libde265,libde265"
PACKAGECONFIG[curl] = "--enable-curl,--disable-curl,curl"
PACKAGECONFIG[resindvd] = "--enable-resindvd,--disable-resindvd,libdvdread libdvdnav"
PACKAGECONFIG[opengl] = "--enable-opengl,--disable-opengl,virtual/libgl libglu"
PACKAGECONFIG[gles2] = "--enable-gles2,--disable-gles2,virtual/libgles2"
PACKAGECONFIG[faad] = "--enable-faad,--disable-faad,faad2"
PACKAGECONFIG[chromaprint] = "--enable-chromaprint,--disable-chromaprint,chromaprint"
PACKAGECONFIG[fluidsynth] = "--enable-fluidsynth,--disable-fluidsynth,fluidsynth"
PACKAGECONFIG[gsm] = "--enable-gsm,--disable-gsm,libgsm"
PACKAGECONFIG[gtk] = "--enable-gtk3,--disable-gtk3,gtk+3"
PACKAGECONFIG[kate] = "--enable-kate,--disable-kate,libkate"
PACKAGECONFIG[libmms] = "--enable-libmms,--disable-libmms,libmms"
PACKAGECONFIG[modplug] = "--enable-modplug,--disable-modplug,libmodplug"
PACKAGECONFIG[ofa] = "--enable-ofa,--disable-ofa,libofa"
PACKAGECONFIG[openal] = "--enable-openal,--disable-openal,openal-soft"
PACKAGECONFIG[opencv] = "--enable-opencv,--disable-opencv,opencv"
PACKAGECONFIG[openexr] = "--enable-openexr,--disable-openexr,openexr"
PACKAGECONFIG[openjpeg] = "--enable-openjpeg,--disable-openjpeg,openjpeg2"
PACKAGECONFIG[opusparse] = "--enable-opus,--disable-opus,opus"
PACKAGECONFIG[orc] = "--enable-orc,--disable-orc,orc"
PACKAGECONFIG[rsvg] = "--enable-rsvg,--disable-rsvg,librsvg"
PACKAGECONFIG[rtmp] = "--enable-rtmp,--disable-rtmp,rtmpdump"
PACKAGECONFIG[sndfile] = "--enable-sndfile,--disable-sndfile,libsndfile"
PACKAGECONFIG[soundtouch] = "--enable-soundtouch,--disable-soundtouch,soundtouch"
PACKAGECONFIG[spandsp] = "--enable-spandsp,--disable-spandsp,spandsp"
PACKAGECONFIG[srtp] = "--enable-srtp,--disable-srtp,srtp"
PACKAGECONFIG[dtls] = "--enable-dtls,--disable-dtls,openssl"
PACKAGECONFIG[voaacenc] = "--enable-voaacenc,--disable-voaacenc,vo-aacenc"
PACKAGECONFIG[voamrwbenc] = "--enable-voamrwbenc,--disable-voamrwbenc,vo-amrwbenc"
PACKAGECONFIG[webp] = "--enable-webp,--disable-webp,libwebp"
PACKAGECONFIG[wildmidi] = "--enable-wildmidi,--disable-wildmidi,wildmidi"
PACKAGECONFIG[x265] = "--enable-x265,--disable-x265,x265"
PACKAGECONFIG[zbar] = "--enable-zbar,--disable-zbar,zbar"
PACKAGECONFIG[qt] = "--enable-qt,--disable-qt,qtbase-opensource-src"

do_compile() {
	oe_runmake WAYLAND_PROTOCOLS_DATADIR=${STAGING_DATADIR}/wayland-protocols
}
do_install_append() {
	sed -i -e "s@${STAGING_DIR_HOST}@@g" ${D}${libdir}/pkgconfig/*.pc
}

PACKAGES =+ "lib${DPN}"

FILES_lib${DPN} = "${libdir}/*${SOLIBS}"
FILES_${PN} += "${libdir}/gstreamer-1.0/* ${datadir}/gstreamer-1.0/presets/GstFreeverb.prs"

PKG_lib${DPN} = "lib${DPN}-0"
PKG_${PN}-dev = "lib${DPN}-dev"
RPROVIDES_lib${DPN} = "lib${DPN}-0"
RPROVIDES_${PN}-dev = "lib${DPN}-dev"
