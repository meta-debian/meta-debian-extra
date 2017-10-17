## Recipe for building python-websocket package

SUMMARY = "WebSocket client library - Python 2.7"
DESCRIPTION = "websocket-client provides a low-level, \
synchronous API providing WebSocket client functionality \
to Python programs. It conforms to the WebSocket specification \
as standardized by the IETF in RFC 6455. \
"
HOMEPAGE = "https://github.com/liris/websocket-client"
SECTION = "python"
DPN = "websocket-client"

PR = "r0"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package 
PV = "0.37.0"

# License information
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=241615fe24b899ca6b6d20b9c4b5dbbd"

# Workaround about installing python..
export BUILD_SYS
export HOST_SYS
