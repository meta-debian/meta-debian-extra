
What is meta-debian-extra?
==========================

meta-debian-extra has additional recipes to meta-debian.
These recipes have upper version than recipes in meta-debian.

For now, jethro branch in meta-debian has recipe for jessie(debian 8) package.
On the other hand, jethro branch in meta-deiban-extra has recipes for jessie-backports/stretch pakcages.

The branch name is one-to-one correspondence between meta-debian and meta-debian-extra.

Quick Start
===========

meta-debian-extra needs meta-debian, so that meta-debian-extra cannot bitbake without meta-debian.
Please refer to README in meta-debian for how to bitbake with meta-debian.

https://github.com/meta-debian/meta-debian

There should be meta-debian-extra in poky directory same as meta-debian.

```sh
./poky/
├── LICENSE
├── README
├── README.hardware
├── bitbake
├── documentation
├── meta
├── meta-debian
├── meta-debian-extra <- here
├── meta-selftest
├── meta-skeleton
├── meta-yocto
├── meta-yocto-bsp
├── oe-init-build-env
├── oe-init-build-env-memres
├── patches
└── scripts
```

#### BBLAYERS

You need to set BBLAYERS in your project to include meta-debian-extra.

```sh
 BBLAYERS ?= " \
   ##OEROOT##/meta \
   ##OEROOT##/meta-debian-extra/meta-stretch \
   ##OEROOT##/meta-debian-extra/meta-jessie-backports \
   ##OEROOT##/meta-debian \
   ##OEROOT##/meta-your-project \
   "
```

#### Layer Priority

meta-debian-extra layer has lower priority than meta-debian.
So all recipe in meta-debian-extra, which has same name as recipes in meta-debian, are overridden by default.
That's why, if you would like to build jessie-backports/stretch packages,
you have to define BBMASK to ignore recipes in meta-debian, like

```sh
BBMASK  = "/meta-debian/recipes-debian/RECIPE-A/"
BBMASK .= "|/meta-debian/recipes-debian/RECIPE-B/"
BBMASK .= "|/meta-debian/recipes-debian/RECIPE-C/"
```

in local.conf for meta-your-project,

NOTE: meta-jessie-backports and meta-stretch has same priority.

License
=======

License of meta-debian is same as meta in poky i.e. All metadata is MIT licensed unless otherwise stated. Source code included in tree for individual recipes is under the LICENSE stated in the associated recipe (.bb file) unless otherwise stated.

See COPYING.MIT for more details about MIT license.

Community Resources
===================

#### Project home

* https://github.com/meta-debian/meta-debian-extra

#### Mailing list

* meta-debian@googlegroups.com

#### Mailing list subscription

* meta-debian+subscribe@googlegroups.com
* https://groups.google.com/forum/#!forum/meta-debian/join
