## Recipe for building python-backports.ssl-match-hostname package

require backports-ssl-match-hostname.inc
inherit setuptools pythonnative

DEBIAN_QUILT_PATCHES = ""

DEPENDS_${PN} =+ "python"
RDEPENDS_${PN} += "python"

PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_DIR}/dist-packages"
do_install_append() {
	mv ${D}/${PYTHON_SITEPACKAGES_DIR}/backports.ssl_match_hostname-${PV}-py2.7.egg-info ${D}/${PYTHON_SITEPACKAGES_DIR}/backports.ssl_match_hostname-${PV}.egg-info
	rm ${D}/${PYTHON_SITEPACKAGES_DIR}/backports/__init__.pyc
	rm ${D}/${PYTHON_SITEPACKAGES_DIR}/backports/ssl_match_hostname/__init__.pyc
}

FILES_${PN} = "\
	${PYTHON_SITEPACKAGES_DIR}/backports/__init__.py \
	${PYTHON_SITEPACKAGES_DIR}/backports/ssl_match_hostname/__init__.py \
	${PYTHON_SITEPACKAGES_DIR}/backports.ssl_match_hostname-${PV}.egg-info \
"
