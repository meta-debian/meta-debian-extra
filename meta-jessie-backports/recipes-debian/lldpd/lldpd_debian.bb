SUMMARY = "Implementation of IEEE 802.1ab (LLDP)"
DESCIPTION = " \
	LLDP is an industry standard protocol designed to supplant \
	proprietary Link-Layer protocols such as Extreme's EDP (Extreme \
	Discovery Protocol) and CDP (Cisco Discovery Protocol). The goal of \
	LLDP is to provide an inter-vendor compatible mechanism to deliver \
	Link-Layer notifications to adjacent network devices. \
	This implementation provides LLDP sending and reception, supports \
	VLAN and includes an SNMP subagent that can interface to an SNMP \
	agent through AgentX protocol. \
	This daemon is also able to deal with CDP, SONMP, FDP and EDP \
	protocol. It also handles LLDP-MED extension. \
	"
HOMEPAGE = "http://vincentbernat.github.com/lldpd/"

inherit debian-package

PR = "r0"
PV = "0.9.5"

DEPENDS += "net-snmp pciutils libxml2 libevent libbsd check jansson"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "jessie-backports-master"

LICENSE = "ISC & BSD-3-Clause & APSL-2.0"
LIC_FILES_CHKSUM = " \
		file://LICENSE;md5=8ae98663bac55afe5d989919d296f28a \
		file://libevent/LICENSE;md5=45c5316ff684bcfe2f9f86d8b1279559 \
		file://include/osx/if_bridgevar.h;endline=102;md5=1e0980ab750549fcb4b9d62ff1e0cbcf \
		"

# source format is 3.0 but there is no patch
DEBIAN_QUILT_PATCHES = ""

inherit pkgconfig useradd

EXTRA_OECONF = "--enable-pie \
		--with-snmp \
		--with-xml \
		--with-json \
		--with-systemdsystemunitdir=${systemd_system_unitdir}"

inherit autotools

do_install_append() {
	rm -rf ${D}${nonarch_libdir}/sysusers.d

	install -d ${D}${sysconfdir}/default
	install -m install -m 644 ${S}/debian/lldpd.default ${D}${sysconfdir}/default/lldpd
	install -d ${D}${sysconfdir}/init
	install -m install -m 644 ${S}/debian/lldpd.upstart ${D}${sysconfdir}/init/lldpd.conf
	install -d ${D}${sysconfdir}/init.d
	install -m install -m 755 ${S}/debian/lldpd.init.d ${D}${sysconfdir}/init.d/lldpd
}

# Follow debian/lldpd.postinst
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r _lldpd"
USERADD_PARAM_${PN} = "-r -g _lldpd --home /var/run/lldpd --no-create-home _lldpd"

FILES_${PN} += "${datadir}/bash-completion/completions/lldpcli \
		${datadir}/zsh/vendor-completions/_lldpcli \
		${systemd_system_unitdir}/lldpd.service \
		${sysconfdir}/default/lldpd \
		${sysconfdir}/init/lldpd.conf \
		${sysconfdir}/init.d/lldpd"

# Follow debian/control
RDEPENDS_${PN} += "adduser lsb-base"
