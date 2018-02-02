## Recipe for building python-backports.ssl-match-hostname package

SUMMARY = "Backport of the Python 3.5 SSL hostname checking function"
DESCRIPTION = "The Secure Sockets layer is only actually secure \
if you check the hostname in the certificate returned by the server \
to which you are connecting, and verify that it matches to hostname \
that you are trying to reach. \
"
HOMEPAGE = "https://bitbucket.org/brandon/backports.ssl_match_hostname"
INC_PR = "r0"
SECTION = "python"
DPN = "backports.ssl-match-hostname"

inherit debian-package

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

PV = "3.5.0.1"

# License information
LICENSE = "PSFv2"
LIC_FILES_CHKSUM = "file://backports/ssl_match_hostname/LICENSE.txt;md5=27a27d83b5b4a5fdb34a152fc19c6166"


# Workaround for installing python packages.
export BUILD_SYS
export HOST_SYS
export DEB_HOST_MULTIARCH
