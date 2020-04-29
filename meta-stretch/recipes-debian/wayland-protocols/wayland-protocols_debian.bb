FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SUMMARY = "wayland compositor protocols"
DESCRIPTION = "Wayland protocols that add functionality not available in the Wayland core \
protocol. Such protocols either add completely new functionality, or extend \
the functionality of some other protocol either in Wayland core, or some other \
protocol in wayland-protocols."
HOMEPAGE = " http://wayland.freedesktop.org/"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package

PV = "1.7"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c7b12b6702da38ca028ace54aae3d484"

SRC_URI += "file://0001-wayland-protocol-locate-the-correct-libraries-with-p.patch"

inherit autotools

DEPENDS += "wayland"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

BBCLASSEXTEND = "native"
