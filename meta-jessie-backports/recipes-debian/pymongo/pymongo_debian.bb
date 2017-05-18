SUMMARY = "Python interface to the MongoDB document-oriented database"
DESCRIPTION = "MongoDB is a high-performance, open source, schema-free \
 document-oriented data store. Pymongo provides an interface \
 to easily access it from Python."
HOMEPAGE = "http://api.mongodb.org/python/"

#Build pymongo with source code from jessie-backports-master branch
DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package

PV = "3.0.3"
LICENSE = "Apache-2.0 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2a944942e1496af1886903d274dedb13 \
                    file://bson/time64.c;endline=25;md5=7484f538668fd2a7dbcc6a9775fae179 \
                   "
# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit setuptools python3native

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS

do_install_append() {
	# remove unwanted files
	rm -rf `find ${D}${libdir} -type d -name "__pycache__"` \
	       ${D}${PYTHON_SITEPACKAGES_DIR}/*.egg-info
}

DEPENDS += "python3-setuptools-native"
PACKAGES =+ "python3-${PN}-ext python3-${PN} python3-gridfs \
             python3-bson-ext python3-bson"

FILES_python3-${PN} = "${PYTHON_SITEPACKAGES_DIR}/${PN}/*"
FILES_python3-${PN}-ext = "${PYTHON_SITEPACKAGES_DIR}/${PN}/_cmessage*.so"
FILES_python3-gridfs = "${PYTHON_SITEPACKAGES_DIR}/gridfs/*"
FILES_python3-bson = "${PYTHON_SITEPACKAGES_DIR}/bson/*"
FILES_python3-bson-ext = "${PYTHON_SITEPACKAGES_DIR}/bson/_cbson*.so"
FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/bson/.debug"

# follow debian/control
RDEPENDS_python3-${PN} += "python3-bson"
RRECOMMENDS_python3-${PN} += "python3-gridfs python3-${PN}-ext"
RDEPENDS_python3-${PN}-ext += "python3-bson python3-${PN}"
RRECOMMENDS_python3-${PN}-ext += "python3-gridfs"
RRECOMMENDS_python3-bson += "python3-bson-ext"
RDEPENDS_python3-bson-ext += "python3-bson"
