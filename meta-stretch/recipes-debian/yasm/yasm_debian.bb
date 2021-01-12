SUMMARY = "modular assembler with multiple syntaxes support"
DESCRIPTION = "Yasm is a complete rewrite of the NASM assembler. It supports multiple \
assembler syntaxes (eg, NASM, GAS, TASM, etc.) in addition to multiple \
output object formats (binary objects, COFF, Win32, ELF32, ELF64) and \
even multiple instruction sets (including AMD64). It also has an \
optimiser module."

HOMEPAGE = "http://www.tortall.net/projects/yasm/"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package
PV = "1.3.0"

LICENSE = "LGPLv2 & GPLv2 & BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "\
  file://GNU_LGPL-2.0;md5=7457a570966216337db2da2ee585d6a2 \
  file://GNU_GPL-2.0;md5=eb723b61539feef013de476e68b5c50a \
  file://BSD.txt;beginline=1;endline=20;md5=3cdcbd6faf1094d4812ca5bacde0b95d \
  file://BSD.txt;beginline=22;endline=44;md5=296f4229fda4b490637256a6bdfd956c"

DEPENDS = "bison-native"

inherit autotools gettext pythonnative

do_install_append() {
        rm -rf ${D}${nonarch_libdir}
        rm -rf ${D}${bindir}/vsyasm
}
