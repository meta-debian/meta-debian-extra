SUMMARY = "REST API framework for Flask applications"
DESCRIPTION = "Flask-RESTful is an extension for Flask that adds support for quickly building \
 REST APIs. It is a lightweight abstraction that works with your existing \
 ORM/libraries. Flask-RESTful encourages best practices with minimal setup. \
 If you are familiar with Flask, Flask-RESTful should be easy to pick up."
HOMEPAGE = "https://github.com/twilio/flask-restful"

#Build flask-restful with source code from stretch-master branch
DEBIAN_GIT_BRANCH = "stretch-master"

DPN = "flask-restful"
INC_PR = "r0"
inherit debian-package

PV = "0.3.5"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=685bb55ed99a366bb431995f5eef2783"
inherit setuptools
do_install_append() {
	# remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
	find ${D}${libdir} -type f -name "SOURCES.txt" -exec rm -f {} \;
	rm -rf `find ${D}${libdir} -type d -name "__pycache__"`
}
