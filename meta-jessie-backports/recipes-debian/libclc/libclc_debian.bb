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

inherit autotools-brokensep

DEPENDS = "python llvm-toolchain-3.8"

KEEP_NONARCH_BASELIB = "1"
export YOCTO_ALTERNATE_EXE_PATH = "${STAGING_DIR_HOST}${nonarch_libdir}/llvm-3.8/bin"

do_configure(){
	cd ${S}
	./configure.py  --prefix=${prefix} --with-llvm-config=${STAGING_BINDIR_NATIVE}/llvm-config-3.8
}

do_compile_prepend(){
	# Use clang, clang++, llvm-as command from llvm-toolchain native
	sed -i -e "s#${STAGING_LIBDIR}/llvm-3.8/bin/#${STAGING_LIBDIR_NATIVE}/llvm-3.8/bin/#g" \
		${S}/Makefile
}
do_install_append(){
	# Remove redundant files
	rm -rf ${D}${libdir}/clc/subnormal_use_default.bc \
	       ${D}${libdir}/clc/subnormal_disable.bc
}

PACKAGES =+ "${PN}-amdgcn ${PN}-ptx ${PN}-r600"

FILES_${PN}-amdgcn = "${libdir}/clc/*amdgcn*"
FILES_${PN}-ptx = "${libdir}/clc/*ptx*"
FILES_${PN}-r600 = "${libdir}/clc/*r600*"
