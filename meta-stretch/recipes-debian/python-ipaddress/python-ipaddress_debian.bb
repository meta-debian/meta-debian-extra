## Recipe for building python-ipaddress package

SUMMARY = "Backport of Python 3 ipaddress module (Python 2)"
DESCRIPTION = "This module is a backport of the stdlib \
'ipaddress' module from Python 3. The "ipaddress" module \
is in turn based off of the 'ipaddr' module available in the \
'python-ipaddr' but there are some important API differences; \
make sure you are using the right module.\
"
HOMEPAGE = "https://github.com/phihag/ipaddress"
PR = "r0"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

DEBIAN_QUILT_PATCHES = ""

inherit debian-package
PV = "1.0.17"
inherit setuptools

PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_DIR}/dist-packages"

export BUILD_SYS
export HOST_SYS
export DEB_HOST_MULTIARCH

DEBIAN_MULTILIB_MANUAL = "1"

do_install_append() {
	mv ${D}/${PYTHON_SITEPACKAGES_DIR}/ipaddress-${PV}-py2.7.egg-info ${D}/${PYTHON_SITEPACKAGES_DIR}/ipaddress-${PV}.egg-info
}

# License information
LICENSE = "PSFv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7f538584cc3407bf76042def7168548a"

DEPENDS_${PN} =+ "python"
RDEPENDS_${PN} =+ "python"

FILES_${PN} = " \
	${PYTHON_SITEPACKAGES_DIR}/ipaddress.py \
	${PYTHON_SITEPACKAGES_DIR}/ipaddress.pyc \
	${PYTHON_SITEPACKAGES_DIR}/ipaddress-${PV}.egg-info/* \
"
