FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

#Fix build against EGL implementations that don't support the EGL_MESA_screen_surface extension
SRC_URI += "file://0001-mesa-demos-Fix-build-when-EGL_MESA_screen_surface-ex.patch"
