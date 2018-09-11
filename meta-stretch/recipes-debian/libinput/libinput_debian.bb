SUMMARY = "input device management and event handling library"
DESCRIPTION = "libinput is a library that handles input devices for display servers and other applications that need to directly deal with input devices.\n\
.\n\
It provides device detection, device handling, input device event\n\
processing and abstraction so minimize the amount of custom input\n\
code the user of libinput need to provide the common set of\n\
functionality that users expect."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libinput/"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

LICENSE = "MIT"
inherit debian-package
inherit autotools pkgconfig

PR = "r1"
PV = "1.6.3"
LIC_FILES_CHKSUM = "file://COPYING;md5=bb3d4b05b7549e0038fad80ef6940844"

PACKAGES += "${PN}-bin"

DEPENDS += "libevdev mtdev libwacom udev"
DEPENDS_${PN}-bin += "udev libwacom"
DEPENDS_${PN}-dev += "libudev-dev ${PN}"

# source format is 3.0 (quilt) but there is no debian/patches
DEBIAN_QUILT_PATCHES = ""

DEBIANNAME_${PN} = "${PN}10"
BBCLASSEXTEND = "native"
