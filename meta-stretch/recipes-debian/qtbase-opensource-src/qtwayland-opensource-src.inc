DESCRIPTION = "QtWayland is a Qt 5 module that wraps the functionality of Wayland."
HOMEPAGE = "http://qt-project.org/"

require qt5.inc
PV = "5.7.1~20161021"

inherit qmake5

LICENSE = "GFDL-1.3 & BSD-3-Clause & ( GPL-3.0 & The-Qt-Company-GPL-Exception-1.0 | The-Qt-Company-Commercial ) & ( LGPL-2.1 | LGPL-3.0 | The-Qt-Company-Commercial ) & (GPL-2+ | LGPL-3.0)"
LIC_FILES_CHKSUM = " \
    file://LICENSE.LGPL3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
    file://LICENSE.LGPLv21;md5=4bfd28363f541b10d9f024181b8df516 \
    file://LICENSE.LGPLv3;md5=e0459b45c5c4840b353141a8bbed91f0 \
    file://LICENSE.GPL2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://LICENSE.GPL3;md5=d32239bcb673463ab874e80d47fae504 \
    file://LICENSE.GPL3-EXCEPT;md5=763d8c535a234d9a3fb682c7ecb6c073 \
    file://LICENSE.GPLv3;md5=88e2b9117e6be406b5ed6ee4ca99a705 \
    file://LGPL_EXCEPTION.txt;md5=9625233da42f9e0ce9d63651a9d97654 \
    file://LICENSE.FDL;md5=6d9f2a9af4c8b8c3c769f6cc1b6aaf7e \
    file://examples/wayland/minimal-cpp/main.cpp;endline=39;md5=70204fb0a11d3414629ca76dc7733f26 \
"

SRC_URI += "\
    file://0001-Install-the-qtwaylandscanner-tool-to-the-native-side.patch \
    file://0001-Fix-building-of-QWaylandIntegration-if-some-Qt5-feat.patch \
"

# Empty DEBIAN_QUILT_PATCHES to avoid error "debian/patches not found"
DEBIAN_QUILT_PATCHES = ""

DEPENDS += "qtbase-opensource-src-native wayland-native"
