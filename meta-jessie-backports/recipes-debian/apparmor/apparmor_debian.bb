# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "jessie-backports-master"

SUMMARY = "User-space parser utility for AppArmor"
HOMEPAGE = "http:/wiki.apparmor.net/index.php/Main_Page"
DESCRIPTION = "This provides the system initialization scripts needed to use the \
 AppArmor Mandatory Access Control system, including the AppArmor Parser \
 which is required to convert AppArmor text profiles into machine-readable \
 policies that are loaded into the kernel for use with the AppArmor Linux \
 Security Module."
LICENSE = "GPLv2 & LGPLv2.1 & (BSD | GPLv2+)"
LIC_FILES_CHKSUM = " \
	file://LICENSE;md5=fd57a4b0bc782d7b80fd431f10bbf9d0 \
	file://libraries/libapparmor/COPYING.LGPL;md5=a6f89e2100d9b6cdffcea4f398e37343 \
	file://changehat/pam_apparmor/COPYING;md5=963a391a5012d951e4fb14f3de32bd23 \
"

inherit debian-package autotools
inherit systemd pythonnative cpan-base perlnative

PR = "r0"
PV = "2.10.95"

# 0001-Fix-autogen.patch: Fix autogen.sh to use libtools under sysroots
SRC_URI += "file://0001-Fix-autogen.patch"

DEPENDS += " flex-native python bison-native swig-native apache2 ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"
# These are needed for --with-python
export STAGING_INCDIR
export STAGING_LIBDIR
export BUILD_SYS
export HOST_SYS
export DEB_HOST_MULTIARCH

# These are needed for --with-perl
# Env var which tells perl if it should use host (no) or target (yes) settings
export PERLCONFIGTARGET = "${@is_target(d)}"

# Env var which tells perl where the perl include files are
PERL_OWN_DIR_class-target = "/${@os.path.relpath(nonarch_libdir, libdir)}"
export PERL_INC = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}/CORE"
export PERL_LIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"
export PERL_ARCHLIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"
export PERLHOSTLIB = "${STAGING_LIBDIR_NATIVE}/perl-native/perl/${@get_perl_version(d)}/"

do_configure() {
	cd ${S}/libraries/libapparmor/
	export STAGING_BINDIR_NATIVE=${STAGING_BINDIR_NATIVE}
	export YACC="${STAGING_BINDIR_NATIVE}/bison -y"
	export FLEX="${STAGING_BINDIR_NATIVE}/flex"

	./autogen.sh
	${STAGING_BINDIR_NATIVE}/autoreconf --force --install

	# AppArmor's configure refuses to install --with-perl if cross-compiling
	# for no good reason. Disable that check.
	sed -i 's/test "$cross_compiling" = yes/false/' ./configure
	PERL="${PERL}" PYTHON="${PYTHON}" ./configure \
           --build=${BUILD_SYS} \
           --host=${HOST_SYS} \
           --target=${TARGET_SYS} \
           --prefix=${prefix} \
           --libdir=${libdir} \
           --mandir=${mandir} \
           --includedir=${includedir} \
           --with-perl --with-python
}

do_compile() {
	STAGING_INCDIR=${STAGING_INCDIR} \
	STAGING_LIBDIR=${STAGING_LIBDIR} \
	BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \

	PYTHON="${PYTHON}" ${MAKE} -C ${S}/libraries/libapparmor
	${MAKE} -C ${S}/utils
	sed -i -e "s|/usr/bin/bison|${STAGING_BINDIR_NATIVE}/bison|g" \
	    -e "s|/usr/bin/flex|${STAGING_BINDIR_NATIVE}/flex|g" \
	    ${S}/parser/Makefile
	${MAKE} -C ${S}/parser apparmor_parser manpages
	${MAKE} -C ${S}/binutils
	${MAKE} -C ${S}/profiles
	${MAKE} -C ${S}/changehat/mod_apparmor
}

do_install() {
	${MAKE} "DESTDIR = ${D}" -C ${S}/libraries/libapparmor install
	${MAKE} "DESTDIR = ${D}" -C ${S}/parser install
	${MAKE} "DESTDIR = ${D}" -C ${S}/binutils install
	${MAKE} "DESTDIR = ${D}" -C ${S}/utils install
	${MAKE} "DESTDIR = ${D}" -C ${S}/changehat/mod_apparmor install
	if [ ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} = "pam" ];then
		${MAKE} "DESTDIR = ${D}" -C ${S}/changehat/pam_apparmor install
	fi

	install -d ${D}${sysconfdir}/init.d/
	install -d ${D}${sysconfdir}/init/
	install -d ${D}${sysconfdir}/xdg/autostart/
	cp -r ${S}/profiles/apparmor.d/ ${D}${sysconfdir}/
	install -m 0755 ${S}/debian/apparmor.init ${D}${sysconfdir}/init.d/apparmor
	install -m 0644 ${S}/debian/apparmor.upstart ${D}${sysconfdir}/init/apparmor.conf
	install -m 0644 ${S}/debian/lib/apparmor/functions ${D}${nonarch_base_libdir}/${PN}/

	install -m 0644 ${S}/debian/notify/apparmor-notify.desktop ${D}${sysconfdir}/xdg/autostart/
	mv ${D}${sbindir}/aa-notify ${D}${bindir}/
	mv ${D}${bindir}/aa-exec ${D}${sbindir}/

	install -d ${D}${nonarch_libdir}/perl5/"$(echo ${@get_perl_version(d)} | cut -d . -f 1,2)"
	mv ${D}${nonarch_libdir}/perl/*/*/* ${D}${nonarch_libdir}/perl5/"$(echo ${@get_perl_version(d)} | cut -d . -f 1,2)"
	rm -rf ${D}${nonarch_libdir}/perl/

	cp -rf ${S}/debian/etc/apache2/	${D}${sysconfdir}/
	rm -rf ${D}${bindir}/aa-enabled ${D}${localstatedir}

	# from debian/apparmor.dirs
	install -d -m 0700 ${D}${sysconfdir}/${PN}.d/cache
}

PACKAGES =+ "${PN}-easyprof ${PN}-notify libapache2-mod-apparmor ${PN}-profiles ${PN}-utils \
libapparmor-dev libapparmor-perl libapparmor libpam-apparmor python-apparmor python-libapparmor "

FILES_${PN} =+ " ${nonarch_base_libdir}/${PN}/* "

FILES_${PN}-easyprof = " \
	${sysconfdir}/${PN}/easyprof.conf \
	${bindir}/aa-easyprof \
	${datadir}/${PN}/easyprof/ \
"

FILES_${PN}-notify = " \
	${sysconfdir}/${PN}/notify.conf \
	${sysconfdir}/xdg/autostart/apparmor-notify.desktop \
	${bindir}/aa-notify \
"

FILES_${PN}-profiles = " ${sysconfdir}/apparmor.d/apache2.d/* \
	${sysconfdir}/apparmor.d/usr.sbin.* \
	${sysconfdir}/apparmor.d/usr.bin.* \
	${sysconfdir}/apparmor.d/usr.lib.* \
	${sysconfdir}/apparmor.d/bin.ping \
	${sysconfdir}/apparmor.d/sbin.klogd \
	${sysconfdir}/apparmor.d/sbin.syslog* \
"

FILES_${PN}-utils = " \
	${sysconfdir}/${PN}/logprof.conf \
	${sysconfdir}/${PN}/severity.db \
	${sbindir}/aa-audit \
	${sbindir}/aa-autodep \
	${sbindir}/aa-cleanprof \
	${sbindir}/aa-complain \
	${sbindir}/aa-decode \
	${sbindir}/aa-disable \
	${sbindir}/aa-enforce \
	${sbindir}/aa-genprof \
	${sbindir}/aa-logprof \
	${sbindir}/aa-mergeprof \
	${sbindir}/aa-unconfined \
"

FILES_libapache2-mod-apparmor = " \
	${sysconfdir}/apache2/mods-available/apparmor.load \
	${sysconfdir}/apparmor.d/local/usr.sbin.apache2 \
	${sysconfdir}/apparmor.d/usr.sbin.apache2 \
	${nonarch_libdir}/apache2/modules/*${SOLIBSDEV} \
"

FILES_libapparmor-dev = " \
	${includedir}/aalogparse/aalogparse.h \
	${includedir}/sys/apparmor.h \
	${libdir}/*${SOLIBSDEV} \
	${libdir}/pkgconfig/* \
"

FILES_libapparmor-perl = " ${nonarch_libdir}/perl5/* "

FILES_libapparmor = " ${libdir}/*${SOLIBS} "

FILES_libpam-apparmor = " ${nonarch_base_libdir}/security/ "

FILES_python-apparmor = " ${PYTHON_SITEPACKAGES_DIR}/apparmor* "

FILES_python-libapparmor = " ${PYTHON_SITEPACKAGES_DIR}/* "

DEBIANNAME_libapparmor = "libapparmor1"
RPROVIDES_libapparmor += " libapparmor1"

# Avoid generated binaries stripping.
#       Avoid ERROR: Function failed: split_and_strip_files in do_package
INHIBIT_PACKAGE_STRIP = "1"

RDEPENDS_${PN} += " libapparmor-perl lsb-base"
RDEPENDS_${PN}-utils += " ${PN} libapparmor-perl"
RDEPENDS_${PN}-profiles += " ${PN}"
RDEPENDS_libapparmor-dev += " libapparmor"

inherit ptest
SRC_URI += "file://run-ptest"
RDEPENDS_${PN}-ptest += "apparmor"
TST_PARSER="tst_regex tst_misc tst_symtab tst_variable tst_lib"
TST_LIBRARIES="tst_aalogmisc tst_features tst_kernel"
do_compile_ptest() {
	oe_runmake -C ${S}/parser ${TST_PARSER} V=1
	oe_runmake -C ${S}/parser/tst gen_xtrans gen_dbus V=1
	oe_runmake -C ${S}/libraries/libapparmor/src ${TST_LIBRARIES} V=1
}

do_install_ptest() {
	for f in ${TST_PARSER}
	do
		install ${S}/parser/${f} ${D}${PTEST_PATH}
	done
	for f in ${TST_LIBRARIES}
	do
		install ${S}/libraries/libapparmor/src/${f} ${D}${PTEST_PATH}
	done
	cp -rp ${S}/parser/tst/* ${D}${PTEST_PATH}
}

# pam_apparmor.so is installed in /lib/security while 'base_libdir' is /lib/<triplet>.
# This is not issue. The QA Warning can be ignore:
#   | apparmor-2.10.95-r0 do_package_qa: QA Issue: libpam-apparmor: found library in wrong location: /lib/security/pam_apparmor.so
#   | apparmor-dbg: found library in wrong location: /lib/security/.debug/pam_apparmor.so [libdir]
INSANE_SKIP_libpam-apparmor = "libdir"
INSANE_SKIP_${PN}-dbg = "libdir"
