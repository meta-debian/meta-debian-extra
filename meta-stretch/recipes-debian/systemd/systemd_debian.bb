#
# base-recipe: meta-debian/recipes-debian/systemd/systemd_debian.bb 
# base-branch: jethro
#
SUMMARY = "A System and service manager"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/systemd"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
file://LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe \
file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c \
"
PROVIDES = "udev"

inherit debian-package

SRC_URI += "file://disable-manpages.patch \
"

PV = "232"

inherit pkgconfig autotools useradd python3native

DEPENDS = "intltool-native \
           gperf-native \
           libcap \
           dbus \
           glib-2.0 \
           acl \
           xz-utils \
           libgcrypt \
           kmod \
           util-linux \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)} \
           python3-lxml-native \
          "
do_debian_patch_prepend() {
        sed -i 's/.gitignore/.gitignore-rename/g' ${DEBIAN_QUILT_PATCHES}/test-resolved-packet-add-a-simple-test-for-our-allocation.patch
}

# These options are almost same as CONFFLAGS in debian/rules.
# Other target specific CONFFLAGS in debian/rules are ignored.
DEBIAN_CONFOPTS = "\
         --with-rootprefix=${base_prefix} \
                   --with-rootlibdir=${base_libdir} \
                   --with-ntp-servers="" \
                   --with-dns-servers="" \
                   --with-system-uid-max=999 \
                   --with-system-gid-max=999 \
                   --enable-coredump \
                   --with-kbd-loadkeys=${base_bindir}/loadkeys \
                   --with-kbd-setfont=${base_bindir}/setfont \
                   --without-kill-user-processes \
                   --enable-split-usr \
                   --disable-qrencode \
                   --disable-vconsole \
                   --disable-firstboot \
                   --disable-xkbcommon \
                   --disable-libiptc \
                   PYTHON="${PYTHON}" \
                  "

# --enable-dependency-tracking:
#   avoid compile error "Cannot open src/*/org.freedesktop.*.policy"
# --disable-selinux: Disable selinux support
EXTRA_OECONF = "${DEBIAN_CONFOPTS} \
                ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--enable-pam', '--disable-pam', d)} \
                --disable-manpages \
                --disable-selinux \
                --enable-dependency-tracking \
                --disable-libcryptsetup \
                --enable-efi \
                --enable-hwdb \
                --enable-sysusers \
               "
CACHED_CONFIGUREVARS += "ac_cv_path_MOUNT_PATH=${base_bindir}/mount \
                         ac_cv_path_UMOUNT_PATH=${base_bindir}/umount \
                         ac_cv_path_KEXEC=${sbindir}/kexec \
                         ac_cv_path_KILL=${base_bindir}/kill \
                         ac_cv_path_KMOD=${base_bindir}/kmod \
                         ac_cv_path_QUOTACHECK=${sbindir}/quotacheck \
                         ac_cv_path_QUOTAON=${sbindir}/quotaon \
                         ac_cv_path_SULOGIN=${base_sbindir}/sulogin \
                         "
do_configure_prepend() {
	export NM="${HOST_PREFIX}gcc-nm"
	export AR="${HOST_PREFIX}gcc-ar"
	export RANLIB="${HOST_PREFIX}gcc-ranlib"
}

# append debian extra files and remove unneeded files
do_install_append() {
	# systemd package: setup base_bindir
	ln -s ${base_libdir}/systemd/systemd ${D}${base_bindir}
	# systemd package: setup sysconfdir
	ln -s ../modules ${D}${sysconfdir}/modules-load.d/modules.conf
	ln -s ../sysctl.conf ${D}${sysconfdir}/sysctl.d/99-sysctl.conf
	for dir in binfmt.d kernel tmpfiles.d; do
		rm -r ${D}${sysconfdir}/${dir}
	done
	rm ${D}${sysconfdir}/init.d/README

	for name in system-shutdown system-sleep systemd-update-done; do
		rm -r ${D}${base_libdir}/systemd/${name}
	done
	# systemd package: setup bindir
	rm ${D}${bindir}/kernel-install
	# systemd package: setup libdir
	for dir in binfmt.d kernel modules-load.d rpm \
	           systemd/network systemd/user-generators \
	           tmpfiles.d/etc.conf; do
		rm -r ${D}${libdir}/${dir}
	done

	mv ${D}${datadir}/zsh/site-functions \
		${D}${datadir}/zsh/vendor-completions
	rm ${D}${datadir}/zsh/vendor-completions/_kernel-install
	# systemd package: remove localstatedir
	rm -r ${D}${localstatedir}
	for service in x11-common hostname rmnologin bootmisc fuse bootlogd \
	               stop-bootlogd-single stop-bootlogd hwclock mountkernfs \
	               mountdevsubfs mountall mountall-bootclean mountnfs mountnfs-bootclean \
	               umountfs umountnfs umountroot checkfs checkroot checkroot-bootclean \
	               cryptdisks cryptdisks-early single killprocs sendsigs halt reboot \
	               rc rcS motd bootlogs; do
		ln -s /dev/null ${D}${systemd_system_unitdir}/${service}.service
	done

	install -d ${D}${systemd_system_unitdir}/getty.target.wants \
	           ${D}${libdir}/systemd/user-generators
	ln -s ../getty-static.service ${D}${systemd_system_unitdir}/getty.target.wants
	ln -s systemd-modules-load.service ${D}${systemd_system_unitdir}/kmod.service
	ln -s systemd-modules-load.service \
		${D}${systemd_system_unitdir}/module-init-tools.service
	install -d ${D}${systemd_system_unitdir}/networking.service.d
	ln -s systemd-sysctl.service ${D}${systemd_system_unitdir}/procps.service
	ln -s rc-local.service ${D}${systemd_system_unitdir}/rc.local.service
	ln -s systemd-random-seed.service ${D}${systemd_system_unitdir}/urandom.service
	ln -s ${D}${systemd_system_unitdir}/user-generators/systemd-dbus1-generator \
	      ${D}${libdir}/systemd/user-generators/systemd-dbus1-generator
	
	# udev package
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/udev.init ${D}${sysconfdir}/init.d/udev
	install -d ${D}${base_libdir}/modprobe.d
	install -m 0644 ${S}/debian/extra/fbdev-blacklist.conf \
		${D}${base_libdir}/modprobe.d/fbdev-blacklist.conf
	install -d ${D}${systemd_system_unitdir}/sysinit.target.wants
	ln -s systemd-udevd.service \
		${D}${systemd_system_unitdir}/udev.service
	install -d ${D}${base_sbindir}
	ln -s ${base_bindir}/udevadm ${D}${base_sbindir}
	ln -s ${base_libdir}/systemd/systemd-udevd ${D}${base_sbindir}/udevd

	# ship test-udev, so that we have it for autopkgtests
	if [ -e ${B}/.libs/test-udev ]; then
		install -D -m 0755 ${B}/.libs/test-udev ${D}${libdir}/udev/test-udev
	else
		install -D -m 0755 ${B}/test-udev ${D}${libdir}/udev/test-udev
	fi
	
	# follow debian/udev.install
	cp -r ${S}/debian/extra/initramfs-tools ${D}${datadir}
	cp -r ${S}/debian/extra/rules/*.rules ${D}${base_libdir}/udev/rules.d/

	install -D -m 0644 ${S}/debian/udev.upstart \
	                   ${D}${sysconfdir}/init/udev.conf
	install -m 0644 ${S}/debian/udev.udevmonitor.upstart \
	                ${D}${sysconfdir}/init/udevmonitor.conf
	install -m 0644 ${S}/debian/udev.udevtrigger.upstart \
	                ${D}${sysconfdir}/init/udevtrigger.conf

	# follow debian/udev-udev.dirs
	install -d ${D}${sysconfdir}/udev/rules.d

	#follow debian/systemd.install
	install -d ${D}${base_libdir}/lsb \
	           ${D}${sysconfdir}/dhcp
	cp -r ${S}/debian/extra/init-functions.d ${D}${base_libdir}/lsb/
	cp -r ${S}/debian/extra/tmpfiles.d/*.conf ${D}${libdir}/tmpfiles.d/
	cp -r ${S}/debian/extra/systemd-sysv-install ${D}${base_libdir}/systemd/
	cp -r ${S}/debian/extra/units/* ${D}${systemd_system_unitdir}/
	cp -r ${S}/debian/extra/dhclient-exit-hooks.d \
	      ${D}${sysconfdir}/dhcp/

	# systemd-sysv
	ln -s ../${base_libdir}/systemd/systemd  ${D}${base_sbindir}/init
	ln -s ../${base_bindir}/systemctl ${D}${base_sbindir}/halt
	ln -s ../${base_bindir}/systemctl ${D}${base_sbindir}/poweroff
	ln -s ../${base_bindir}/systemctl ${D}${base_sbindir}/reboot
	ln -s ../${base_bindir}/systemctl ${D}${base_sbindir}/runlevel
	ln -s ../${base_bindir}/systemctl ${D}${base_sbindir}/shutdown
	ln -s ../${base_bindir}/systemctl ${D}${base_sbindir}/telinit
	
	# Make sure the runlevel services are known by systemd so their targets
	# get launched. See https://bugzilla.redhat.com/show_bug.cgi?id=1002806
	for t in poweroff rescue multi-user graphical reboot; do
		mkdir -p ${D}${systemd_system_unitdir}/${t}.target.wants
		ln -sf ../systemd-update-utmp-runlevel.service \
			${D}${systemd_system_unitdir}/${t}.target.wants
	done

	# we don't want /tmp to be a tmpfs by default
	mv ${D}${systemd_system_unitdir}/tmp.mount ${D}${datadir}/systemd/
	printf '\n[Install]\nWantedBy=local-fs.target\n' >> ${D}${datadir}/systemd/tmp.mount
	rm ${D}${systemd_system_unitdir}/local-fs.target.wants/tmp.mount

	# base on debian/libpam-systemd.install
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}" = "pam" ]; then
		cp -r ${S}/debian/extra/pam-configs ${D}${datadir}	
	fi

	# remove unwanted files
	rm -rf ${D}${base_sbindir}/udevd \
	       ${D}${sysconfdir}/X11 \
	       ${D}${libdir}/sysctl.d/50-default.conf \
	       ${D}${base_libdir}/systemd/libsystemd-shared.so \
	       ${D}${datadir}/factory/ \
	       ${D}${datadir}/bash-completion/completions/kernel-install \
	       ${D}${systemd_system_unitdir}/halt-local.service
	find ${D}/ -name '*.busname' -delete
	# remove files related to factory-reset feature
	find ${D}/ \( -name 'systemd-update-done*' -o \
		-name systemd-journal-catalog-update.service -o \
		-name systemd-udev-hwdb-update.service -o \
		-name ldconfig.service -o \
		-name etc.conf \) -delete
        # remove symlinks enabling default-on services
        rm -rf ${D}${sysconfdir}/systemd/system/*.target.wants/
        # FIXME: generate proper sysusers.d/basic.conf for Debian, and add autopkgtest
        rm -rf ${D}${libdir}/sysusers.d/*
        rm -f ${D}${systemd_system_unitdir}/*sysusers*.service \
	      ${D}${systemd_system_unitdir}/*/*sysusers*.service
}

PACKAGES =+ "libnss-myhostname \
             libnss-mymachines \
             libnss-resolve \
             libsystemd-dev \
             libsystemd \
             ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam-systemd', '', d)} \
             udev \
             libudev-dev \
             libudev \
             systemd-coredump \
             systemd-journal-remote \
             systemd-container \
             systemd-sysv \
            "
FILES_libnss-myhostname = "${base_libdir}/libnss_myhostname${SOLIBS}"
FILES_libnss-mymachines = "${base_libdir}/libnss_mymachines${SOLIBS}"
FILES_libnss-resolve = "${base_libdir}/libnss_resolve${SOLIBS}"
FILES_systemd-container = "${base_bindir}/machinectl \
                           ${sysconfdir}/dbus-1/system.d/org.freedesktop.import1.conf \
                           ${sysconfdir}/dbus-1/system.d/org.freedesktop.machine1.conf \
                           ${systemd_system_unitdir}/dbus-org.freedesktop.import1.service \
                           ${systemd_system_unitdir}/dbus-org.freedesktop.machine1.service \
                           ${systemd_system_unitdir}/local-fs.target.wants/var-lib-machines.mount \
                           ${systemd_system_unitdir}/machines.target \
                           ${systemd_system_unitdir}/systemd-importd.service \
                           ${systemd_system_unitdir}/systemd-machined.service \
                           ${systemd_system_unitdir}/systemd-nspawn@.service \
                           ${systemd_system_unitdir}/var-lib-machines.mount \
                           ${base_libdir}/systemd/import-pubring.gpg \
                           ${base_libdir}/systemd/systemd-export \
                           ${base_libdir}/systemd/systemd-import \
                           ${base_libdir}/systemd/systemd-importd \
                           ${base_libdir}/systemd/systemd-machined \
                           ${base_libdir}/systemd/systemd-pull \
                           ${bindir}/systemd-nspawn \
                           ${libdir}/tmpfiles.d/systemd-nspawn.conf \
                           ${datadir}/bash-completion/completions/machinectl \
                           ${datadir}/bash-completion/completions/systemd-nspawn \
                           ${datadir}/dbus-1/system-services/org.freedesktop.import1.service \
                           ${datadir}/dbus-1/system-services/org.freedesktop.machine1.service \
                           ${datadir}/polkit-1/actions/org.freedesktop.import1.policy \
                           ${datadir}/polkit-1/actions/org.freedesktop.machine1.policy \
                           ${datadir}/zsh/vendor-completions/_machinectl \
                           ${datadir}/zsh/vendor-completions/_sd_machines \
                           ${datadir}/zsh/vendor-completions/_systemd-nspawn \
                          "
FILES_systemd-coredump = "${sysconfdir}/systemd/coredump.conf \
                          ${systemd_system_unitdir}/sockets.target.wants/systemd-coredump.socket \
                          ${systemd_system_unitdir}/systemd-coredump* \
                          ${systemd_system_unitdir}/systemd/systemd-coredump \
                          ${bindir}/coredumpctl \
                          ${libdir}/sysctl.d/50-coredump.conf \
                          ${datadir}/bash-completion/completions/coredumpctl \
                          ${datadir}/zsh/vendor-completions/_coredumpctl \
                         "
FILES_systemd-journal-remote = "${sysconfdir}/systemd/journal-remote.conf \
                                ${sysconfdir}/systemd/journal-upload.conf \
                                ${systemd_system_unitdir}/systemd-journal-gatewayd.* \
                                ${systemd_system_unitdir}/systemd-journal-remote.* \
                                ${systemd_system_unitdir}/systemd-journal-upload.service \
                                ${base_libdir}/systemd/systemd-journal-* \
                                ${libdir}/tmpfiles.d/systemd-remote.conf \
                               "
FILES_${PN} = "${base_bindir} \
               ${bindir} \
               ${base_libdir} \
               ${libdir} \
               ${datadir} \
               ${sysconfdir}/dbus-1 \
               ${sysconfdir}/modules-load.d/modules.conf \
               ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${sysconfdir}/pam.d/systemd-user', '', d)} \
               ${sysconfdir}/sysctl.d/99-sysctl.conf \
               ${sysconfdir}/systemd \
               ${sysconfdir}/xdg \
               ${sysconfdir}/dhcp \
              "
FILES_${PN}-dbg += "${base_libdir}/systemd/.debug \
                    ${base_libdir}/systemd/system-generators/.debug \
                    ${base_libdir}/udev/.debug \
                    ${libdir}/udev/.debug \
                    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${base_libdir}/security/.debug/pam_systemd.so', '', d)} \
                    ${PYTHON_SITEPACKAGES_DIR}/systemd/.debug \
                   "
FILES_${PN}-dev = ""
ALLOW_EMPTY_${PN}-dev = "1"

FILES_libsystemd = "${base_libdir}/libsystemd${SOLIBS}"
FILES_libsystemd-dev = "${base_libdir}/libsystemd.so \
                        ${base_libdir}/libsystemd.la \
                        ${base_libdir}/pkgconfig/libsystemd.pc \
                        ${includedir}/systemd \
                       "

FILES_libpam-systemd = "${base_libdir}/security/pam_systemd.so \
                        ${datadir}/pam-configs/systemd \
                       "

FILES_udev = "${base_bindir}/udevadm \
              ${base_bindir}/systemd-hwdb \
              ${base_sbindir}/udevadm \
              ${systemd_system_unitdir}/sockets.target.wants/systemd-udevd-*.socket \
              ${systemd_system_unitdir}/sysinit.target.wants/systemd-udev-trigger.service \
              ${systemd_system_unitdir}/sysinit.target.wants/systemd-udevd.service \
              ${systemd_system_unitdir}/sysinit.target.wants/udev-finish.service \
              ${systemd_system_unitdir}/sysinit.target.wants/systemd-hwdb-update.service \
              ${systemd_system_unitdir}/systemd-hwdb-update.service \
              ${systemd_system_unitdir}/systemd-udev-settle.service \
              ${systemd_system_unitdir}/systemd-udev-trigger.service \
              ${systemd_system_unitdir}/systemd-udevd-control.socket \
              ${systemd_system_unitdir}/systemd-udevd-kernel.socket \
              ${systemd_system_unitdir}/systemd-udevd.service \
              ${systemd_system_unitdir}/udev.service \
              ${base_libdir}/systemd/network/99-default.link \
              ${base_libdir}/systemd/systemd-udevd \
              ${base_libdir}/udev/ata_id \
              ${base_libdir}/udev/cdrom_id \
              ${base_libdir}/udev/collect \
              ${base_libdir}/udev/hwdb.d \
              ${base_libdir}/udev/mtd_probe \
              ${base_libdir}/udev/rules.d/50-* \
              ${base_libdir}/udev/rules.d/60-* \
              ${base_libdir}/udev/rules.d/64-btrfs.rules \
              ${base_libdir}/udev/rules.d/70-debian-uaccess.rules \
              ${base_libdir}/udev/rules.d/70-mouse.rules \
              ${base_libdir}/udev/rules.d/70-power-switch.rules \
              ${base_libdir}/udev/rules.d/73-special-net-names.rules \
              ${base_libdir}/udev/rules.d/73-usb-net-by-mac.rules \
              ${base_libdir}/udev/rules.d/75-* \
              ${base_libdir}/udev/rules.d/78-sound-card.rules \
              ${base_libdir}/udev/rules.d/80-* \
              ${base_libdir}/udev/scsi_id \
              ${base_libdir}/udev/v4l_id \
              ${sysconfdir}/init.d \
              ${sysconfdir}/init/udev* \
              ${sysconfdir}/udev/udev.conf \
              ${sysconfdir}/udev/rules.d \
              ${sysconfdir}/udev/hwdb.d \
              ${base_libdir}/modprobe.d/fbdev-blacklist.conf \
              ${datadir}/pkgconfig/udev.pc \
              ${datadir}/bash-completion/completions/udevadm \
              ${datadir}/initramfs-tools \
              ${datadir}/zsh/vendor-completions/_udevadm \
             "

FILES_libudev = "${base_libdir}/libudev${SOLIBS}"
FILES_libudev-dev = "${includedir}/libudev.h \
                     ${base_libdir}/libudev.so \
                     ${base_libdir}/libudev.la \
                     ${base_libdir}/pkgconfig/libudev.pc \
                     ${base_libdir}/udev/test-udev \
                    "
FILES_systemd-sysv = " ${base_sbindir}/init \
                       ${base_sbindir}/halt \
                       ${base_sbindir}/poweroff \
                       ${base_sbindir}/reboot \
                       ${base_sbindir}/runlevel \
                       ${base_sbindir}/shutdown \
                       ${base_sbindir}/telinit \
                     "
RDEPENDS_${PN} += "systemd-sysv"
RDEPENDS_libnss-resolve += "${PN}"
RDEPENDS_libpam-systemd += "${PN} libpam-runtime dbus systemd-sysv"
RDEPENDS_systemd-journal-remote += "adduser"
RDEPENDS_systemd-coredump += "adduser"
RPROVIDES_systemd-coredump += "core-dump-handler"
RPROVIDES_libsystemd += "libsystemd0"

# init script requires init-functions, procps's ps, and mountpoint
RDEPENDS_udev += "lsb-base procps sysvinit-mountpoint"

inherit update-alternatives

ALTERNATIVE_${PN} = "init halt reboot shutdown poweroff runlevel"

ALTERNATIVE_TARGET[init] = "${base_bindir}/systemd"
ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
ALTERNATIVE_PRIORITY[init] ?= "300"

ALTERNATIVE_TARGET[halt] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[halt] = "${base_sbindir}/halt"
ALTERNATIVE_PRIORITY[halt] ?= "300"

ALTERNATIVE_TARGET[reboot] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[reboot] = "${base_sbindir}/reboot"
ALTERNATIVE_PRIORITY[reboot] ?= "300"

ALTERNATIVE_TARGET[shutdown] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[shutdown] = "${base_sbindir}/shutdown"
ALTERNATIVE_PRIORITY[shutdown] ?= "300"

ALTERNATIVE_TARGET[poweroff] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[poweroff] = "${base_sbindir}/poweroff"
ALTERNATIVE_PRIORITY[poweroff] ?= "300"

ALTERNATIVE_TARGET[runlevel] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[runlevel] = "${base_sbindir}/runlevel"
ALTERNATIVE_PRIORITY[runlevel] ?= "300"

DEBIAN_NOAUTONAME_libnss-myhostname = "1"
DEBIAN_NOAUTONAME_libnss-mymachines = "1"
DEBIAN_NOAUTONAME_libnss-resolve = "1"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} +=  "--system --no-create-home --home /run/systemd --shell /bin/false --user-group systemd-timesync; --system --no-create-home --home /run/systemd/netif --shell /bin/false --user-group systemd-network; --system --no-create-home --home /run/systemd/resolve --shell /bin/false --user-group systemd-resolve; --system --no-create-home --home /run/systemd --shell /bin/false --user-group systemd-bus-proxy"
GROUPADD_PARAM_${PN} += "--system systemd-journal"

# Follow debian/systemd-coredump.postinst
pkg_postinst_systemd-coredump() {
	# enable systemd-coredump right after package installation
	if [ -d $D/run/systemd/system ]; then
		systemctl daemon-reload && systemctl start systemd-coredump.socket || true
	fi
	$D${base_libdir}/systemd/systemd-sysctl $D${libdir}/sysctl.d/50-coredump.conf || true
}
USERADD_PARAM_systemd-coredump += "--system --no-create-home --home /run/systemd systemd-coredump"

# Avoid a parallel build problem
PARALLEL_MAKE = ""
