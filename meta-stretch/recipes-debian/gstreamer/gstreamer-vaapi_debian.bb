SUMMARY = "VA-API plugins for GStreamer"
DESCRIPTION = "Gstreamer-vaapi is a collection of GStreamer plugins and helper libraries that \
allow hardware accelerated video decoding, encoding and processing through \
VA-API."
HOMEPAGE = "https://github.com/01org/gstreamer-vaapi/"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package
PV = "1.10.4"

LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

inherit autotools gettext pkgconfig

DEBIAN_QUILT_PATCHES=""

DEPENDS = "libva gstreamer gst-plugins-base gst-plugins-bad yasm"

EXTRA_OECONF += "--disable-silent-rules"

FILES_${PN} += "${libdir}/gstreamer-*/libgstvaapi.so"

PACKAGECONFIG ??= "drm \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl x11', 'glx', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"

PACKAGECONFIG[drm] = "--enable-drm,--disable-drm,udev libdrm"
PACKAGECONFIG[glx] = "--enable-glx,--disable-glx,virtual/mesa"
PACKAGECONFIG[wayland] = "--enable-wayland,--disable-wayland,wayland"
PACKAGECONFIG[x11] = "--enable-x11,--disable-x11,virtual/libx11 libxrandr libxrender"

do_install_append() {
	rm -rf ${D}${libdir}/gstreamer-*/libgstvaapi.la
}
