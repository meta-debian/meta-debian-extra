require flask-restful.inc

PR = "${INC_PR}.0"

DEPENDS += "python-setuptools-native"
RDEPENDS_${PN} += "python-aniso8601 python-flask python-six python-tz"
