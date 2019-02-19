SUMMARY = "Userspace interface to the kernel DRM services"
DESCRIPTION = "The runtime library for accessing the kernel DRM services.  DRM \
stands for \"Direct Rendering Manager\", which is the kernel portion of the \
\"Direct Rendering Infrastructure\" (DRI).  DRI is required for many hardware \
accelerated OpenGL drivers."

# Use jessie-backports-master branch
DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
file://xf86drm.c;beginline=9;endline=32;md5=c8a3b961af7667c530816761e949dc71 \
"

PV = "2.4.74"

DEBIAN_PATCH_TYPE = "quilt"

PROVIDES = "drm"
DEPENDS = "libpthread-stubs udev libpciaccess valgrind libbsd"

inherit autotools pkgconfig

EXTRA_OECONF = " \
    --enable-radeon \
    --disable-libkms \
    --enable-udev \
    --enable-vmwgfx \
    --enable-nouveau \
    --enable-intel \
    --disable-omap-experimental-api \
    --disable-freedreno \
    --disable-exynos-experimental-api \
    --disable-tegra-experimental-api \
"
EXTRA_OECONF_append_arm = " \
    --disable-intel \
    --enable-omap-experimental-api \
    --enable-freedreno --enable-freedreno-kgsl \
    --enable-exynos-experimental-api \
    --enable-tegra-experimental-api \
"

do_install_append() {
	rm -rf ${D}${bindir}
}
PACKAGES =+ "${PN}-amdgpu ${PN}-exynos ${PN}-freedreno ${PN}-intel \
             ${PN}-nouveau ${PN}-omap ${PN}-radeon ${PN}-tegra"

FILES_${PN}-amdgpu = "${libdir}/libdrm_amdgpu${SOLIBS}"
FILES_${PN}-exynos = "${libdir}/libdrm_exynos${SOLIBS}"
FILES_${PN}-freedreno = "${libdir}/libdrm_freedreno${SOLIBS}"
FILES_${PN}-intel = "${libdir}/libdrm_intel${SOLIBS}"
FILES_${PN}-nouveau = "${libdir}/libdrm_nouveau${SOLIBS}"
FILES_${PN}-omap = "${libdir}/libdrm_omap${SOLIBS}"
FILES_${PN}-radeon = "${libdir}/libdrm_radeon${SOLIBS}"
FILES_${PN}-tegra = "${libdir}/libdrm_tegra${SOLIBS}"

RPROVIDES_${PN}-amdgpu = "${PN}-amdgpu1"
RPROVIDES_${PN}-exynos = "${PN}-exynos1"
RPROVIDES_${PN}-freedreno = "${PN}-freedreno1"
RPROVIDES_${PN}-intel = "${PN}-intel1"
RPROVIDES_${PN}-nouveau = "${PN}-nouveau2"
RPROVIDES_${PN}-omap = "${PN}-omap1"
RPROVIDES_${PN}-radeon = "${PN}-radeon1"
RPROVIDES_${PN}-tegra = "${PN}-tegra0"
