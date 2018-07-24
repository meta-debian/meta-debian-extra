require qtwayland-opensource-src.inc

inherit native

do_configure() {
	${OE_QMAKE_QMAKE} ${OE_QMAKE_DEBUG_OUTPUT} -r ${S}/src/qtwaylandscanner
}

do_install() {
	oe_runmake install INSTALL_ROOT=${D}
}
