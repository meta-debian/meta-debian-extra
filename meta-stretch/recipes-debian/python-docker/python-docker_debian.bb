## Recipe for building python-ipaddress package

SUMMARY = "Python wrapper to access docker.io's control socket"
DESCRIPTION = "This package contains oodles of routines that \
aid in controlling docker.io over it's socket control, the same \
way the docker.io client controls the daemon.\
"
HOMEPAGE = "https://github.com/dotcloud/docker-py/"
PR = "r0"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package
PV = "1.9.0"
inherit setuptools pythonnative
PR = "1"

SRC_URI_append = " \
    file://setup.patch \
"

PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_DIR}/dist-packages"

export BUILD_SYS
export HOST_SYS
export DEB_HOST_MULTIARCH

DEBIAN_MULTILIB_MANUAL = "1"

do_install_append() {
	mv ${D}/${PYTHON_SITEPACKAGES_DIR}/docker_py-${PV}-py2.7.egg-info ${D}/${PYTHON_SITEPACKAGES_DIR}/docker_py-${PV}.egg-info
}

# License information
LICENSE = "PSFv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7f538584cc3407bf76042def7168548a"

DEPENDS_${PN} =+ "python python-backports.ssl-match-hostname python-ipaddress python-requests python-six python-websocket"
RDEPENDS_${PN} =+ "python python-backports.ssl-match-hostname python-ipaddress python-requests python-six python-websocket"

FILES_${PN} = " \
	${PYTHON_SITEPACKAGES_DIR}/docker/* \ 
	${PYTHON_SITEPACKAGES_DIR}/docker/api/* \ 
	${PYTHON_SITEPACKAGES_DIR}/docker/auth/* \ 
	${PYTHON_SITEPACKAGES_DIR}/docker/ssladapter/* \ 
	${PYTHON_SITEPACKAGES_DIR}/docker/transport/* \ 
	${PYTHON_SITEPACKAGES_DIR}/docker/utils/* \ 
	${PYTHON_SITEPACKAGES_DIR}/docker/utils/ports/* \ 
	${PYTHON_SITEPACKAGES_DIR}/docker_py-${PV}.egg-info/* \
"
BBCLASSEXTEND = "native"
