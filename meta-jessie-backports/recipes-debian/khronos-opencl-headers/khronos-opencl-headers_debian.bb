SUMMARY = "OpenCL (Open Computing Language) header files"
DESCRIPTION= "OpenCL (Open Computing Language) is a multi-vendor open standard for \
 general-purpose parallel programming of heterogeneous systems that include \
 CPUs, GPUs and other processors."
HOMEPAGE = "http://www.khronos.org/registry/cl/"

# Use jessie-backports-master branch
DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package

PV = "2.0~svn31815"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://opencl.h;beginline=2;endline=21;md5=160254230e1cbfa20b9fad6e8cea0048 \
"

do_install() {
	# Base on debian/opencl-headers.install
	install -d ${D}${includedir}/CL
	cp ${S}/opencl.h ${D}${includedir}/CL
	cp ${S}/cl*.h ${D}${includedir}/CL
	cp ${S}/cl*.hpp ${D}${includedir}/CL
}
PACKAGES = "opencl-headers"
FILES_opencl-headers = "${includedir}/CL"

BBCLASSEXTEND = "native"
