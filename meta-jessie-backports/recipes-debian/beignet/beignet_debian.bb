SUMMARY = "OpenCL library for Intel GPUs"
DESCRIPTION = "OpenCL (Open Computing Language) is a multivendor open standard for \
 general-purpose parallel programming of heterogeneous systems that include \
 CPUs, GPUs and other processors."

# Use jessie-backports-master branch
DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package

LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=6b566c5b4da35d474758324899cb4562 \
"

PV = "1.3.0"

inherit cmake pkgconfig python3native

DEPENDS_class-target = "llvm-toolchain-3.8-native beignet-native ocl-icd llvm-toolchain-3.8 \
            mesa libdrm ncurses libedit zlib khronos-opencl-headers \
            ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libxext libxfixes libx11', '', d)} \
"
DEPENDS_class-native = "llvm-toolchain-3.8-native khronos-opencl-headers-native ocl-icd-native"

SRC_URI += "file://Fix-llvm-paths.patch"
SRC_URI_append_class-native = " file://Reduced-dependency-native.patch"
SRC_URI_append_class-target = " file://Run-native-gbe_bin_generater-to-compile-built-in-kernels.patch"

EXTRA_OECMAKE += "-DENABLE_OPENCL_20=0 \
                  -DLLVM_LIBRARY_DIR=${STAGING_DIR_HOST}${nonarch_libdir}/llvm-3.8/lib \
                  -DPYTHON_EXECUTABLE=${PYTHON}"

EXTRA_OECMAKE_append_class-target = " -DSTANDALONE_GBE_COMPILER_DIR=${STAGING_BINDIR_NATIVE}"
export YOCTO_ALTERNATE_EXE_PATH = "${STAGING_DIR_HOST}${nonarch_libdir}/llvm-3.8/bin"

do_configure_prepend(){
	# Base on debian/rules
	find ${S}/include/CL/ -type f -not -name 'cl_intel.h' -delete

	# Correct LLVM path
	sed -i -e "s|##LLVM_LIBDIR##|${STAGING_DIR_HOST}${nonarch_libdir}/llvm-3.8/lib|" \
		${S}/backend/src/CMakeLists.txt
}

do_install_append_class-native() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/backend/src/gbe_bin_generater ${D}${bindir}
}

do_install_append(){
	# Base on beignet-dev.install
	install -d ${D}${datadir}/beignet/test_kernels/
	cp -r ${S}/kernels/* ${D}${datadir}/beignet/test_kernels/
}

FILES_${PN} = "\
    ${sysconfdir} \
    ${libdir}/beignet/beignet.bc \
    ${libdir}/beignet/beignet.pch \
    ${libdir}/beignet/beignet_20.* \
    ${libdir}/beignet/include/* \
    ${libdir}/beignet/libcl.so \
    ${libdir}/beignet/libgbe.so \
    ${libdir}/beignet/libgbeinterp.so \
    ${datadir}/metainfo/com.intel.beignet.metainfo.xml \
"
FILES_${PN}-dev += "\
    ${libdir}/beignet/utest_run \
    ${libdir}/beignet/libutests.so \
    ${datadir}/beignet/test_kernels/* \
"

RPROVIDES_${PN} += "opencl-icd"

# Provide Debian package name "${PN}-opencl-icd"
RPROVIDES_${PN} += "${PN}-opencl-icd"
PKG_${PN} = "${PN}-opencl-icd"

PARALLEL_MAKE = ""
BBCLASSEXTEND += "native"
