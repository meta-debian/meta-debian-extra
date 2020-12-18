SUMMARY = "Generic OpenCL ICD Loader"
DESCRIPTION= "\
OpenCL (Open Computing Language) is a multivendor open standard for \
general-purpose parallel programming of heterogeneous systems that include \
CPUs, GPUs and other processors. \
"
HOMEPAGE = "https://forge.imag.fr/projects/ocl-icd/"

# Use strech-master branch
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package

PV = "2.2.11"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=232257bbf7320320725ca9529d3782ab \
    file://ocl_icd_loader.h;beginline=2;endline=24;md5=e8f444668356a52389ea9fa2959589b0 \
"
DEPENDS = "ruby2.3-native"

inherit autotools

PACKAGES =+ "${PN}-libopencl ${PN}-opencl-dev"

RPROVIDES_${PN}-libopencl += "${PN}-libopencl1"

FILES_${PN}-libopencl += "${libdir}/libOpenCL${SOLIBS}"

FILES_${PN}-opencl-dev += "${libdir}/libOpenCL${SOLIBSDEV}  ${datadir}/pkgconfig/OpenCL.pc"

BBCLASSEXTEND = "native"
