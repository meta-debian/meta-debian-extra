# Base recipe: /meta/recipes-devtools/ruby/ruby_2.4.4.bb
# Base branch: morty

SUMMARY = "An interpreter of object-oriented scripting language"
DESCRIPTION = "Ruby is an interpreted scripting language for quick \
and easy object-oriented programming. It has many features to process \
text files and to do system management tasks (as in Perl). \
It is simple, straight-forward, and extensible. \
"
HOMEPAGE = "http://www.ruby-lang.org/"

# use strech-master branch
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package
PV = "2.3.3"

LICENSE = "Ruby | BSD | GPLv2"
LIC_FILES_CHKSUM = "\
  file://COPYING;md5=837b32593517ae48b9c3b5c87a5d288c \
  file://BSDL;md5=19aaf65c88a40b508d17ae4be539c4b5 \
  file://GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
"

DEPENDS = "ruby2.3-native zlib openssl tcl libyaml gdbm readline"
DEPENDS_class-native = "openssl-native libyaml-native"

inherit autotools

# it's unknown to configure script, but then passed to extconf.rb
# maybe it's not really needed as we're hardcoding the result with
# 0001-socket-extconf-hardcode-wide-getaddr-info-test-outco.patch
UNKNOWN_CONFIGURE_WHITELIST += "--enable-wide-getaddrinfo"

PACKAGECONFIG ??= ""
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'ipv6', '', d)}"

PACKAGECONFIG[valgrind] = "--with-valgrind=yes, --with-valgrind=no, valgrind"
PACKAGECONFIG[gmp] = "--with-gmp=yes, --with-gmp=no, gmp"
PACKAGECONFIG[ipv6] = ",--enable-wide-getaddrinfo,"

EXTRA_AUTORECONF += "--exclude=aclocal"

EXTRA_OECONF = "\
    --disable-versioned-paths \
    --disable-rpath \
    --disable-dtrace \
    --enable-shared \
    --enable-load-relative \
"
EXTRA_OECONF_class-target += "--with-baseruby=${STAGING_BINDIR_NATIVE}/ruby"

do_install() {
	oe_runmake 'DESTDIR=${D}' install
}

do_install_append_class-target() {
	chrpath --delete ${D}${bindir}/ruby
}

PACKAGES =+ "${PN}-rdoc lib${PN}"

RDEPENDS_${PN}-rdoc = "${PN}"
FILES_${PN}-rdoc += "${datadir}/ri ${docdir}/${PN}-doc"

FILES_lib${PN} += "${libdir}/ruby ${nonarch_base_libdir}/ruby "

FILES_${PN} += "${datadir}/rubygems"

BBCLASSEXTEND = "native"
