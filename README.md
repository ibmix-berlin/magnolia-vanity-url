Magnolia Vanity-URL App
=======================

[![Build Status](https://travis-ci.com/aperto/magnolia-vanity-url.svg?branch=master)](https://travis-ci.com/aperto/magnolia-vanity-url) 
[![Magnolia compatibility](https://img.shields.io/badge/magnolia-5.4-brightgreen.svg)](https://www.magnolia-cms.com)
[![Magnolia compatibility](https://img.shields.io/badge/magnolia-5.5-brightgreen.svg)](https://www.magnolia-cms.com)
[![Magnolia compatibility](https://img.shields.io/badge/magnolia-5.6-brightgreen.svg)](https://www.magnolia-cms.com)
[![Magnolia compatibility](https://img.shields.io/badge/magnolia-5.7-brightgreen.svg)](https://www.magnolia-cms.com)
[![Magnolia compatibility](https://img.shields.io/badge/magnolia-6.1-brightgreen.svg)](https://www.magnolia-cms.com)
[![Magnolia compatibility](https://img.shields.io/badge/magnolia-6.2-brightgreen.svg)](https://www.magnolia-cms.com)

Attention
---------
Project was moved to https://github.com/IBM/magkit-vanity-url.

With that also the Maven coords will be changed for all version higher than `1.6.0`.

Introduction
------------

A [module](https://documentation.magnolia-cms.com/display/DOCS/Modules) containing an [app](https://documentation.magnolia-cms.com/display/DOCS/Apps) for the [Magnolia CMS](http://www.magnolia-cms.com)

Allows to configure vanity URLs in the Magnolia CMS without requiring access to the config workspace. Ideal for page/content editors who are not supposed to write to the config workspace. Also creates QR codes for quick testing with your mobile phone.

License
-------

Released under the GPLv3, see LICENSE.txt. 

Feel free to use this app, but if you modify the source code please fork us on Github.

Issue tracking
--------------
Issues are tracked at [GitHub](https://github.com/aperto/magnolia-vanity-url/issues).
Any bug reports, improvement or feature requests are welcome! 

Maven artifacts in Magnolia's Nexus
---------------------------------
The code is built on [Travis CI](https://travis-ci.com/aperto/magnolia-vanity-url).
You can browse available artifacts through [Magnolia's Nexus](https://nexus.magnolia-cms.com/#nexus-search;quick~magnolia-vanity-url)

Maven dependency
-----------------
```xml
    <dependency>
        <artifactId>magnolia-vanity-url</artifactId>
        <groupId>com.aperto.magkit</groupId>
        <version>1.6.0</version>
    </dependency>
```

Versions
-----------------
* Version 1.2.x is compatible with Magnolia 5.2.x
* Version 1.3.x is compatible with Magnolia 5.3.x
* Version 1.4.x is compatible with Magnolia 5.4.x and 5.5.x
* Version 1.5.x is compatible with Magnolia 5.6.x, 5.7.x, 6.1.x and 6.2.x
* Version 1.6.x is compatible with Magnolia 6.2.x (new UI support)

Magnolia Module Configuration
-----------------
In the module configuration of the vanity url module, you can configure the following settings:
* _excludes_ : Pattern of urls, which are no candidates for vanity urls.
  * by default an exclude for all urls containing a dot is configured, that prevents the virtual uri mapping checks every ordinary request like script.js or page.html 
* _publicUrlService_ : Implementation of _com.aperto.magnolia.vanity.PublicUrlService_. Two implementations are already available.
  * _com.aperto.magnolia.vanity.DefaultPublicUrlService_ (default) : Use of default base url and site configuration with context path replacement.
  * _com.aperto.magnolia.vanity.SimplePublicUrlService_ : Used configured public prefix and removes the author context path.
