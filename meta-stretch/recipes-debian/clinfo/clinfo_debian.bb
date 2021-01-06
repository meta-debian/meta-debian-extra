LICENSE = "PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=069146d1c0028c0ef02b59ad670eec54"

# Use strech-master branch
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package

PV = "2.1.16.01.12"

# Source format is "3.0 (quilt)" but no patch exist.
DEBIAN_QUILT_PATCHES = ""

DEPENDS = "ocl-icd khronos-opencl-headers"

do_install() {
	install -d ${D}/${bindir}
	install -m 0755 ${S}/clinfo ${D}/${bindir}
}
