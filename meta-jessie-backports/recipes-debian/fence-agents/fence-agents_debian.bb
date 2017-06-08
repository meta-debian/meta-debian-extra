SUMMARY = "Fence Agents for Red Hat Cluster"
DESCRIPTION = " \
	Red Hat Fence Agents is a collection of scripts to handle remote\
	power management for several devices.  They allow failed or\
	unreachable nodes to be forcibly restarted and removed from the\
	cluster."
HOMEPAGE = "https://fedorahosted.org/cluster/wiki/HomePage"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "jessie-backports-master"

PR = "r1"

inherit debian-package
inherit autotools pythonnative
PV = "4.0.25"

DEPENDS += "autoconf automake python-suds-native python-pexpect-native \
            python-pycurl-native python-requests-native"

RDEPENDS_${PN} = "\
	perl \
	python-pexpect \
	python-pycurl \
	python \
	"
RRECOMMENDS_${PN} += "\
	libnet-telnet-perl \
	openssh-client \
	snmp \
	"
RSUGGESTS_${PN} = "\
	python-requests \
	python-suds \
	"
LICENSE = "GPL-2+ & LGPL-2.1+"
LIC_FILES_CHKSUM = "\
	file://doc/COPYING.applications;md5=751419260aa954499f7abaabaa882bbe \
	file://doc/COPYING.libraries;md5=2d5025d4aa3495befef8f17206a5b0a1"

# skip creating fence_kdump.8 to enable cross build
SRC_URI += "file://0001-skip-creating-fence_kdump.8.patch"

# Enable to specify python binary
do_configure_prepend() {
	sed -i -e "156i\AM_PATH_PYTHON(,, :)" ${S}/configure.ac
}

do_compile_prepend() {
	find ${S}/fence/agents/lib -name "*.py" -exec sed -i -e "s|#!/usr/bin/python.*$|#!/usr/bin/env python|" {} \;
}

FILES_${PN} += "/run ${datadir}"
# Avoid a parallel build problem
PARALLEL_MAKE = ""
