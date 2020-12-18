# Base recipe: meta-debian/recipes-debian/libyaml/libyaml_debian.bb
# Base branch: morty

SUMMARY = "Fast YAML 1.1 parser and emitter library"
DESCRIPTION = "LibYAML is a C library for parsing and emitting data in YAML 1.1 \
a human-readable data serialization format."
HOMEPAGE = "http://pyyaml.org/wiki/LibYAML"

PV = "0.1.7"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package

# debian/srouce/format is 3.0 (quilt)
# but it does not include debian patches
DEBIAN_QUILT_PATCHES = ""
DEBIAN_PATCH_TYPE = "nopatch"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6015f088759b10e0bc2bf64898d4ae17"

inherit autotools

DEBIAN_NOAUTONAME_${PN}-dev = "1"

BBCLASSEXTEND = "native"
