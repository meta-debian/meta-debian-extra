SUMMARY=" OpenCL C language implementation"
DESCRIPTION="\
libclc is an open implementation of the OpenCL C programming language, \
as specified by the OpenCL 1.1 Specification. \
"
HOMEPAGE = "http://libclc.llvm.org"

DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package

PV="0.2.0+git20150813"

LICENSE = "NCSA & MIT"
LIC_FILES_CHKSUM = "\
  file://LICENSE.TXT;md5=3d5e39153f76a72ef2ced27e62d33511 \
"

DEPENDS = "python llvm-toolchain-3.8"

do_configure() {
	${S}/configure.py  --prefix=${prefix} --with-llvm-config=${STAGING_BINDIR_NATIVE}/llvm-config-3.8
}

# This recipe is used for generating libclc-dev package to build
# mesa-opencl-icd only
do_compile[noexec] = "1"

do_install_append(){
	mkdir -p ${D}${includedir}
	mkdir -p ${D}${datadir}/pkgconfig
	cp -r ${S}/generic/include/clc ${D}${includedir}
	cp -r ${S}/libclc.pc  ${D}${datadir}/pkgconfig
}

FILES_${PN}-dev = "\
  ${includedir}/clc \
  ${datadir}/pkgconfig/${PN}.pc \
"
