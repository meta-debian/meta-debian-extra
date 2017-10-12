## Recipe for building jq

SUMMARY = "lightweight and flexible command-line JSON processor"
DESCRIPTION = "jq is like sed for JSON data – you can use it to \
slice and filter and map and transform structured data with the \
same ease that sed, awk, grep and friends let you play with text. \
" 
HOMEPAGE = "https://github.com/stedolan/jq"

inherit debian-package
inherit autotools 

# License files
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=29dd0c35d7e391bb8d515eacf7592e00"

PR = "r0"
PV = "1.5+dfsg-1.3"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "jessie-backports-master"

PACKAGES =+ "lib${PN}"

DEPENDS_${PN} =+ "libonig-dev"
DEPENDS_lib${PN} =+ "libonig-dev"

RDEPENDS_${PN} =+ "libonig"
RDEPENDS_lib${PN} =+ "libonig"

FILES_${PN} = "${bindir}/jq \
"
FILES_lib${PN} = "${libdir}/*${SOLIBS} \
"

DEBIAN_PATCH_TYPE = "nopatch"

# Package name of libjq is libjq1, which is not adopted the debian 
# package name conventions.

DEBIAN_NOAUTONAME_lib${PN} = "1"
DEBIANNAME_lib${PN} = "lib${PN}1"

