## Recipe for building python-msgpack package

SUMMARY = "Python 2 client for serialization of messagepack."
DESCRIPTION = "This package contains the Python extensions \
for MessagePack."
HOMEPAGE = "http://pypi.python.org/pypi/msgpack-python/"
PR = "r0"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "jessie-backports-master"

DEBIAN_QUILT_PATCHES = ""

inherit debian-package
PV = "0.4.6"
inherit setuptools

PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_DIR}/dist-packages"

export BUILD_SYS
export HOST_SYS

# License information
LICENSE = "ASFv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd9523181d9d4fbf7ffca52eaa2a5751"

DEPENDS_${PN} =+ "python  libgcc libstdc++"
RDEPENDS_${PN} =+ "python libgcc libstdc++"

FILES_${PN} = " \
	${PYTHON_SITEPACKAGES_DIR}/msgpack/__init__.py \
	${PYTHON_SITEPACKAGES_DIR}/msgpack/_packer.x86_64-linux-gnu.so \
	${PYTHON_SITEPACKAGES_DIR}/msgpack/_unpacker.x86_64-linux-gnu.so \
	${PYTHON_SITEPACKAGES_DIR}/msgpack/_version.py \
	${PYTHON_SITEPACKAGES_DIR}/msgpack/exceptions.py \
	${PYTHON_SITEPACKAGES_DIR}/msgpack/fallback.py \
	${PYTHON_SITEPACKAGES_DIR}/msgpack_python-0.4.6.egg-info/PKG-INFO \
	${PYTHON_SITEPACKAGES_DIR}/msgpack_python-0.4.6.egg-info/dependency_links.txt \
	${PYTHON_SITEPACKAGES_DIR}/msgpack_python-0.4.6.egg-info/top_level.txt \
"
