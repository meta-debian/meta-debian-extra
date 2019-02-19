#
# Base recipe: meta-debian/recipes-debian/xorg-driver/xf86-video-intel_debian.bb
# Base branch: morty
#
SUMMARY = "X.Org X server -- Intel i8xx, i9xx display driver"
DESCRIPTION = "This package provides the driver for the Intel i8xx and i9xx family \
 of chipsets, including i810, i815, i830, i845, i855, i865, i915, i945 \
 and i965 series chips."
HOMEPAGE = "http://www.x.org"

# Use jessie-backports-master branch
DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package

PV = "2.99.917+git20161206"
DPN = "xserver-xorg-video-intel"

LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=8730ad58d11c7bbad9a7066d69f7808e"

# There is no debian patch, but debian/rules keep using quilt.
DEBIAN_PATCH_TYPE = "quilt"

KEEP_NONARCH_BASELIB = "1"

inherit autotools pkgconfig

DEPENDS += "virtual/libx11 drm libpciaccess pixman \
            virtual/xserver xproto randrproto util-macros"

PACKAGECONFIG ??= "sna udev uxa valgrind xvmc \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'dri', '', d)}"

PACKAGECONFIG[dri] = "--enable-dri,--disable-dri,xf86driproto dri2proto"
PACKAGECONFIG[sna] = "--enable-sna,--disable-sna"
PACKAGECONFIG[uxa] = "--enable-uxa,--disable-uxa"
PACKAGECONFIG[udev] = "--enable-udev,--disable-udev,udev"
PACKAGECONFIG[valgrind] = "--enable-valgrind,--disable-valgrind,valgrind"
PACKAGECONFIG[xvmc] = "--enable-xvmc,--disable-xvmc,libxvmc"

# --enable-kms-only option is required by ROOTLESS_X
EXTRA_OECONF += '${@base_conditional( "ROOTLESS_X", "1", " --enable-kms-only", "", d )}'

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

do_install_append() {
	# base on debian/rules
	find ${D} -name '*.la' -delete
	rm -f ${D}${libdir}/libI810XvMC.so
	rm -f ${D}${libdir}/libIntelXvMC.so
}

# Function to add the relevant ABI dependency to drivers, which should be called
# from a populate_packages append/prepend.
def add_abi_depends(d, name):
    # Map of ABI names exposed in the dependencies to pkg-config variables
    abis = {
      "video": "abi_videodrv",
      "input": "abi_xinput"
    }

    output = os.popen("pkg-config xorg-server --variable=%s" % abis[name]).read()
    mlprefix = d.getVar('MLPREFIX', True) or ''
    abi = "%sxorg-abi-%s-%s" % (mlprefix, name, output.split(".")[0])

    pn = d.getVar("PN", True)
    d.appendVar('RDEPENDS_' + pn, ' ' + abi)


python populate_packages_prepend() {
    add_abi_depends(d, "video")
}

FILES_${PN} += "${datadir}/polkit-1/actions/* \
                ${libdir}/xorg/modules/drivers/*.so"

PKG_${PN} = "${DPN}"
PKG_${PN}-dev = "${DPN}-dev"
PKG_${PN}-dbg = "${DPN}-dbg"

RDEPENDS_${PN} += "xserver-xorg-core xserver-xorg-extension-glx"
RPROVIDES_${PN} += "${DPN}"
RPROVIDES_${PN}-dev += "${DPN}-dev"
