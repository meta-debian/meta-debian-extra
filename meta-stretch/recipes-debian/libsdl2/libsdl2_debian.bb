SUMMARY = "Simple DirectMedia Layer"
DESCRIPTION = "Simple DirectMedia Layer is a cross-platform multimedia \
library designed to provide low level access to audio, keyboard, mouse, \
joystick, 3D hardware via OpenGL, and 2D video framebuffer."
HOMEPAGE = "http://www.libsdl.org"
BUGTRACKER = "http://bugzilla.libsdl.org/"
SECTION = "libs"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=b2304ad7e91711027590d3f102a754b7"

PROVIDES = "libsdl2 libsdl2-dev"
PV = "2.0.5"

DEPENDS_class-nativesdk = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'virtual/nativesdk-libx11 nativesdk-libxrandr nativesdk-libxrender nativesdk-libxext', '', d)}"

SRC_URI += " \
    file://0001-prepend-our-sysroot-path-so-that-make-finds-our-wayl.patch \
    file://0002-Avoid-finding-build-host-s-wayland-scanner.patch \
    file://linkage.patch \
    file://fix-build-failure-on-ppc.patch \
"

S = "${WORKDIR}/SDL2-${PV}"

SRC_URI[md5sum] = "d4055424d556b4a908aa76fad63abd3c"
SRC_URI[sha256sum] = "442038cf55965969f2ff06d976031813de643af9c9edc9e331bd761c242e8785"

inherit autotools  binconfig pkgconfig
inherit debian-package

EXTRA_OECONF = "--disable-oss --disable-esd --disable-arts \
                --disable-diskaudio --disable-nas --disable-esd-shared --disable-esdtest \
                --disable-video-dummy \
                --enable-pthreads \
                --enable-sdl-dlopen \
                --disable-rpath \
               "
# opengl packageconfig factored out to make it easy for distros
# and BSP layers to pick either (desktop) opengl, gles2, or no GL
PACKAGECONFIG_GL ?= "${@bb.utils.contains('DISTRO_FEATURES', 'opengl','opengl', '', d)}"

PACKAGECONFIG ??= " \
    ${PACKAGECONFIG_GL} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'directfb', 'directfb', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland gles2', '', d)} \
"

PACKAGECONFIG[alsa]       = "--enable-alsa --disable-alsatest,--disable-alsa,alsa-lib,"
PACKAGECONFIG[directfb]   = "--enable-video-directfb,--disable-video-directfb,directfb"
PACKAGECONFIG[gles2]      = "--enable-video-opengles,--disable-video-opengles,virtual/libgles2"
PACKAGECONFIG[opengl]     = "--enable-video-opengl,--disable-video-opengl,virtual/libgl"
PACKAGECONFIG[pulseaudio] = "--enable-pulseaudio,--disable-pulseaudio,pulseaudio"
PACKAGECONFIG[tslib]      = "--enable-input-tslib,--disable-input-tslib,tslib"
PACKAGECONFIG[wayland]    = "--enable-video-wayland,--disable-video-wayland,wayland-native wayland wayland-protocols libxkbcommon"
PACKAGECONFIG[x11]        = "--enable-video-x11,--disable-video-x11,virtual/libx11 libxext libxrandr libxrender"

EXTRA_AUTORECONF += "--include=acinclude --exclude=autoheader"

do_configure_prepend() {
        # Remove old libtool macros.
        MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"
        for i in ${MACROS}; do
               rm -f ${S}/acinclude/$i
        done
	export WAYLAND_PROTOCOLS_SYSROOT_DIR="${STAGING_DIR_NATIVE}"
}

# correct the library path in the libsdl2.pc file
do_compile_append() {
	for i in $(find ${B} -name "*.pc") ; do
		sed -i -e s:${STAGING_DIR_TARGET}::g $i
	done

}

# remove some unnecessary files (*.la)
do_install_append() {
	rm -f ${D}${libdir}/libSDL2.la
}

FILES_${PN}     = "${libdir}/libSDL2-2.0.so.0 \
                   ${libdir}/libSDL2-2.0.so.0.4.1 \
                  "

FILES_${PN}-dev = "${bindir}/sdl2-config \
                   ${includedir}/SDL2/*.h \
                   ${libdir}/libSDL2-2.0.so \
                   ${libdir}/libSDL2.a \
                   ${libdir}/libSDL2.so \
                   ${libdir}/libSDL2_test.a \
                   ${libdir}/libSDL2main.a \
                   ${libdir}/cmake/SDL2/sdl2-config.cmake \
                   ${libdir}/pkgconfig/sdl2.pc \
                   ${datadir}/aclocal/sdl2.m4 \
                   ${libdir}/cmake \
                  "
