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

inherit debian-package
PV = "0.0.18"
inherit setuptools

DEBIAN_QUILT_PATCHES = ""

KEEP_NONARCH_BASELIB = "1"

export BUILD_SYS
export HOST_SYS
export DEB_HOST_MULTIARCH

# License information
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS_${PN} =+ "python"
RDEPENDS_${PN} =+ "python"

do_install_append() {
	rm -fr ${D}${PYTHON_SITEPACKAGES_DIR}/prometheus_client-${PV}-py2.7.egg-info/
	rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/prometheus_client/*.pyc
	rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/prometheus_client/bridge/*.pyc
	rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/prometheus_client/twisted/*.pyc
	rm -fr ${D}/usr/share/doc
}

FILES_${PN} = " \
	${PYTHON_SITEPACKAGES_DIR}/prometheus_client/__init__.py \
	${PYTHON_SITEPACKAGES_DIR}/prometheus_client/core.py \
	${PYTHON_SITEPACKAGES_DIR}/prometheus_client/decorator.py \
	${PYTHON_SITEPACKAGES_DIR}/prometheus_client/exposition.py \
	${PYTHON_SITEPACKAGES_DIR}/prometheus_client/multiprocess.py \
	${PYTHON_SITEPACKAGES_DIR}/prometheus_client/parser.py \
	${PYTHON_SITEPACKAGES_DIR}/prometheus_client/process_collector.py \
	${PYTHON_SITEPACKAGES_DIR}/prometheus_client/bridge/__init__.py \
	${PYTHON_SITEPACKAGES_DIR}/prometheus_client/bridge/graphite.py \
	${PYTHON_SITEPACKAGES_DIR}/prometheus_client/twisted/__init__.py \
	${PYTHON_SITEPACKAGES_DIR}/prometheus_client/twisted/_exposition.py \
"
