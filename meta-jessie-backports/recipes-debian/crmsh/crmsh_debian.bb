SUMMARY = "CRM shell for the pacemaker cluster manager"
DESCRIPTION = "The crm shell is an advanced command-line interface for High-Availability \
 cluster management in GNU/Linux. Effortlessly configure, manage and \
 troubleshoot your clusters from the command line, with full tab completion \
 and extensive help. crmsh also provides advanced features like low-level \
 cluster configuration, cluster scripting and package management, and \
 history exploration tools giving you an instant view of what your cluster \
 is doing."
HOMEPAGE = "http://crmsh.github.io/"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package

PR = "r0"
PV = "2.3.2"
LICENSE = "GPLv2+ & MIT"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
	file://crmsh/orderedset.py;endline=20;md5=d197d7a986883ab3cb2d725327b806e9 \
	"

DEPENDS += "pacemaker debconf"
inherit pythonnative
#export some variable from poky, to use for python command
export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR
export DEB_HOST_MULTIARCH

DEBIAN_MULTILIB_MANUAL = "1"

# base on debian/rules
EXTRA_OECONF += "--docdir=${docdir}/${PN} --htmldir=${docdir}/${PN}/html"

inherit autotools-brokensep
do_install_append() {
	# base on debian/rules
	rm ${D}${libdir}/python*/*/${PN}/*.pyc
}
# base on debian/postinst
pkg_postinst_${PN}() {
	. ${STAGING_DATADIR}/debconf/confmodule
	${PYTHON} -m compileall $D${PYTHON_SITEPACKAGES_DIR}/${PN}
}

FILES_${PN} += "${libdir}/*"
RDEPENDS_${PN} += "\
	pacemaker-cli-utils python-lxml python-yaml python-compression \
	python-datetime python-doctest python-json python-core python-shell \
	python-netclient python-io python-lang python-math python-profile \
	python-subprocess python-codecs python-tests python-pprint python-unixadmin \
	python-textutils python-crypt python-distutils python-difflib python-netserver\
	"

RRECOMMENDS_${PN} += "pacemaker"
