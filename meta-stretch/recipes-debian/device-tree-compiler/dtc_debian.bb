SUMMARY = "Device Tree Compiler for Flat Device Trees"
DESCRIPTION = "Device Tree Compiler, dtc, takes as input \
a device-tree in a given format and outputs a device-tree \
in another format for booting kernels on embedded systems.\
"
HOMEPAGE = "https://git.kernel.org/cgit/utils/dtc/dtc.git"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"
DEBIAN_PATCH_TYPE = "quilt"

LICENSE = "MIT | GPLv2"
inherit debian-package
inherit pkgconfig autotools-brokensep

PR = "1"
PV = "1.4.2"
LIC_FILES_CHKSUM = "file://GPL;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS += "flex-native bison-native"

EXTRA_OEMAKE='PREFIX="${prefix}" LIBDIR="${libdir}"'

PACKAGES =+ "${PN}-misc"

FILES_${PN} = "${bindir}/*"
FILES_${PN}-misc = "${bindir}/convert-dtsv0 \
                    ${bindir}/ftdump \
                    ${bindir}/dtdiff \
"
DPN = "device-tree-compiler"
BBCLASSEXTEND = "native"
