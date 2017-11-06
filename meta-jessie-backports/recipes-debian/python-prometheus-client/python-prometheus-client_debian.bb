## Recipe for building python-prometeus-client package

SUMMARY = "Python 2 client for the Prometheus monitoring system"
DESCRIPTION = "This library provides an API for exporting metrics \
from a Python application. It provides classes for the metric types, \
and an HTTP server to expose the metrics to Prometheus.\
"
HOMEPAGE = "https://github.com/prometheus/client_python"
PR = "r0"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "jessie-backports-master"

DEBIAN_QUILT_PATCHES = ""

inherit debian-package
PV = "0.0.18"
inherit setuptools

PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_DIR}/dist-packages"

export BUILD_SYS
export HOST_SYS

# License information
LICENSE = "ASFv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS_${PN} =+ "python"
RDEPENDS_${PN} =+ "python"

FILES_${PN} = " \
	${PYTHON_SITEPACKAGES_DIR}/__init__.py \
	${PYTHON_SITEPACKAGES_DIR}/core.py \
	${PYTHON_SITEPACKAGES_DIR}/decorator.py \
	${PYTHON_SITEPACKAGES_DIR}/exposition.py \
	${PYTHON_SITEPACKAGES_DIR}/multiprocess.py \
	${PYTHON_SITEPACKAGES_DIR}/parser.py \
	${PYTHON_SITEPACKAGES_DIR}/process_collector.py \
	${PYTHON_SITEPACKAGES_DIR}/bridge/__init__.py \
	${PYTHON_SITEPACKAGES_DIR}/bridge/graphite.py \
	${PYTHON_SITEPACKAGES_DIR}/twisted/__init__.py \
	${PYTHON_SITEPACKAGES_DIR}/twisted/_exposition.py \
"
