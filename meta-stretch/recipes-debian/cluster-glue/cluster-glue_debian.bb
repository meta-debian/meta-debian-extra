PR = "r1"

# Build cluster-glue with source code from stretch-master branch
DEBIAN_GIT_BRANCH = "stretch-master"
inherit debian-package
PV = "1.0.12"

LICENSE = "GPLv2+ & LGPLv2.1+ & BSD-4-Clause"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
    file://COPYING.LIB;md5=243b725d71bb5df4a1e5920b344b86ad \
    file://replace/daemon.c;beginline=5;endline=39;md5=291f4ddf5018e77f845619881b468f34"

DEPENDS = "libxml2 libtool glib-2.0 bzip2 util-linux libxslt \
           openhpi net-snmp dbus dbus-glib"

inherit autotools-brokensep pkgconfig useradd

SRC_URI_append = " \
    file://0001-don-t-compile-doc-and-Error-Fix.patch \
"

EXTRA_OECONF += "\
	--enable-upstart --with-ocf-root=${libdir}/ocf\
	--disable-fatal-warnings --enable-doc=no \
	--with-initdir=${sysconfdir}/init.d"

do_configure_prepend() {
        cd ${S}
        ./autogen.sh && cd -
}

do_install() {
	oe_runmake install DESTDIR=${D}
	install -d ${D}${sysconfdir}/init.d
	install -m 755 ${S}/debian/cluster-glue.logd.init ${D}${sysconfdir}/init.d/logd
	cp -ax ${S}/logd/logd.cf ${D}${sysconfdir}
}

PACKAGES =+ "liblrm liblrm-dev libplumb libplumb-dev libpils \
	libpils-dev libplumbgpl libstonith libstonith-dev"

FILES_${PN} += " \
	${libdir}/stonith/plugins/xen0-ha-dom0-stonith-helper \
	${libdir}/stonith/plugins/external \
	${libdir}/stonith/plugins/stonith2/ribcl.py \
	${libdir}/stonith/plugins/stonith2/*.so \
	${libdir}/stonith/plugins/external/* \
	${libdir}/heartbeat/logtest \
	${libdir}/heartbeat/ha_logd \
	${libdir}/heartbeat/ipctest \
	${libdir}/heartbeat/base64_md5_test \
	${libdir}/heartbeat/transient-test.sh \
	${libdir}/heartbeat/lrmd \
	${libdir}/heartbeat/ipctransientclient \
	${libdir}/heartbeat/ipctransientserver \
	${libdir}/heartbeat/plugins/RAExec/*.so \
	${libdir}/heartbeat/plugins/test/*.so \
	${libdir}/heartbeat/plugins/compress/*.so \
	${libdir}/heartbeat/plugins/InterfaceMgr/*.so \
	"

FILES_${PN}-dbg += " \
	${libdir}/stonith/plugins/stonith2/.debug/* \
	${libdir}/heartbeat/.debug/* \
	${libdir}/heartbeat/plugins/RAExec/.debug/* \
	${libdir}/heartbeat/plugins/test/.debug/* \
	${libdir}/heartbeat/plugins/compress/.debug/* \
	${libdir}/heartbeat/plugins/InterfaceMgr/.debug/* \
	"

FILES_${PN}-dev += " \
	${libdir}/stonith/plugins/stonith2/*.la \
	${libdir}/heartbeat/plugins/RAExec/*.la \
	${libdir}/heartbeat/plugins/test/*.la \
	${libdir}/heartbeat/plugins/compress/*.la \
	${libdir}/heartbeat/plugins/InterfaceMgr/*.la \
	"

FILES_${PN}-staticdev += " \
	${libdir}/heartbeat/plugins/InterfaceMgr/*.a \
	${libdir}/heartbeat/plugins/compress/*.a \
	${libdir}/heartbeat/plugins/test/*.a \
	${libdir}/heartbeat/plugins/RAExec/*.a \
	${libdir}/stonith/plugins/stonith2/*.a \
	"
FILES_liblrm = " \
	${libdir}/liblrm${SOLIBS} \
	"

FILES_liblrm-dev = "${includedir}/heartbeat/lrm"

FILES_libplumb = " \
	${libdir}/libplumb${SOLIBS} \
	"

FILES_libplumb-dev = "${includedir}/clplumbing"

FILES_libpils = " \
	${libdir}/libpils${SOLIBS} \
	"

FILES_libpils-dev = "${includedir}/pils"

FILES_libplumbgpl = " \
	${libdir}/libplumbgpl${SOLIBS} \
	"

FILES_libstonith = " \
	${libdir}/libstonith${SOLIBS} \
	"

FILES_libstonith-dev = "${includedir}/stonith"

DEBIANNAME_liblrm-dev = "liblrm2-dev"
DEBIANNAME_libplumb-dev = "libplumb2-dev"
DEBIANNAME_libpils-dev = "libpils2-dev"
DEBIANNAME_libstonith-dev = "libstonith1-dev"

# follow debian/control
RDEPENDS_${PN} += "libtimedate-perl liblrm libpils libplumb libplumbgpl libstonith"

# Base on debian/cluster-glue.postinst
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r haclient"
USERADD_PARAM_${PN}= "-r -g haclient --home /var/lib/pacemaker \
                              --no-create-home hacluster \
                              "
pkg_postinst_${PN}() {
	chown hacluster:haclient $D${localstatedir}/lib/heartbeat/cores/hacluster
	chown nobody:nogroup $D${localstatedir}/lib/heartbeat/cores/nobody
}

FILES_${PN} += "${systemd_system_unitdir}"
RDEPENDS_${PN} += "adduser bzip2"
