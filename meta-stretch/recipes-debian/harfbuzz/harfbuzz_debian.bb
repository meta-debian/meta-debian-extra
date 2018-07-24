SUMMARY = "OpenType text shaping engine"
DESCRIPTION = "HarfBuzz is an implementation of the OpenType Layout engine (aka layout \
engine) and the script-specific logic (aka shaping engine)."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/HarfBuzz"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package
PV = "1.4.2"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=e021dd6dda6ff1e6b1044002fc662b9b \
    file://src/hb-ucdn/COPYING;md5=994ba0f1295f15b4bda4999a5bbeddef \
"

inherit autotools pkgconfig

EXTRA_OECONF = "--with-gobject --enable-static"

PACKAGECONFIG ?= "cairo freetype glib graphite2 icu"
PACKAGECONFIG[cairo] = "--with-cairo=yes, --with-cairo=no, cairo"
PACKAGECONFIG[freetype] = "--with-glib=yes, --with-glib=no, glib-2.0"
PACKAGECONFIG[glib] = "--with-freetype=yes, --with-freetype=no, freetype"
PACKAGECONFIG[graphite2] = "--with-graphite2=yes, --with-graphite2=no, graphite2"
PACKAGECONFIG[icu] = "--with-icu, --without-icu, icu"
PACKAGECONFIG[gtk-doc] = "--enable-gtk-doc,--disable-gtk-doc,gtk-doc"

PACKAGES =+ "libharfbuzz-bin libharfbuzz-icu libharfbuzz-gobject"
FILES_libharfbuzz-bin = "${bindir}/*"
FILES_libharfbuzz-icu = "${libdir}/libharfbuzz-icu${SOLIBS}"
FILES_libharfbuzz-gobject = "${libdir}/libharfbuzz-gobject${SOLIBS}"
