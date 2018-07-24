DESCRIPTION = "Qt is a cross-platform C++ application framework. Qt's primary feature \
 is its rich set of widgets that provide standard GUI functionality."
HOMEPAGE = "http://qt-project.org/"

require qt5.inc
PV = "5.7.1"

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
    file://examples/quick/scenegraph/graph/graph.h;endline=39;md5=b0739af76072fbe303dc04b6941e054f \
"

DEPENDS += "qtbase-opensource-src"

SRC_URI += "\
    file://0002-qquickviewcomparison-fix-QCoreApplication-has-not-be.patch \
    file://0001-qmltestexample-fix-link.patch \
"

do_install_append() {
	# Remove libtool-like files.
	rm -f ${D}/${OE_QMAKE_PATH_LIBS}/*.la

	# Remove CMake files for plugins, we don't need them.
	rm -fv ${D}/${OE_QMAKE_PATH_LIBS}/cmake/Qt5Qml/*Plugin.cmake

	# Remove sysroots path in *.pc file.
	sed -i -e "s:${STAGING_DIR_TARGET}::g" ${D}${OE_QMAKE_PATH_LIBS}/pkgconfig/*.pc
}
PACKAGES = "${PN}-dbg ${PN}-staticdev \
            libqt5qml libqt5quick libqt5quickparticles libqt5quicktest \
            libqt5quickwidgets qml qml-module-qt-labs-folderlistmodel \
            qml-module-qt-labs-settings qml-module-qtqml-models2 \
            qml-module-qtqml-statemachine qml-module-qtquick-layouts \
            qml-module-qtquick-localstorage qml-module-qtquick-particles2 \
            qml-module-qtquick-window2 qml-module-qtquick-xmllistmodel \
            qml-module-qtquick2 qml-module-qttest qmlscene qt5-qmltooling-plugins \
            qtdeclarative5-dev-tools qtdeclarative5-examples \
            qtdeclarative5-private-dev ${PN}-dev"

FILES_libqt5qml = "\
    ${OE_QMAKE_PATH_LIBS}/libQt5Qml${SOLIBS} \
    ${OE_QMAKE_PATH_LIBS}/qt5/qml/QtQml/plugins.qmltypes \
    ${OE_QMAKE_PATH_LIBS}/qt5/qml/QtQml/qmldir \
    ${OE_QMAKE_PATH_LIBS}/qt5/qml/builtins.qmltypes \
    "
FILES_libqt5quick  = "${OE_QMAKE_PATH_LIBS}/libQt5Quick${SOLIBS}"
FILES_libqt5quickparticles = "${OE_QMAKE_PATH_LIBS}/libQt5QuickParticles${SOLIBS}"
FILES_libqt5quicktest = "${OE_QMAKE_PATH_LIBS}/libQt5QuickTest${SOLIBS}"
FILES_libqt5quickwidgets = "${OE_QMAKE_PATH_LIBS}/libQt5QuickWidgets${SOLIBS}"
FILES_qml = "${OE_QMAKE_PATH_QT_BINS}/qml"
FILES_qml-module-qt-labs-folderlistmodel = "${OE_QMAKE_PATH_LIBS}/qt5/qml/Qt/labs/folderlistmodel/*"
FILES_qml-module-qt-labs-settings = "${OE_QMAKE_PATH_LIBS}/qt5/qml/Qt/labs/settings/*"
FILES_qml-module-qtqml-models2 = "${OE_QMAKE_PATH_LIBS}/qt5/qml/QtQml/Models.2/*"
FILES_qml-module-qtqml-statemachine = "${OE_QMAKE_PATH_LIBS}/qt5/qml/QtQml/StateMachine/*"
FILES_qml-module-qtquick-layouts = "${OE_QMAKE_PATH_LIBS}/qt5/qml/QtQuick/Layouts/*"
FILES_qml-module-qtquick-localstorage = "${OE_QMAKE_PATH_LIBS}/qt5/qml/QtQuick/LocalStorage/*"
FILES_qml-module-qtquick-particles2 = "${OE_QMAKE_PATH_LIBS}/qt5/qml/QtQuick/Particles.2/*"
FILES_qml-module-qtquick-window2 = "${OE_QMAKE_PATH_LIBS}/qt5/qml/QtQuick/Window.*/*"
FILES_qml-module-qtquick-xmllistmodel = "${OE_QMAKE_PATH_LIBS}/qt5/qml/QtQuick/XmlListModel/*"
FILES_qml-module-qtquick2 = "${OE_QMAKE_PATH_LIBS}/qt5/qml/QtQuick.*/*"
FILES_qml-module-qttest = "${OE_QMAKE_PATH_LIBS}/qt5/qml/QtTest/*"
FILES_qmlscene = "${OE_QMAKE_PATH_QT_BINS}/qmlscene"
FILES_qt5-qmltooling-plugins = "${OE_QMAKE_PATH_LIBS}/qt5/plugins/qmltooling/*"
FILES_qtdeclarative5-dev-tools = "${OE_QMAKE_PATH_QT_BINS}/*"
FILES_qtdeclarative5-examples = "${OE_QMAKE_PATH_LIBS}/qt5/examples/*"
FILES_qtdeclarative5-private-dev = "\
    ${OE_QMAKE_PATH_HEADERS}/QtPacketProtocol/${PV}/* \
    ${OE_QMAKE_PATH_HEADERS}/QtQml/${PV}/* \
    ${OE_QMAKE_PATH_HEADERS}/QtQmlDebug/${PV}/* \
    ${OE_QMAKE_PATH_HEADERS}/QtQmlDevTools/${PV}/* \
    ${OE_QMAKE_PATH_HEADERS}/QtQuick/${PV}/* \
    ${OE_QMAKE_PATH_HEADERS}/QtQuickParticles/${PV}/* \
    ${OE_QMAKE_PATH_HEADERS}/QtQuickTest/${PV}/* \
    ${OE_QMAKE_PATH_HEADERS}/QtQuickWidgets/${PV}/* \
    ${OE_QMAKE_PATH_LIBS}/qt5/mkspecs/* \
    "
FILES_${PN}-dev += "\
    ${OE_QMAKE_PATH_LIBS}/*.prl \
    ${OE_QMAKE_PATH_LIBS}/cmake/* \
"

RPROVIDES_libqt5qml += "libqt5qml5"
RPROVIDES_libqt5quick += "libqt5quick5"
RPROVIDES_libqt5quickparticles += "libqt5quickparticles5"
RPROVIDES_libqt5quicktest += "libqt5quicktest5"
RPROVIDES_libqt5quickwidgets += "libqt5quickwidgets5"
DEBIANNAME_${PN}-dev = "qtdeclarative5-dev"
RPROVIDES_${PN}-dev += "qtdeclarative5-dev"

RDEPENDS_qtdeclarative5-examples += "\
    qml-module-qtqml-models2 \
    qml-module-qtquick-layouts \
    qml-module-qtquick-localstorage \
    qml-module-qtquick-particles2 \
    qml-module-qtquick-xmllistmodel \
    qml-module-qttest \
"
