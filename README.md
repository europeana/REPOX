**CAUTION!**  
**The information below is subject to change**

###REPOX - Data Aggregation and Interoperability Manager ###
- - - 
REPOX is a framework to manage data spaces. It comprises several channels to import data from data providers, services to transform data between schemas according to user's specified rules, and services to expose the results to the exterior. This tailored version of REPOX aims to provide to all the TEL and Europeana partners a simple solution to import, convert and expose their bibliographic data via OAI-PMH, by the following means:

* __Cross platform__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;It is developed in Java, so it can be deployed in any operating system that has an available Java virtual machine.

* __Easy deployment__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;With the new wiki pages, there are full instructions on how to deploy an instance.

* __Support for several data formats and encodings__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;It supports UNIMARC and MARC21 schemas, and encodings in ISO 2709 (including several variants), MarcXchange or MARCXML. During the course of the TELplus project, support will be added for other possible encodings required by the partners.

* __Data crosswalks__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;It offers crosswalks for converting UNIMARC and MARC21 records to simple Dublin Core as also to TEL-AP (TEL Application Profile). A simple user interface makes it possible to customize these crosswalks, and create new ones for other formats.

Original repox site:  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Repox original website](http://repox.ist.utl.pt/ "Repox original website")

Repox github site:  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Repox github development](https://github.com/europeana/repox2 "Repox github development")

###Contributor###
- - - 
<img src="http://www.theeuropeanlibrary.org/confluence/download/attachments/8880494/TEL_logoe_transparent_AEtry-out.jpg" alt="The European Library" width=200px/> <img src="http://www.axes-project.eu/wp-content/uploads/2012/02/europeana.jpg" alt="Europeana" width=200px/>  
The European Library and Europeana are the main contributors of refactoring the whole REPOX project as it was originally delivered at v2.3.5 and is heading to v3.0 and upwards,  
including new implementations of REST API's, bug fixing and introducing new features.

###Repox Structure Overview###
- - - 
Repox consists of the following modules:

* __Repox Parent (repox-system)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This project is basically the parent project containing all the modules.
* __Repox Gui (repox-gui)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The GUI implementation using GWT. This is the main war build that should be deployed in a servlet container.  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The URL configuration of swagger-core dynamic JSON generation is located in the `web.xml`.
* __Repox Manager (repox-manager)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; As the name of the module describes it is the core manager of the repox features. 
* __Repox Commons (repox-commons)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Contains common classes for the rest of the modules
* __Repox Resources (repox-resources)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This is the module for centralization of common resources.
* __Repox Server (repox-server)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The repox-server module is another parent project that contains the following modules repox-server-rest, repox-server-rest-jersey, repox-server-oai.
* __Repox Server Rest Jersey(repox-server-rest-jersey)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The new RESTful API service, using Jersey 2 framework for accessing Repox functionality. This module can be build and deployed independently from the repox-gui.  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The URL configuration of swagger-core dynamic JSON generation is located in the `web.xml`.  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Spring security is configured in the file `security.xml`.   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; When the full build is initiated, then the `web.xml` of the `repox-gui` is the valid one, so any values in this `web.xml` must be copied in the `web.xml` of the `repox-gui`.
* __Repox Server OAI (repox-server-oai)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; An OAI-PMH endpoint for accessing information of Repox datasets. This module can be build and deployed independently from the repox-gui.

###General Information###
- - -
*  Check the wiki pages for more information - [Wiki](https://github.com/europeana/REPOX/wiki)
*  A separate configuration file, instead the one on `WEB-INF/classes` directory of the .war, can be used by declaring a JVM system variable of the directory where the  
required `configuration.properties` file is located(this applies also for the `oaicat.properties` file), like so `repox.conf.dir=/path/to/configuration/dir`
*  All the configuration files in Repox are always read once and then the information is kept in memory, any changes on the files are not reflected to the memory.  
Repox overwrites these files on changes, so for example a change that is made on the `configuration.properties` file will not be visible to the implementation,  
it will only be visible only if Repox is restarted.
*  For development builds the permutations have been reduced in the file `/repox-gui/src/main/java/harvesterUI/HarvesterUI.gwt.xml`.  
For other configuration needs, this file needs to be reconfigured.
*  Tests on the Repox build are skipped by default in the `maven-surefire-plugin` and can be activated from the `repox-system` `pom.xml` by changing  
the property `skipTests` from `true` to `false`, after refactoring the only updated test are in `repox-server-rest-jersey`.

###Building Repox###
- - -
Repox is a maven project and can be easily build by building the root project where the parent pom is.
The command to build is:

&nbsp;&nbsp;&nbsp;&nbsp; `mvn clean install -Pproduction`  

####Profiles####

1. `production`

    This is the profile that should be used when everything is in place and the resources of the `repox-resources` project are updated.  
    Always run this profile when a project is ready to run outside of an IDE.  
    <b>!Caution:</b> Deletes specific resources that are bound in the `src/main/resources` and then generates the build. The resources that are being deleted are resources that were  
    previously generated on demand when there is the need to run a module as a standalone or in an IDE(e.g. testing the REST API module).

2. `copy-resources`

    Maven profile that generates copies of specific configuration files from the repox-resources project to the maven `src/main/resources`.  
    &nbsp;&nbsp;&nbsp;&nbsp; `process-resources -Pcopy-resources`  
    This profile is bound to the `process-resources` phase of maven so its sufficient to run the maven build until this phase to get the resources(In some IDE's a refresh of the project is needed).  
    The resources that are being generated are resources that are required on demand when there is the need to run a module as a standalone or in an IDE(e.g. testing the REST API module).  
    After copying the `copy-resources` profile is run then a normal build without a profile can be run `clean package`.

3. `generate-doc`

    Repox uses swagger 2.0 for documenting the REST API of `repox-server-rest-jersey`. 
    This profile is bound to the `generate-resources`.  
    &nbsp;&nbsp;&nbsp;&nbsp; `generate-resources -Pgenerate-doc`  
    It uses Server integration that dynamically provides JSON at runtime BUT If in any case the json needs to be generated in files, this profile can be used to generate them and the applicable  
    url and document locations can be edited in the `pom.xml` of the module.


###Deploying Repox###
- - -
For Repox deployment there is a need of a PostgreSQL or MySQL database server and a servlet container as tomcat.  
Tested versions are:  
&nbsp;&nbsp;&nbsp;&nbsp; <b>PostgreSQL</b> 8.3.17, 9.1.14, 9.3.5  
&nbsp;&nbsp;&nbsp;&nbsp; <b>MySQL</b> 5.5.40, 5.6.20  
&nbsp;&nbsp;&nbsp;&nbsp; <b>Tomcat</b> 6.0.41, 7.0.55, 8.0.12  

Strongly recommended the use of PostgreSQL, as it has the latest updates.  

Additionally a database schema with a username and password that can access and modify it, is needed in the SQL server.
These information must be added in the `configuration.properties` file mentioned above.  

After the above software is configured the .war file, created by building the <b>repox-system</b> and located under `/repox-gui/target`, can be moved in the `webapps` directory of tomcat.
