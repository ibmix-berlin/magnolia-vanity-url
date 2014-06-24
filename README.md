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
Once the build job on [Magnolia's Jenkins](http://jenkins.magnolia-cms.com/) is in place and everything is setup properly, you will be able to retrieve artifacts through [Magnolia's Forge release repository](http://nexus.magnolia-cms.com/content/repositories/magnolia.forge.releases/) and [Forge snapshot repository](http://nexus.magnolia-cms.com/content/repositories/magnolia.forge.snapshots/). 

Please stay tuned...

Maven dependency
-----------------
```xml
        <dependency>
            <artifactId>magnolia-vanity-url</artifactId>
            <groupId>com.aperto.magkit</groupId>
            <version>1.2.0-SNAPSHOT</version>
        </dependency>
```        