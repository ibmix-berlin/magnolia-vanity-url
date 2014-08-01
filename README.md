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
            <version>1.2.0</version>
        </dependency>
```        
Versions
-----------------
Version 1.2.0 is compatible with Magnolia 5.2

Version 1.2.1-SNAPSHOT onwards is compatible with Magnolia 5.3
