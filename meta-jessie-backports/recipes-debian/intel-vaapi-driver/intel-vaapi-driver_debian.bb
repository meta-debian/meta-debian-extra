#
#base recipe: http://git.yoctoproject.org/cgit/cgit.cgi/meta-intel/tree/recipes-multimedia/libva/intel-vaapi-driver_2.3.0.bb?h=master
#

SUMMARY = "VAAPI driver for Intel G45 & HD Graphics family"
DESCRIPTION = "The VA-API (Video Acceleration API) enables hardware accelerated video \
decode/encode at various entry-points (VLD, IDCT, Motion Compensation \
etc.) for the prevailing coding standards today (MPEG-2, MPEG-4 \
ASP/H.263, MPEG-4 AVC/H.264, and VC-1/WMV3). It provides an interface \
to fully expose the video decode capabilities in today's GPUs. \
"

# Use jessie-backports-master branch
DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package

PV = "1.7.3" 

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2e48940f94acb0af582e5ef03537800f" 

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

DEPENDS = "libva libdrm"

RPROVIDES_${PN} += "va-driver i965-va-driver"
PKG_${PN} = "i965-va-driver"

inherit autotools pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "x11", "x11", "", d)} \
                   ${@bb.utils.contains("DISTRO_FEATURES", "wayland", "wayland", "", d)}"
PACKAGECONFIG[x11] = "--enable-x11,--disable-x11"
PACKAGECONFIG[wayland] = "--enable-wayland,--disable-wayland,wayland wayland-native virtual/egl"

FILES_${PN} += "${libdir}/dri/*.so"
FILES_${PN}-dev += "${libdir}/dri/*.la"
FILES_${PN}-dbg += "${libdir}/dri/.debug"
