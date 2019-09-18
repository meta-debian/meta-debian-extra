SUMMARY = "Video Acceleration (VA) API for Linux"
DESCRIPTION= "Video Acceleration API (VA API) is a library ("libVA") and API specification \
which enables and provides access to graphics hardware (GPU) acceleration for \
video processing on Linux and UNIX based operating systems. Accelerated \
processing includes video decoding, video encoding, subpicture blending and \
rendering. The specification was originally designed by Intel for its GMA \
(Graphics Media Accelerator) series of GPU hardware, the API is however not \
limited to GPUs or Intel specific hardware, as other hardware and manufacturers \
can also freely use this API for hardware accelerated video decoding. \
"

# Use jessie-backports-master branch
DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package

PV = "1.7.3"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
file://COPYING;md5=2e48940f94acb0af582e5ef03537800f \
file://va/x11/va_dri2.h;beginline=2;endline=30;md5=48c678be1248906d94c7496f795983fb"

DEPENDS = "libdrm libxext libxfixes virtual/mesa"

inherit autotools pkgconfig

EXTRA_OECONF = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', '--enable-wayland', '--disable-wayland', d)}"

PACKAGES =+ "${PN}-drm ${PN}-x11 ${PN}-tpi ${PN}-egl ${PN}-glx ${PN}-wayland vainfo"

RPROVIDES_${PN} += "libva-driver-abi-0.32 libva-driver-abi-0.33 libva-driver-abi-0.34 libva-driver-abi-0.35 \
			libva-driver-abi-0.36 libva-driver-abi-0.37 libva-driver-abi-0.38 libva-driver-abi-0.39"

DEBIANNAME_${PN} = "libva1"

RDEPENDS_${PN}-dev += "${PN}-drm ${PN}-egl ${PN}-glx ${PN}-tpi ${PN}-x11 ${PN}"
RDEPENDS_${PN}-drm += "${PN}"
RDEPENDS_${PN}-x11 += "${PN}"
RDEPENDS_${PN}-tpi += "${PN}"
RDEPENDS_${PN}-egl += "${PN}-x11"
RDEPENDS_${PN}-glx += "${PN}-x11"
RDEPENDS_vainfo += "${PN}"

FILES_${PN} += "${libdir}/dri"
FILES_${PN} =+ "${libdir}/*/libva${SOLIBS}"

FILES_${PN}-drm =+ "${libdir}/libva-drm*${SOLIBS}"
FILES_${PN}-x11 =+ "${libdir}/libva-x11*${SOLIBS}"
FILES_${PN}-tpi =+ "${libdir}/libva-tpi*${SOLIBS}"
FILES_${PN}-egl =+ "${libdir}/libva-egl*${SOLIBS}"
FILES_${PN}-glx =+ "${libdir}/libva-glx*${SOLIBS}"
FILES_${PN}-wayland =+ "${libdir}/libva-wayland*${SOLIBS}"
FILES_vainfo =+ "${bindir}/vainfo"
