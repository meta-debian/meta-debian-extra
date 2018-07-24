#
# base recipe: meta-qt5/recipes-qt/qt5/qtwayland_git.bb
# base commit: f37449f25e3e58b76a64a4398d4f10ce7dc9206c
#

require qtwayland-opensource-src.inc

DEPENDS += "qtbase-opensource-src-native qtwayland-opensource-src-native wayland qtbase-opensource-src qtdeclarative-opensource-src"

#FIXME: xkb should be optional; we add it here to fix the build error without it
#       (https://bugreports.qt.io/browse/QTBUG-54851)
#compositor-api
PACKAGECONFIG ?= " \
    compositor-api \
    wayland-egl \
    xkb \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xcompositor xkb glx', '', d)} \
"

PACKAGECONFIG[compositor-api] = "CONFIG+=wayland-compositor"
PACKAGECONFIG[xcompositor] = "CONFIG+=config_xcomposite CONFIG+=done_config_xcomposite,CONFIG+=done_config_xcomposite,libxcomposite"
PACKAGECONFIG[glx] = "CONFIG+=config_glx CONFIG+=done_config_glx,CONFIG+=done_config_glx,virtual/mesa"
PACKAGECONFIG[xkb] = "CONFIG+=config_xkbcommon CONFIG+=done_config_xkbcommon,CONFIG+=done_config_xkbcommon,libxkbcommon xproto"
PACKAGECONFIG[wayland-egl] = "CONFIG+=config_wayland_egl CONFIG+=done_config_wayland_egl,CONFIG+=done_config_wayland_egl,virtual/egl"
PACKAGECONFIG[brcm-egl] = "CONFIG+=config_brcm_egl CONFIG+=done_config_brcm_egl,CONFIG+=done_config_brcm_egl,virtual/egl"
PACKAGECONFIG[drm-egl] = "CONFIG+=config_drm_egl_server CONFIG+=done_config_drm_egl_server,CONFIG+=done_config_drm_egl_server,libdrm virtual/egl"
PACKAGECONFIG[libhybris-egl] = "CONFIG+=config_libhybris_egl_server CONFIG+=done_config_libhybris_egl_server,CONFIG+=done_config_libhybris_egl_server,libhybris"

EXTRA_QMAKEVARS_PRE += "${PACKAGECONFIG_CONFARGS}"

do_install_append() {
	# libQt5WaylandClient library has no public API, do not ship development files for it
	rm -rfv ${D}${OE_QMAKE_PATH_HEADERS}/qt5/QtWaylandClient/
	rm -rfv ${D}${OE_QMAKE_PATH_LIBS}/cmake/Qt5WaylandClient/
	rm -fv ${D}${OE_QMAKE_PATH_LIBS}/libQt5WaylandClient.la
	rm -fv ${D}${OE_QMAKE_PATH_LIBS}/libQt5WaylandClient.prl
	rm -fv ${D}${OE_QMAKE_PATH_LIBS}/libQt5WaylandClient.so
	rm -fv ${D}${OE_QMAKE_PATH_LIBS}/pkgconfig/Qt5WaylandClient.pc
	rm -fv ${D}${OE_QMAKE_PATH_LIBS}/qt5/mkspecs/modules/qt_lib_waylandclient.pri

	# Remove libtool-like file
	rm -vf ${D}${OE_QMAKE_PATH_LIBS}/libQt5WaylandCompositor.la

	# Remove private elements.
	rm -rvf ${D}${OE_QMAKE_PATH_HEADERS}/qt5/QtWaylandCompositor/*/QtWaylandCompositor/private
	rm -fv ${D}${OE_QMAKE_PATH_LIBS}/qt5/mkspecs/modules/qt_lib_*_private.pri

	# Hack away weird defunct artifact created by build.
	rm -rf ${D}${OE_QMAKE_PATH_LIBS}/cmake/Qt5Gui

	# Remove sysroots path in *.pc file.
	sed -i -e "s:${STAGING_DIR_TARGET}::g" ${D}${OE_QMAKE_PATH_LIBS}/pkgconfig/*.pc
}
PACKAGES =+ "libqt5waylandclient libqt5waylandcompositor libqt5waylandcompositor-dev \
             qml-module-qtwayland-compositor qtwayland5-examples"

FILES_libqt5waylandclient = "${OE_QMAKE_PATH_LIBS}/libQt5WaylandClient${SOLIBS}"
FILES_libqt5waylandcompositor = "${OE_QMAKE_PATH_LIBS}/libQt5WaylandCompositor${SOLIBS}"
FILES_libqt5waylandcompositor-dev = "\
    ${OE_QMAKE_PATH_HEADERS}/qt5/QtWaylandCompositor/* \
    ${OE_QMAKE_PATH_LIBS}/cmake/Qt5WaylandCompositor/*.cmake \
    ${OE_QMAKE_PATH_LIBS}/libQt5WaylandCompositor.prl \
    ${OE_QMAKE_PATH_LIBS}/libQt5WaylandCompositor.so \
    ${OE_QMAKE_PATH_LIBS}/pkgconfig/Qt5WaylandCompositor.pc \
    ${OE_QMAKE_PATH_LIBS}/qt5/mkspecs/modules/qt_lib_waylandcompositor.pri \
    "
FILES_qml-module-qtwayland-compositor = "${OE_QMAKE_PATH_LIBS}/qt5/qml/QtWayland/Compositor/*"
FILES_qtwayland5-examples = "${OE_QMAKE_PATH_LIBS}/qt5/examples/*"
FILES_${PN} += "\
    ${OE_QMAKE_PATH_LIBS}/qt5/plugins/* \
    ${OE_QMAKE_PATH_QT_BINS}/qtwaylandscanner"

PKG_${PN} = "qtwayland5"
RPROVIDES_${PN} = "qtwayland5"
RPROVIDES_libqt5waylandclient = "libqt5waylandclient5"
RPROVIDES_libqt5waylandcompositor = "libqt5waylandcompositor5"

RDEPENDS_qtwayland5-examples += "qml-module-qtwayland-compositor"
