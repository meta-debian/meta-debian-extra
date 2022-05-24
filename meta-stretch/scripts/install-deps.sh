#!/bin/sh
# Install packages required to use meta-debian-extra/meta-stretch

if [ "$(whoami)" != "root" ]; then
	echo "Please run this script as root"
	exit 1
fi

# Require for building ruby
# meta-stretch/conf/layer.conf: SANITY_REQUIRED_UTILITIES += "ruby"
apt-get install ${@} ruby
