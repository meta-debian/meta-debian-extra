## Recipe for building python-websocket package
PR = "${INC_PR}.0"

require websocket-client.inc
inherit setuptools pythonnative

DEPENDS_${PN} =+ "python python-six"
RDEPENDS_${PN} += "python python-six"

PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_DIR}/dist-packages"

KEEP_NONARCH_BASELIB = "1"

do_install_append() {
	install -m 755 ${D}/${bindir}/wsdump.py ${D}/${bindir}/python2-wsdump
	rm ${D}/${bindir}/wsdump.py
}

FILES_${PN} = " \
	${bindir}/python2-wsdump \
	${PYTHON_SITEPACKAGES_DIR}/websocket/* \
	${PYTHON_SITEPACKAGES_DIR}/websocket_client*/* \
"

BBCLASSEXTEND = "native"
