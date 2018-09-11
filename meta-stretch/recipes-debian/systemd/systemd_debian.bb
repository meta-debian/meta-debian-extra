#
# base-recipe: meta-debian/recipes-debian/systemd/systemd_debian.bb 
# base-branch: jethro
#
SUMMARY = "A System and service manager"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/systemd"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-system-232-r0-remake-the-patch-that-doesn-t-generate.patch"

PV = "232"
PROVIDES += "udev libudev libudev-dev"
DEPENDS_libudev-dev += "libudev"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
file://LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe \
file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c \
"
inherit debian-package

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
	echo "do_install_append is called."
	# systemd package: setup base_bindir
	ln -s ${nonarch_base_libdir}/systemd/systemd ${D}${base_bindir}

	# create machine-id
	touch ${D}${sysconfdir}/machine-id

	# systemd package: setup sysconfdir
	ln -s ../modules ${D}${sysconfdir}/modules-load.d/modules.conf
	ln -s ../sysctl.conf ${D}${sysconfdir}/sysctl.d/99-sysctl.conf
	for dir in binfmt.d kernel tmpfiles.d; do
		if [ -d ${D}${sysconfdir}/${dir} ]; then
			rm -rf ${D}${sysconfdir}/${dir}
		fi
	done
	rm ${D}${sysconfdir}/init.d/README

	for name in system-shutdown system-sleep systemd-update-done; do
		if [ -d ${D}${nonarch_base_libdir}/systemd/${name} ]; then
			rm -rf ${D}${nonarch_base_libdir}/systemd/${name}
		fi
	done
	# systemd package: setup bindir
	rm ${D}${bindir}/kernel-install
	# systemd package: setup libdir
	for dir in binfmt.d kernel modules-load.d rpm \
	           systemd/network systemd/user-generators \
	           tmpfiles.d/etc.conf; do
		if [ -f ${D}${libdir}/../${dir} -o -d ${D}${libdir}/../${dir} ]; then
			echo "rm -rf ${D}${libdir}/../${dir}"
			rm -rf ${D}${libdir}/../${dir}
		else
			echo "rm -rf ${D}${libdir}/${dir}"
			rm -rf ${D}${libdir}/${dir}
		fi
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
	install -d ${D}${nonarch_base_libdir}/modprobe.d
	install -d ${D}${systemd_system_unitdir}/sysinit.target.wants
	ln -s systemd-udevd.service \
		${D}${systemd_system_unitdir}/udev.service
	install -d ${D}${base_sbindir}
	ln -s ${base_bindir}/udevadm ${D}${base_sbindir}
	ln -s ${nonarch_base_libdir}/systemd/systemd-udevd ${D}${base_sbindir}/udevd

	# ship test-udev, so that we have it for autopkgtests
	if [ -e ${B}/.libs/test-udev ]; then
		install -D -m 0755 ${B}/.libs/test-udev ${D}${libdir}/udev/test-udev
	else
		install -D -m 0755 ${B}/test-udev ${D}${libdir}/udev/test-udev
	fi
	
	# follow debian/udev.install
	cp -r ${S}/debian/extra/initramfs-tools ${D}${datadir}
	install -d ${D}${nonarch_base_libdir}/udev/rules.d/
	cp -r ${S}/debian/extra/rules/*.rules ${D}${nonarch_base_libdir}/udev/rules.d/

	install -D -m 0644 ${S}/debian/udev.upstart \
	                   ${D}${sysconfdir}/init/udev.conf
	install -m 0644 ${S}/debian/udev.udevmonitor.upstart \
	                ${D}${sysconfdir}/init/udevmonitor.conf
	install -m 0644 ${S}/debian/udev.udevtrigger.upstart \
	                ${D}${sysconfdir}/init/udevtrigger.conf

	# follow debian/udev-udev.dirs
	install -d ${D}${sysconfdir}/udev/rules.d

	#follow debian/systemd.install
	install -d ${D}${nonarch_base_libdir}/lsb \
	           ${D}${sysconfdir}/dhcp \
		   ${D}${nonarch_libdir}/tmpfiles.d \
		   ${D}${nonarch_base_libdir}/systemd

	cp -r ${S}/debian/extra/init-functions.d ${D}${nonarch_base_libdir}/lsb/
	cp -r ${S}/debian/extra/tmpfiles.d/*.conf ${D}${nonarch_libdir}/tmpfiles.d/
	cp -r ${S}/debian/extra/systemd-sysv-install ${D}${nonarch_base_libdir}/systemd/
	cp -r ${S}/debian/extra/units/* ${D}${systemd_system_unitdir}/
	cp -r ${S}/debian/extra/dhclient-exit-hooks.d \
	      ${D}${sysconfdir}/dhcp/

	# systemd-sysv
	ln -s ../${nonarch_base_libdir}/systemd/systemd  ${D}${base_sbindir}/init
	ln -s ../${nonarch_base_bindir}/systemctl ${D}${base_sbindir}/halt
	ln -s ../${nonarch_base_bindir}/systemctl ${D}${base_sbindir}/poweroff
	ln -s ../${nonarch_base_bindir}/systemctl ${D}${base_sbindir}/reboot
	ln -s ../${nonarch_base_bindir}/systemctl ${D}${base_sbindir}/runlevel
	ln -s ../${nonarch_base_bindir}/systemctl ${D}${base_sbindir}/shutdown
	ln -s ../${nonarch_base_bindir}/systemctl ${D}${base_sbindir}/telinit
	
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
	rm -rf ${D}${nonarch_base_sbindir}/udevd \
	       ${D}${sysconfdir}/X11 \
	       ${D}${nonarch_libdir}/sysctl.d/50-default.conf \
	       ${D}${nonarch_base_libdir}/systemd/libsystemd-shared.so \
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
        rm -rf ${D}${nonarch_libdir}/sysusers.d
        rm -f ${D}${systemd_system_unitdir}/*sysusers*.service \
	      ${D}${systemd_system_unitdir}/*/*sysusers*.service

	# remove unnecessary la files. 
	for i in libudev libnss_resolve libnss_myhostname libnss_mymachines libnss_systemd; do
		rm -f ${D}${base_libdir}/${i}.la
	done
	rm -fr ${D}${nonarch_base_libdir}/modprobe.d ${D}${base_libdir}/modprobe.d
}


PACKAGES =+ "libnss-myhostname \
             libnss-mymachines \
             libnss-resolve \
             libnss-systemd \
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
FILES_libnss-systemd = "${base_libdir}/libnss_systemd${SOLIBS}"
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
               ${libdir} \
               ${datadir} \
               ${sysconfdir}/* \
               ${sysconfdir}/dbus-1/system.d/*.conf \
               ${sysconfdir}/modules-load.d/modules.conf \
               ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${sysconfdir}/pam.d/systemd-user', '', d)} \
               ${sysconfdir}/sysctl.d/99-sysctl.conf \
               ${sysconfdir}/systemd/*.conf \
               ${sysconfdir}/xdg/systemd/user \
               ${sysconfdir}/machine-id \
               ${nonarch_libdir}/tmpfiles.d \
               ${nonarch_libdir}/tmpfiles.d/*.conf \
               ${sysconfdir}/dhcp/dhclient-exit-hooks.d/timesyncd \
               ${systemd_system_unitdir}/getty.target.wants/* \
               ${systemd_system_unitdir}/graphical.target.wants/* \
               ${systemd_system_unitdir}/local-fs.target.wants/* \
               ${systemd_system_unitdir}/multi-user.target.wants/* \
               ${systemd_system_unitdir}/rescue.target.wants/systemd-update-utmp-runlevel.service \
               ${systemd_system_unitdir}/sysinit.target \
               ${systemd_system_unitdir}/sysinit.target.want/* \
               ${systemd_system_unitdir}/*.socket \
               ${systemd_system_unitdir}/*.target \
               ${systemd_system_unitdir}/*.service \
               ${systemd_system_unitdir}/* \
               ${systemd_system_unitdir}/systemd-resolved.service.d/resolvconf.conf \
               ${systemd_system_unitdir}/timers.target.wants/*.timer \
               ${nonarch_base_libdir}/lsb/init-functions.d/40-systemd \
               ${nonarch_base_libdir}/systemd/* \
               ${nonarch_base_libdir}/systemd/resolv.conf \
               ${nonarch_base_libdir}/systemd/libsystemd-shared-${PV}.so \
               ${nonarch_base_libdir}/udev/rules.d/70-uaccess.rules \
               ${nonarch_base_libdir}/udev/rules.d/71-seat.rules \
               ${nonarch_base_libdir}/udev/rules.d/73-seat-late.rules \
               ${nonarch_base_libdir}/udev/rules.d/99-systemd.rules \
               ${nonarch_base_libdir}/systemd/system-generators \
               ${nonarch_base_libdir}/systemd/system-generators/*generator \
               ${nonarch_libdir}/systemd/system-preset/* \
               ${nonarch_libdir}/systemd/catalog/*.catalog \
               ${nonarch_libdir}/systemd/user/*.target \
               ${nonarch_libdir}/systemd/user/*.service \
               ${nonarch_libdir}/tmpfiles.d/*.conf \
               ${nonarch_libdir}/systemd/boot/efi/* \
               ${datadir}/bug/systemd/* \
               ${datadir}/dbus-1/services/* \
               ${datadir}/dbus-1/system-services/* \
               ${datadir}/polkit-1/actions/*.policy \
               ${datadir}/locale/*/LC_MESSAGES/systemd.mo \
               ${datadir}/systemd/* \
               ${datadir}/bash-completion/completions/* \
               ${datadir}/zsh/vendor-completions/* \ 
               ${bindir}/bootctl \
               ${bindir}/busctl \
               ${bindir}/hostnamectl \
               ${bindir}/kernel-install \
               ${bindir}/localectl \
               ${bindir}/systemd-* \
               ${bindir}/timedatectl \
               ${nonarch_libdir}/sysctl.d/50-coredump.conf \
              "
FILES_${PN}-dbg += "${nonarch_base_libdir}/systemd/.debug \
                    ${nonarch_base_libdir}/systemd/system-generators/.debug \
                    ${nonarch_base_libdir}/udev/.debug \
                    ${nonarch_libdir}/udev/.debug \
                    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${nonarch_base_libdir}/security/.debug/pam_systemd.so', '', d)} \
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
              ${base_sbindir}/udevd \
              ${systemd_system_unitdir}/sockets.target.wants/systemd-udevd-control.socket \
              ${systemd_system_unitdir}/sockets.target.wants/systemd-udevd-kernel.socket \
              ${systemd_system_unitdir}/sysinit.target.wants/systemd-hwdb-update.service \
              ${systemd_system_unitdir}/sysinit.target.wants/systemd-udev-trigger.service \
              ${systemd_system_unitdir}/sysinit.target.wants/systemd-udevd.service \
              ${systemd_system_unitdir}/systemd-hwdb-update.service \
              ${systemd_system_unitdir}/systemd-udev-settle.service \
              ${systemd_system_unitdir}/systemd-udev-trigger.service \
              ${systemd_system_unitdir}/systemd-udevd-control.socket \
              ${systemd_system_unitdir}/systemd-udevd-kernel.socket \
              ${systemd_system_unitdir}/systemd-udevd.service \
              ${systemd_system_unitdir}/udev.service \
              ${nonarch_base_libdir}/systemd/systemd-udevd \
              ${nonarch_base_libdir}/udev/ata_id \
              ${nonarch_base_libdir}/udev/cdrom_id \
              ${nonarch_base_libdir}/udev/collect \
              ${nonarch_base_libdir}/udev/hwdb.d/20-OUI.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/20-acpi-vendor.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/20-bluetooth-vendor-product.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/20-net-ifname.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/20-pci-classes.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/20-pci-vendor-model.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/20-sdio-classes.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/20-sdio-vendor-model.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/20-usb-classes.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/20-usb-vendor-model.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/60-evdev.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/60-keyboard.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/70-mouse.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/70-pointingstick.hwdb \
              ${nonarch_base_libdir}/udev/hwdb.d/70-touchpad.hwdb \
              ${nonarch_base_libdir}/udev/mtd_probe \
              ${nonarch_base_libdir}/udev/rules.d/50-firmware.rules \
              ${nonarch_base_libdir}/udev/rules.d/50-udev-default.rules \
              ${nonarch_base_libdir}/udev/rules.d/60-block.rules \
              ${nonarch_base_libdir}/udev/rules.d/60-cdrom_id.rules \
              ${nonarch_base_libdir}/udev/rules.d/60-drm.rules \
              ${nonarch_base_libdir}/udev/rules.d/60-evdev.rules \
              ${nonarch_base_libdir}/udev/rules.d/60-persistent-alsa.rules \
              ${nonarch_base_libdir}/udev/rules.d/60-persistent-input.rules \
              ${nonarch_base_libdir}/udev/rules.d/60-persistent-storage-tape.rules \
              ${nonarch_base_libdir}/udev/rules.d/60-persistent-storage.rules \
              ${nonarch_base_libdir}/udev/rules.d/60-persistent-v4l.rules \
              ${nonarch_base_libdir}/udev/rules.d/60-serial.rules \
              ${nonarch_base_libdir}/udev/rules.d/64-btrfs.rules \
              ${nonarch_base_libdir}/udev/rules.d/70-debian-uaccess.rules \
              ${nonarch_base_libdir}/udev/rules.d/70-mouse.rules \
              ${nonarch_base_libdir}/udev/rules.d/70-power-switch.rules \
              ${nonarch_base_libdir}/udev/rules.d/70-touchpad.rules \
              ${nonarch_base_libdir}/udev/rules.d/73-special-net-names.rules \
              ${nonarch_base_libdir}/udev/rules.d/73-usb-net-by-mac.rules \
              ${nonarch_base_libdir}/udev/rules.d/75-net-description.rules \
              ${nonarch_base_libdir}/udev/rules.d/75-probe_mtd.rules \
              ${nonarch_base_libdir}/udev/rules.d/78-sound-card.rules \
              ${nonarch_base_libdir}/udev/rules.d/80-debian-compat.rules \
              ${nonarch_base_libdir}/udev/rules.d/80-drivers.rules \
              ${nonarch_base_libdir}/udev/rules.d/80-net-setup-link.rules \
              ${nonarch_base_libdir}/udev/scsi_id \
              ${nonarch_base_libdir}/udev/v4l_id \
              ${sysconfdir}/udev/udev.conf \
              ${sysconfdir}/init.d/udev \
              ${sysconfdir}/init/udev.conf \
              ${sysconfdir}/init/udevmonitor.conf \
              ${sysconfdir}/init/udevtrigger.conf \
              ${datadir}/bash-completion/completions/udevadm \
              ${datadir}/bug/udev/control \
              ${datadir}/bug/udev/script \
              ${datadir}/initramfs-tools/hooks/udev \
              ${datadir}/initramfs-tools/scripts/init-bottom/udev \
              ${datadir}/initramfs-tools/scripts/init-top/udev \
              ${datadir}/pkgconfig/udev.pc \
              ${datadir}/zsh/vendor-completions/_udevadm \
             "

FILES_libudev = "${base_libdir}/libudev${SOLIBS}"
FILES_libudev-dev = "${includedir}/libudev.h \
                     ${base_libdir}/libudev.so \
                     ${libdir}/pkgconfig/libudev.pc \
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
RDEPENDS_udev += "acl kmod util-linux lsb-base procps sysvinit-mountpoint libudev"

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
	$D${nonarch_base_libdir}/systemd/systemd-sysctl $D${nonarch_libdir}/sysctl.d/50-coredump.conf || true
}
USERADD_PARAM_systemd-coredump += "--system --no-create-home --home /run/systemd systemd-coredump"

# Avoid a parallel build problem
PARALLEL_MAKE = ""
