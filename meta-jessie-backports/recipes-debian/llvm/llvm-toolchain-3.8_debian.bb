#
# base recipe: meta-debian/recipes-debian/llvm/llvm-toolchain-3.5_debian.bb
# base branch: morty
#

SUMMARY = "Low-Level Virtual Machine (LLVM)"
HOMEPAGE = "http://www.llvm.org/"

# Use jessie-backports-master branch
DEBIAN_GIT_BRANCH = "jessie-backports-master"

inherit debian-package
PV = "3.8.1"

# 3-clause BSD-like
# University of Illinois/NCSA Open Source License
LICENSE = "NCSA"
LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=4c0bc17c954e99fd547528d938832bfa"

DEPENDS_class-target = "libffi libxml2-native zlib binutils python libedit swig-native dpkg-native llvm-toolchain-3.8-native"
DEPENDS_class-native = "libffi-native libxml2-native zlib-native binutils-native python-native swig-native dpkg-native"

KEEP_NONARCH_BASELIB = "1"

inherit perlnative pythonnative cmake

SRC_URI += "file://0007-llvm-allow-env-override-of-exe-path.patch"

LLVM_VERSION = "3.8"
LLVM_VERSION_FULL = "${PV}"

def get_llvm_arch(bb, d, arch_var):
    import re
    a = d.getVar(arch_var, True)
    if   re.match('(i.86|x86.64)$', a):                return 'AMDGPU;X86'
    elif re.match('athlon$', a):                       return 'X86'
    elif re.match('arm$', a):                          return 'ARM'
    elif re.match('armeb$', a):                        return 'ARM'
    elif re.match('aarch64$', a):                      return 'AArch64'
    elif re.match('aarch64_be$', a):                   return 'AArch64'
    elif re.match('mips(isa|)(32|64|)(r6|)(el|)$', a): return 'Mips'
    elif re.match('p(pc|owerpc)(|64)', a):             return 'PowerPC'
    else:
        raise bb.parse.SkipRecipe("Cannot map '%s' to a supported LLVM architecture" % a)

def get_llvm_target_arch(bb, d):
    return get_llvm_arch(bb, d, 'TARGET_ARCH')
#
# Default to build all OE-Core supported target arches (user overridable).
#
LLVM_TARGETS ?= "${@get_llvm_target_arch(bb, d)}"

EXTRA_OECMAKE += "-DUSE_SHARED_LLVM=on \
                -DCMAKE_INSTALL_PREFIX=${libdir}/llvm-${LLVM_VERSION} \
                -DCMAKE_VERBOSE_MAKEFILE=ON \
                -DCMAKE_BUILD_TYPE=RelWithDebInfo \
                -DCMAKE_CXX_FLAGS="${OECMAKE_CXX_FLAGS}" \
                -DLLVM_LINK_LLVM_DYLIB=ON \
                -DLLVM_INSTALL_UTILS=ON \
                -DLLVM_VERSION_SUFFIX= \
                -DLLVM_ENABLE_RTTI=ON \
                -DLLVM_ENABLE_FFI=ON \
                -DLLVM_BINUTILS_INCDIR=${includedir} \
                -DLIBCLANG_LIBRARY_VERSION=1 \
                -DCMAKE_CROSSCOMPILING:BOOL=ON \
                -DLLVM_TARGETS_TO_BUILD="${LLVM_TARGETS}" \
"
EXTRA_OECMAKE_append_class-target = "\
                -DLLVM_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm-tblgen-${LLVM_VERSION} \
                -DCLANG_TABLEGEN=${STAGING_BINDIR_NATIVE}/clang-tblgen-${LLVM_VERSION} \
"
EXTRA_OECMAKE_append_class-native = "\
                -DPYTHON_RELEASE_LIB=${STAGING_LIBDIR} \
                -DLLDB_DISABLE_LIBEDIT=1 \
                -DDL_LIBRARY_PATH=${libdir}/libdl.so \
"

export STAGING_INCDIR
export STAGING_LIBDIR
export DEB_HOST_MULTIARCH

do_configure_prepend(){
	# Base on debian/rules
	cd ${S}
	DEBIAN_REVISION=`dpkg-parsechangelog |  sed -rne "s,^Version: 1:([0-9.]+)(~|-)(.*),\3,p"`
	cd -
	mkdir -p ${S}/clang/include/clang/Debian
	sed -e "s|@DEB_PATCHSETVERSION@|$DEBIAN_REVISION|" \
		${S}/debian/debian_path.h > ${S}/clang/include/clang/Debian/debian_path.h
	# Remove some old symlinks
	cd ${S}/tools/
	if test -h clang; then
		rm clang
	fi
	ln -s ../clang .
	readlink clang

	if test -h lldb; then
		rm lldb
	fi
	ln -s ../lldb .

	cd ${S}/projects/
	if test -h compiler-rt; then
		rm compiler-rt
	fi
	ln -s ../compiler-rt .
	readlink compiler-rt
	# Due to bug upstream, no symlink here
	cp -R -H ${S}/clang-tools-extra ${S}/tools/clang/tools/extra

	# Remove RPATHs
	sed -i 's:$(RPATH) -Wl,$(\(ToolDir\|LibDir\|ExmplDir\))::g' ${S}/Makefile.rules

	# Fix paths in llvm-config
	sed -i "s|sys::path::parent_path(CurrentPath))\.str()|sys::path::parent_path(sys::path::parent_path(CurrentPath))).str()|g" \
	       ${S}/tools/llvm-config/llvm-config.cpp
	sed -ri  "s#\"/(bin|include|lib)(/?\")#\"/lib/llvm-${LLVM_VERSION}/\1\2#g" ${S}/tools/llvm-config/llvm-config.cpp
	cd ${B}
}

do_install_append(){
	# Create this fake directory to make the install libclang-common-dev happy
	# under the unsupported archs of compiler-rt
	install -d ${D}${libdir}/clang/${LLVM_VERSION} \
	           ${D}${libdir}/llvm-${LLVM_VERSION}/lib/clang/${LLVM_VERSION_FULL}/lib/ \
	           ${D}${libdir}/llvm-${LLVM_VERSION}/lib/clang/${LLVM_VERSION}/lib/clang_linux/
	mkdir -p ${B}/tools/clang/runtime/compiler-rt/clang_linux/

	# idem for the lldb python binding
	mkdir -p ${D}${libdir}/llvm-${LLVM_VERSION}/lib/${PYTHON_DIR}/site-packages/lldb/

	# Rename binaries
	install -d ${D}${bindir}
	cd ${D}${bindir}; rm -f *
	for f in ../lib/llvm-${LLVM_VERSION}/bin/*; do
		ln -s $f `basename $f`-${LLVM_VERSION}
	done
	cd -

	cp ${S}/compiler-rt/lib/asan/scripts/asan_symbolize.py ${D}${bindir}/asan_symbolize-${LLVM_VERSION}

	# Rename some stuff with the version name
	cp ${S}/clang/tools/scan-build/man/scan-build.1 ${S}/clang/tools/scan-build/man/scan-build-${LLVM_VERSION}.1
	for dir in ftdetect ftplugin syntax indent; do
		install -d ${D}${datadir}/vim/addons/$dir
		cp -f ${S}/utils/vim/$dir/llvm.vim ${D}${datadir}/vim/addons/$dir/llvm-${LLVM_VERSION}.vim
		if [ -f ${S}/utils/vim/$dir/tablegen.vim ]; then
			cp -f ${S}/utils/vim/$dir/tablegen.vim ${D}${datadir}/vim/addons/$dir/tablegen-llvm.vim
		fi
	done
	cp -f ${S}/utils/vim/vimrc ${D}${datadir}/vim/addons/llvm-${LLVM_VERSION}-vimrc

	cp -f ${S}/clang/tools/clang-format/clang-format-diff.py \
	          ${S}/clang/tools/clang-format/clang-format-diff-${LLVM_VERSION}
	cp -f ${S}/clang/tools/clang-format/clang-format.py \
	          ${S}/clang/tools/clang-format/clang-format-${LLVM_VERSION}.py
	rm -rf ${S}/clang/tools/scan-build-${LLVM_VERSION}
	cp -fR ${S}/clang/tools/scan-build ${S}/clang/tools/scan-build-${LLVM_VERSION}
	rm -rf ${S}/clang/tools/scan-view-${LLVM_VERSION}
	cp -fR ${S}/clang/tools/scan-view  ${S}/clang/tools/scan-view-${LLVM_VERSION}

	# Managed in lldb-X.Y.links.in
	rm -f ${B}/Release/lib/python*/site-packages/lldb/_lldb.so

	# According to debian/llvm-X.Y-dev.dirs.in
	install -d ${D}${libdir}/llvm-${LLVM_VERSION}/build \
	           ${D}${datadir}/emacs/site-lisp/llvm-${LLVM_VERSION}

	# According to debian/clang-X.Y.install.in
	install -d ${D}${datadir}/clang
	cp -r ${S}/tools/clang/tools/scan-build-${LLVM_VERSION} ${D}${datadir}/clang/
	cp -r ${S}/tools/clang/tools/scan-view-${LLVM_VERSION}  ${D}${datadir}/clang/

	# According to debian/clang-format-X.Y.install.in
	install -d ${D}${datadir}/vim/addons/syntax/ \
	           ${D}${datadir}/emacs/site-lisp/clang-format-${LLVM_VERSION}/ \
                   ${D}${datadir}/clang/clang-format-${LLVM_VERSION}/

	cp ${S}/clang/tools/clang-format/clang-format-${LLVM_VERSION}.py   ${D}${datadir}/vim/addons/syntax/
	#cp ${S}/clang/tools/clang-format/clang-format-diff-${LLVM_VERSION} ${D}${bindir}
	mv ${D}${libdir}/llvm-${LLVM_VERSION}/share/clang/clang-format.el \
	       ${D}${datadir}/emacs/site-lisp/clang-format-${LLVM_VERSION}/
	mv ${D}${libdir}/llvm-${LLVM_VERSION}/share/clang/clang-format-diff.py \
		${D}${datadir}/clang/clang-format-${LLVM_VERSION}/
	mv ${D}${libdir}/llvm-${LLVM_VERSION}/share/clang/clang-format-sublime.py \
		${D}${datadir}/clang/clang-format-${LLVM_VERSION}/
	mv ${D}${libdir}/llvm-${LLVM_VERSION}/share/clang/clang-format.py \
		${D}${datadir}/clang/clang-format-${LLVM_VERSION}/

	# According to debian/rules
	rm -rf ${D}${libdir}/llvm-${LLVM_VERSION}/share/clang/clang-format-bbedit.applescript
	rm -rf ${D}${libdir}/llvm-${LLVM_VERSION}/lib/python2.7/site-packages/six.py

	# According to debian/libclang-X.Y-dev.install.in
	cp -r ${B}/tools/clang/runtime/compiler-rt/clang_linux/ \
	          ${D}${libdir}/llvm-${LLVM_VERSION}/lib/clang/${LLVM_VERSION}/lib/

	install -d ${D}${libdir}/${DEB_HOST_MULTIARCH}

	# According to debian/libclang1-X.Y.install.in
	mv ${D}${libdir}/llvm-${LLVM_VERSION}/lib/libclang-${LLVM_VERSION}.so.1 ${D}${libdir}/${DEB_HOST_MULTIARCH}/

	# According to debian/liblldb-X.Y.install.in
	cp ${D}${libdir}/llvm-${LLVM_VERSION}/lib/liblldb-${LLVM_VERSION}.so.1  ${D}${libdir}/${DEB_HOST_MULTIARCH}/

	# According to debian/libllvmX.Y.install.in
	mv ${D}${libdir}/llvm-${LLVM_VERSION}/lib/libLLVM-${LLVM_VERSION}.so.1  ${D}${libdir}/${DEB_HOST_MULTIARCH}/

	# According to debian/llvm-X.Y-dev.install.in
	install -d ${D}${datadir}/llvm-${LLVM_VERSION}/cmake \
	           ${D}${includedir}/llvm-${LLVM_VERSION} \
	           ${D}${includedir}/llvm-c-${LLVM_VERSION}
	mv ${D}${libdir}/llvm-${LLVM_VERSION}/include/llvm/            ${D}${includedir}/llvm-${LLVM_VERSION}/
	mv ${D}${libdir}/llvm-${LLVM_VERSION}/include/llvm-c/          ${D}${includedir}/llvm-c-${LLVM_VERSION}/
	mv ${D}${libdir}/llvm-${LLVM_VERSION}/share/llvm/cmake/*.cmake ${D}${datadir}/llvm-${LLVM_VERSION}/cmake/
	rm -rf ${D}${libdir}/llvm-${LLVM_VERSION}/share/llvm

	cp ${S}/utils/emacs/emacs.el         ${D}${datadir}/emacs/site-lisp/llvm-${LLVM_VERSION}/
	cp ${S}/utils/emacs/llvm-mode.el     ${D}${datadir}/emacs/site-lisp/llvm-${LLVM_VERSION}/
	cp ${S}/utils/emacs/tablegen-mode.el ${D}${datadir}/emacs/site-lisp/llvm-${LLVM_VERSION}/

	# Remove some license files
	rm -f ${D}${libdir}/llvm-${LLVM_VERSION}/include/llvm/Support/LICENSE.TXT \
	      ${D}${libdir}/llvm-${LLVM_VERSION}/build/autoconf/LICENSE.TXT

	# According to debian/llvm-X.Y-runtime.install.in
	sed -e "s|@LLVM_VERSION@|${LLVM_VERSION}|g" ${S}/debian/llvm-X.Y-runtime.binfmt.in \
	       > ${S}/debian/llvm-${LLVM_VERSION}-runtime.binfmt
	install -d ${D}${datadir}/binfmts/
	cp ${S}/debian/llvm-${LLVM_VERSION}-runtime.binfmt ${D}${datadir}/binfmts/

	# According to debian/llvm-X.Y-tools.install.in
	install -d ${D}${libdir}/llvm-${LLVM_VERSION}/build/unittests \
	           ${D}${libdir}/llvm-${LLVM_VERSION}/build/utils/lit
	cp ${S}/unittests/Makefile.unittest ${D}${libdir}/llvm-${LLVM_VERSION}/build/unittests/
	cp -r ${S}/utils/lit/*              ${D}${libdir}/llvm-${LLVM_VERSION}/build/utils/lit/

	# According to debian/python-clang-X.Y.install.in
	install -d ${D}${PYTHON_SITEPACKAGES_DIR}
	cp -r ${S}/tools/clang/bindings/python/clang/ ${D}${PYTHON_SITEPACKAGES_DIR}/

	# According to debian/clang-X.Y.links.in
	ln -sf ${datadir}/clang/scan-build-${LLVM_VERSION}/bin/scan-build  ${D}${bindir}/scan-build-${LLVM_VERSION}
	ln -sf ${datadir}/clang/scan-view-${LLVM_VERSION}/bin/scan-view    ${D}${bindir}/scan-view-${LLVM_VERSION}

	# According to debian/libclang-X.Y-dev.links.in
	ln -sf libclang-${LLVM_VERSION}.so.1 ${D}${libdir}/${DEB_HOST_MULTIARCH}/libclang-${LLVM_VERSION}.so
	ln -sf ../../${DEB_HOST_MULTIARCH}/libclang-${LLVM_VERSION}.so.1 \
	        ${D}${libdir}/llvm-${LLVM_VERSION}/lib/libclang.so

	# According to debian/libclang-common-X.Y-dev.links.in
	install -d ${D}${includedir}/clang/${LLVM_VERSION} \
	           ${D}${includedir}/clang/${LLVM_VERSION_FULL} \
	           ${D}${libdir}/clang/${LLVM_VERSION_FULL}
	ln -sf ../../../lib/llvm-${LLVM_VERSION}/lib/clang/${LLVM_VERSION_FULL}/include \
	        ${D}${includedir}/clang/${LLVM_VERSION}/include
	ln -sf ../../llvm-${LLVM_VERSION}/lib/clang/${LLVM_VERSION_FULL}/include \
	        ${D}${libdir}/clang/${LLVM_VERSION}/include
	ln -sf ../../llvm-${LLVM_VERSION}/lib/clang/${LLVM_VERSION_FULL}/lib \
	        ${D}${libdir}/clang/${LLVM_VERSION}/lib
	ln -sf ../../../lib/llvm-${LLVM_VERSION}/lib/clang/${LLVM_VERSION_FULL}/include \
	        ${D}${includedir}/clang/${LLVM_VERSION_FULL}/include
	ln -sf ../../llvm-${LLVM_VERSION}/lib/clang/${LLVM_VERSION_FULL}/include \
	        ${D}${libdir}/clang/${LLVM_VERSION_FULL}/include
	ln -sf ../../llvm-${LLVM_VERSION}/lib/clang/${LLVM_VERSION_FULL}/lib \
	        ${D}${libdir}/clang/${LLVM_VERSION_FULL}/lib

	# According to debian/libclang1-X.Y.links.in
	# as upstream
	ln -sf ../../${DEB_HOST_MULTIARCH}/libclang-${LLVM_VERSION}.so.1 \
	        ${D}${libdir}/llvm-${LLVM_VERSION}/lib/libclang-${LLVM_VERSION}.so.1
	# Compatibility for the ABI breakage (See #762959)
	ln -sf libclang-${LLVM_VERSION}.so.1 ${D}${libdir}/${DEB_HOST_MULTIARCH}/libclang.so.1

	# According to debian/liblldb-X.Y.links.in
	ln -sf liblldb-${LLVM_VERSION}.so.1 ${D}${libdir}/${DEB_HOST_MULTIARCH}/liblldb-${LLVM_VERSION}.so
	ln -sf ../../${DEB_HOST_MULTIARCH}/liblldb-${LLVM_VERSION}.so.1 \
	        ${D}${libdir}/llvm-${LLVM_VERSION}/lib/liblldb.so.1
	ln -sf ../../${DEB_HOST_MULTIARCH}/liblldb-${LLVM_VERSION}.so.1 \
	        ${D}${libdir}/llvm-${LLVM_VERSION}/lib/liblldb-${LLVM_VERSION}.so.1


	# According to debian/llvm-X.Y-dev.links.in
	install -d ${D}${libdir}/llvm-${LLVM_VERSION}/build
	ln -sf ../../${DEB_HOST_MULTIARCH}/libLLVM-${LLVM_VERSION}.so.1 \
	        ${D}${libdir}/llvm-${LLVM_VERSION}/lib/libLLVM-${LLVM_VERSION}.so
	ln -sf ../../${DEB_HOST_MULTIARCH}/libLLVM-${LLVM_VERSION}.so.1 \
	        ${D}${libdir}/llvm-${LLVM_VERSION}/lib/libLLVM-${LLVM_VERSION_FULL}.so.1
	ln -sf ../../${DEB_HOST_MULTIARCH}/libLLVM-${LLVM_VERSION}.so.1 \
	        ${D}${libdir}/llvm-${LLVM_VERSION}/lib/libLLVM-${LLVM_VERSION_FULL}.so
	ln -sf libLLVM-${LLVM_VERSION}.so.1 \
	        ${D}${libdir}/${DEB_HOST_MULTIARCH}/libLLVM-${LLVM_VERSION_FULL}.so.1
	ln -sf ../../../include/llvm-c-${LLVM_VERSION}/llvm-c ${D}${libdir}/llvm-${LLVM_VERSION}/include/llvm-c
	ln -sf ../../../include/llvm-${LLVM_VERSION}/llvm     ${D}${libdir}/llvm-${LLVM_VERSION}/include/llvm
	ln -sf ../include/       ${D}${libdir}/llvm-${LLVM_VERSION}/build/include
	ln -sf ../../llvm-${LLVM_VERSION}/ ${D}${libdir}/llvm-${LLVM_VERSION}/build/Release
	ln -sf ../../llvm-${LLVM_VERSION}/ ${D}${libdir}/llvm-${LLVM_VERSION}/build/Debug+Asserts

	# According to debian/python-lldb-X.Y.links.in
	ln -sf ../../llvm-${LLVM_VERSION}/lib/${PYTHON_DIR}/site-packages/lldb/ \
	        ${D}${PYTHON_SITEPACKAGES_DIR}/lldb
	ln -sf ../../../../../${DEB_HOST_MULTIARCH}/libLLVM-${LLVM_VERSION_FULL}.so.1 \
	        ${D}${PYTHON_SITEPACKAGES_DIR}/lldb/libLLVM-${LLVM_VERSION_FULL}.so.1
	ln -sf ../../../../../${DEB_HOST_MULTIARCH}/libLLVM-${LLVM_VERSION_FULL}.so.1 \
	        ${D}${PYTHON_SITEPACKAGES_DIR}/lldb/libLLVM-${LLVM_VERSION}.so.1

	# Correct files permission
	chmod 0644 ${D}${libdir}/llvm-${LLVM_VERSION}/lib/*.a
}

do_install_append_class-target(){
	# Base on debian/rules
	chrpath -d `find ${D}${libdir}/llvm-${LLVM_VERSION}/bin/ -type f -executable -exec file -i '{}' \; | \
		grep 'x-executable; charset=binary'|cut -d: -f1`
}

do_install_append_class-native() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/NATIVE/bin/clang-tblgen ${D}${bindir}/clang-tblgen-${LLVM_VERSION}
	install -m 0755 ${B}/NATIVE/bin/llvm-config ${D}${bindir}/llvm-config-${LLVM_VERSION}
	install -m 0755 ${B}/NATIVE/bin/llvm-tblgen ${D}${bindir}/llvm-tblgen-${LLVM_VERSION}
}

# we name and ship packages as Debian,
# so we need pass QA errors with dev-so and dev-deps
# - python-lldb-3.8 need to include "_lldb.so"
# - liblldb-3.8 need to include "liblldb-3.8.so"
# - clang-3.8 needs to depend on libclang-common-3.8-dev
# - lldb-3.8 needs to depend on llvm-3.8-dev
# - llvm-3.8-tools  needs to depend on llvm-3.8-dev
INSANE_SKIP_${MLPREFIX}python-lldb-${LLVM_VERSION} += "dev-so"
INSANE_SKIP_${MLPREFIX}liblldb-${LLVM_VERSION} += "dev-so"
INSANE_SKIP_${MLPREFIX}lldb-${LLVM_VERSION} += "dev-deps"
INSANE_SKIP_${MLPREFIX}llvm-${LLVM_VERSION}-tools += "dev-deps"
INSANE_SKIP_${MLPREFIX}clang-${LLVM_VERSION} += "dev-deps"

PACKAGES =+ "\
    libclang-${LLVM_VERSION}-staticdev llvm-${LLVM_VERSION}-staticdev liblldb-${LLVM_VERSION}-staticdev \
    clang-tidy-${LLVM_VERSION} clang-format-${LLVM_VERSION} libclang-${LLVM_VERSION} libclang-${LLVM_VERSION}-dev \
    libclang-common-${LLVM_VERSION}-dev python-clang-${LLVM_VERSION} clang-${LLVM_VERSION} \
    libllvm${LLVM_VERSION} llvm-${LLVM_VERSION} llvm-${LLVM_VERSION}-runtime llvm-${LLVM_VERSION}-tools \
    llvm-${LLVM_VERSION}-dev lldb-${LLVM_VERSION} liblldb-${LLVM_VERSION} python-lldb-${LLVM_VERSION} \
    liblldb-${LLVM_VERSION}-dev"

FILES_libclang-${LLVM_VERSION}-staticdev = " \
    ${libdir}/llvm-${LLVM_VERSION}/lib/libclang*.a \
    ${libdir}/llvm-${LLVM_VERSION}/lib/libmodernizeCore.a \
    ${libdir}/llvm-${LLVM_VERSION}/lib/clang/${LLVM_VERSION_FULL}/lib/linux/*.a \
"
FILES_llvm-${LLVM_VERSION}-staticdev = " \
    ${libdir}/llvm-${LLVM_VERSION}/lib/libLLVM*.a \
    ${libdir}/llvm-${LLVM_VERSION}/lib/libllvm*.a \
    ${libdir}/llvm-${LLVM_VERSION}/lib/libLTO*.a \
"
FILES_liblldb-${LLVM_VERSION}-staticdev = "${libdir}/llvm-${LLVM_VERSION}/lib/liblldb*.a"

FILES_clang-format-${LLVM_VERSION} = " \
    ${bindir}/clang-format-* \
    ${bindir}/git-clang-format-${LLVM_VERSION} \
    ${libdir}/llvm-${LLVM_VERSION}/bin/git-clang-format \
    ${libdir}/llvm-${LLVM_VERSION}/bin/clang-format \
    ${datadir}/*/*/*/clang-format* \
"
FILES_libclang-${LLVM_VERSION} = " \
    ${libdir}/${DEB_HOST_MULTIARCH}/libclang*${SOLIBS} \
    ${libdir}/llvm-${LLVM_VERSION}/lib/libclang*${SOLIBS} \
"
FILES_libclang-${LLVM_VERSION}-dev = " \
    ${libdir}/${DEB_HOST_MULTIARCH}/libclang-${LLVM_VERSION}${SOLIBSDEV} \
    ${libdir}/llvm-${LLVM_VERSION}/include/clang* \
    ${libdir}/llvm-${LLVM_VERSION}/lib/libclang*${SOLIBSDEV} \
"
FILES_libclang-common-${LLVM_VERSION}-dev = " \
    ${bindir}/clang-tblgen-${LLVM_VERSION} \
    ${bindir}/yaml-bench-${LLVM_VERSION} \
    ${includedir}/clang \
    ${libdir}/clang \
    ${libdir}/llvm-${LLVM_VERSION}/bin/yaml-bench \
    ${libdir}/llvm-${LLVM_VERSION}/bin/clang-tblgen \
    ${libdir}/llvm-${LLVM_VERSION}/lib/clang \
"
FILES_python-clang-${LLVM_VERSION} = " \
    ${PYTHON_SITEPACKAGES_DIR}/clang \
"
FILES_clang-${LLVM_VERSION} = " \
    ${bindir}/asan_symbolize-${LLVM_VERSION} \
    ${bindir}/c-index-test-${LLVM_VERSION} \
    ${bindir}/clang* \
    ${bindir}/modularize-${LLVM_VERSION} \
    ${bindir}/sancov-${LLVM_VERSION} \
    ${bindir}/scan-*${LLVM_VERSION} \
    ${libdir}/llvm-${LLVM_VERSION}/bin/clang* \
    ${libdir}/llvm-${LLVM_VERSION}/bin/sancov \
    ${libdir}/llvm-${LLVM_VERSION}/bin/c-index-test \
    ${libdir}/llvm-${LLVM_VERSION}/share/scan-build/* \
    ${libdir}/llvm-${LLVM_VERSION}/share/scan-view/* \
    ${libdir}/llvm-${LLVM_VERSION}/share/clang/cmake/* \
    ${libdir}/llvm-${LLVM_VERSION}/libexec/* \
    ${libdir}/llvm-${LLVM_VERSION}/bin/scan-build \
    ${libdir}/llvm-${LLVM_VERSION}/bin/modularize \
    ${libdir}/llvm-${LLVM_VERSION}/bin/scan-view \
    ${datadir}/clang \
"
FILES_libllvm${LLVM_VERSION} = "${libdir}/${DEB_HOST_MULTIARCH}/libLLVM*${SOLIBS}"
FILES_llvm-${LLVM_VERSION} = " \
    ${bindir}/bugpoint-${LLVM_VERSION} \
    ${bindir}/llc-${LLVM_VERSION} \
    ${bindir}/llvm-*-${LLVM_VERSION} \
    ${bindir}/macho-dump-${LLVM_VERSION} \
    ${bindir}/obj2yaml-${LLVM_VERSION} \
    ${bindir}/opt-${LLVM_VERSION} \
    ${bindir}/verify-uselistorder-${LLVM_VERSION} \
    ${libdir}/llvm-${LLVM_VERSION}/bin/bugpoint \
    ${libdir}/llvm-${LLVM_VERSION}/bin/llc \
    ${libdir}/llvm-${LLVM_VERSION}/bin/llvm-* \
    ${libdir}/llvm-${LLVM_VERSION}/bin/macho-dump \
    ${libdir}/llvm-${LLVM_VERSION}/bin/opt \
    ${libdir}/llvm-${LLVM_VERSION}/bin/verify-uselistorder \
    ${libdir}/llvm-${LLVM_VERSION}/bin/yaml2obj \
    ${libdir}/llvm-${LLVM_VERSION}/bin/obj2yaml \
"
FILES_llvm-${LLVM_VERSION}-runtime = " \
    ${bindir}/lli*${LLVM_VERSION} \
    ${libdir}/llvm-${LLVM_VERSION}/bin/lli* \
    ${datadir}/binfmts \
"
FILES_llvm-${LLVM_VERSION}-tools = " \
    ${bindir}/FileCheck-${LLVM_VERSION} \
    ${bindir}/count-${LLVM_VERSION} \
    ${bindir}/not-${LLVM_VERSION} \
    ${libdir}/llvm-${LLVM_VERSION}/bin/FileCheck \
    ${libdir}/llvm-${LLVM_VERSION}/bin/count \
    ${libdir}/llvm-${LLVM_VERSION}/bin/not \
    ${libdir}/llvm-${LLVM_VERSION}/build/utils \
"
FILES_llvm-${LLVM_VERSION}-dev = " \
    ${includedir}/llvm-* \
    ${libdir}/llvm-${LLVM_VERSION}/build \
    ${libdir}/llvm-${LLVM_VERSION}/include/llvm* \
    ${libdir}/llvm-${LLVM_VERSION}/lib/LLVM*${SOLIBSDEV} \
    ${libdir}/llvm-${LLVM_VERSION}/lib/libLTO${SOLIBSDEV} \
    ${libdir}/llvm-${LLVM_VERSION}/lib/BugpointPasses${SOLIBSDEV} \
    ${libdir}/llvm-${LLVM_VERSION}/lib/libLLVM*.so* \
    ${libdir}/${DEB_HOST_MULTIARCH}/libLLVM*.so* \
    ${datadir}/emacs/site-lisp/llvm-${LLVM_VERSION} \
    ${datadir}/llvm-${LLVM_VERSION}/cmake \
    ${datadir}/vim/addons/* \
"
FILES_lldb-${LLVM_VERSION} = " \
    ${bindir}/lldb-* \
    ${libdir}/llvm-${LLVM_VERSION}/bin/lldb* \
"
FILES_liblldb-${LLVM_VERSION} = " \
    ${libdir}/${DEB_HOST_MULTIARCH}/liblldb-${LLVM_VERSION}.so* \
    ${libdir}/llvm-${LLVM_VERSION}/lib/liblldb${SOLIBS} \
    ${libdir}/llvm-${LLVM_VERSION}/lib/liblldb-${LLVM_VERSION}${SOLIBS} \
    ${libdir}/llvm-${LLVM_VERSION}/lib/${PYTHON_DIR}/*-packages/readline.so \
"
FILES_python-lldb-${LLVM_VERSION} = " \
    ${libdir}/llvm-${LLVM_VERSION}/lib/${PYTHON_DIR}/*-packages/lldb \
    ${PYTHON_SITEPACKAGES_DIR}/lldb \
"
FILES_clang-tidy-${LLVM_VERSION} = "\
    ${bindir}/clang-tidy* \
    ${bindir}/run-clang-tidy* \
    ${libdir}/llvm-${LLVM_VERSION}/bin/clang-tidy \
    ${libdir}/llvm-${LLVM_VERSION}/share/clang/clang-tidy-diff.py \
    ${libdir}/llvm-${LLVM_VERSION}/share/clang/run-clang-tidy.py \
"
FILES_liblldb-${LLVM_VERSION}-dev = " \
    ${libdir}/llvm-${LLVM_VERSION}/include/lldb \
    ${libdir}/llvm-${LLVM_VERSION}/lib/liblldb*.so \
"

FILES_${PN}-dbg += " \
    ${libdir}/llvm-${LLVM_VERSION}/*/.debug \
"
FILES_${PN}-doc += " \
    ${libdir}/llvm-${LLVM_VERSION}/share/man \
    ${libdir}/llvm-${LLVM_VERSION}/docs \
"

RDEPENDS_clang-${LLVM_VERSION} += "libclang-common-${LLVM_VERSION}-dev libclang-${LLVM_VERSION} binutils"
RDEPENDS_clang-format-${LLVM_VERSION} += "python"
RDEPENDS_libclang-${LLVM_VERSION}-dev += "libclang-common-${LLVM_VERSION}-dev"
RDEPENDS_libclang-common-${LLVM_VERSION}-dev += "libllvm${LLVM_VERSION}"
RDEPENDS_python-clang-${LLVM_VERSION} += "python"
RDEPENDS_llvm-${LLVM_VERSION} += "llvm-${LLVM_VERSION}-runtime"
RDEPENDS_llvm-${LLVM_VERSION}-runtime += "binfmt-support"
RDEPENDS_llvm-${LLVM_VERSION}-tools += "python llvm-${LLVM_VERSION}-dev"
RDEPENDS_lldb-${LLVM_VERSION} += "libllvm${LLVM_VERSION} python llvm-${LLVM_VERSION}-dev python-lldb-${LLVM_VERSION}"
RDEPENDS_liblldb-${LLVM_VERSION} += "llvm-${LLVM_VERSION}"
RDEPENDS_python-lldb-${LLVM_VERSION} += "python"
RDEPENDS_liblldb-${LLVM_VERSION} += "lldb-${LLVM_VERSION}"

DEBIANNAME_libclang-${LLVM_VERSION} = "libclang1-${LLVM_VERSION}"
DEBIAN_NOAUTONAME_libllvm${LLVM_VERSION} = "1"
DEBIAN_NOAUTONAME_liblldb-${LLVM_VERSION} = "1"

BBCLASSEXTEND = "native"
