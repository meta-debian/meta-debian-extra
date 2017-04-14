require flask-restful.inc

PR = "${INC_PR}.1"

inherit python3native
DEPENDS += "python3-setuptools-native"
RDEPENDS_${PN} += "python3-aniso8601 python3-flask python3-six python3-tz"
