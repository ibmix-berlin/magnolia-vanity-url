Magnolia Vanity-URL App
=======================

A [module](https://documentation.magnolia-cms.com/display/DOCS/Modules) containing an [app](https://documentation.magnolia-cms.com/display/DOCS/Apps) for the [Magnolia CMS](http://www.magnolia-cms.com)

Allows to configure vanity URLs in the Magnolia CMS without requiring access to the config workspace. Ideal for page/content editors who are not supposed to write to the config workspace. Also creates QR codes.

License
-------

Released under the GPLv3, see LICENSE.txt. 

Feel free to use this app, but if you modify the source code please fork us on Github.

Magnolia Forge
--------------
Though the sourcecode is kept on Github, this module is part of the [Magnolia Forge](http://forge.magnolia-cms.com/) and uses its infrastructure.

Issue tracking
--------------
Issues are tracked at the [Magnolia JIRA for this Magnolia Forge module](https://jira.magnolia-cms.com/browse/VANITY).
Any bug reports, improvement or feature requests are welcome! 

Maven artifacts in Magnolia's Nexus
---------------------------------
The code is built on [Magnolia's Jenkins](http://jenkins.magnolia-cms.com/job/forge_magnolia-vanity-url/), and Maven artifacts are available through [Magnolia's Forge release repository](http://nexus.magnolia-cms.com/content/repositories/magnolia.forge.releases/) and [Forge snapshot repository](http://nexus.magnolia-cms.com/content/repositories/magnolia.forge.snapshots/). 

You can browse available artifacts through [Magnolia's Nexus](http://nexus.magnolia-cms.com/#nexus-search;quick~magnolia-vanity-url)

Maven dependency
-----------------
```xml
        <dependency>
            <artifactId>magnolia-vanity-url</artifactId>
            <groupId>com.aperto.magkit</groupId>
            <version>1.4.1</version>
        </dependency>
```

Versions
-----------------
Version 1.2.x is compatible with Magnolia 5.2.x
Version 1.3.x is compatible with Magnolia 5.3.x
Version 1.4.x is compatible with Magnolia 5.4.x

Magnolia Module Configuration
-----------------
In the module configuration of the vanity url module, you can configure the following settings:
* _excludes_ : Pattern of urls, which are no candidates for vanity urls.
  * by default an exclude for all urls containing a dot is configured, that prevents the virtual uri mapping checks every ordinary request like script.js or page.html 
* _publicUrlService_ : Implementation of _com.aperto.magnolia.vanity.PublicUrlService_. Two implementations are already available.
  * _com.aperto.magnolia.vanity.DefaultPublicUrlService_ (default) : Use of default base url and site configuration with context path replacement.
  * _com.aperto.magnolia.vanity.SimplePublicUrlService_ : Used configured public prefix and removes the author context path.
