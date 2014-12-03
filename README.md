#REPOX#

**CAUTION!**  
**The information below is subject to change**

###REPOX - Data Aggregation and Interoperability Manager ###
- - - 
REPOX is a framework to manage data spaces. It comprises several channels to import data from data providers, services to transform data between schemas according to user's specified rules, and services to expose the results to the exterior. This tailored version of REPOX aims to provide to all the TEL and Europeana partners a simple solution to import, convert and expose their bibliographic data via OAI-PMH, by the following means:

* __Cross platform__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;It is developed in Java, so it can be deployed in any operating system that has an available Java virtual machine.

* __Easy deployment__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;It is available with an easy installer, which includes all the required software.

* __Support for several data formats and encodings__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;It supports UNIMARC and MARC21 schemas, and encodings in ISO 2709 (including several variants), MarcXchange or MARCXML. During the course of the TELplus project, support will be added for other possible encodings required by the partners.

* __Data crosswalks__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;It offers crosswalks for converting UNIMARC and MARC21 records to simple Dublin Core as also to TEL-AP (TEL Application Profile). A simple user interface makes it possible to customize these crosswalks, and create new ones for other formats.

Original repox site:  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Repox original website](http://repox.ist.utl.pt/ "Repox original website")

Repox github site:  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Repox github development](https://github.com/europeana/repox2 "Repox github development")

###Repox Structure Overview###
- - - 
Repox consists of following projects:

* __Repox Parent (repox-system)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This project is basically the parent project consisting of the modules repox-gui, repox-manager, repox-commons, repox-server(repox-server-client, repox-server-oai), repox-resources
* __Repox Gui (repox-gui)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The GUI implementation using GWT. This is the main war build that should be deployed in a servlet container.
* __Repox Manager (repox-manager)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; As the name of the module describes it is the core manager of the repox features. 
* __Repox Commons (repox-commons)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Contains common classes for the rest of the modules
* __Repox Resources (repox-resources)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This is the module for centralization of common resources.
* __Repox Server (repox-server)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The repox-server module is another parent project that contains the following modules repox-server-rest, repox-server-rest-jersey, repox-server-oai.
* __Repox Server Rest (repox-server-rest)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This is the API Restful service for accessing Repox functionality. This module can be build and deployed independently from the repox-gui.
* __Repox Server Rest Jersey(repox-server-rest-jersey)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The new RESTful API service, using Jersey 2 framework for accessing Repox functionality. This module can be build and deployed independently from the repox-gui.
* __Repox Server OAI (repox-server-oai)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; An OAI-PMH endpoint for accessing information of Repox datasets. This module can be build and deployed independently from the repox-gui.


###Building Repox###
- - -
Repox is a maven project and can be easily build by building the root project where the parent pom is.
The command to build is:

&nbsp;&nbsp;&nbsp;&nbsp; `clean install -Pproduction`  
(Tests are skipped, and can be activated by configuring the maven pom's)

####Profiles####

1. `production`

    This is the profile that should be used when everything is in place and the resources of the `repox-resources` project are updated.  
    Always run this profile when a project is ready to run outside of an IDE.  
    <b>!Caution:</b> Deletes specific resources that are bound in the `src/main/resources` and then generates the build. The resources that are being deleted are resources that were previously generated on demand when there is the need to run a module as a standalone or in an IDE(e.g. testing the REST API module).

2. `copy-resources`

    Maven profile that generates copies of specific configuration files from the repox-resources project to the maven `src/main/resources`.  
    &nbsp;&nbsp;&nbsp;&nbsp; `process-resources -Pcopy-resources`  
    This profile is bound to the `process-resources` phase of maven so its sufficient to run the maven build until this phase to get the resources. The resources that are being generated are resources that are required on demand when there is the need to run a module as a standalone or in an IDE(e.g. testing the REST API module).  
    After copying the `copy-resources` profile is run then a normal build without a profile can be run `clean package`.

3. `generate-doc`

    Repox uses swagger 2.0 for documenting the REST API of `repox-server-rest-jersey`. 
    This profile is bound to the `generate-resources`.  
    &nbsp;&nbsp;&nbsp;&nbsp; `generate-resources -Pgenerate-doc`  
    It uses Server integration that dynamically provides JSON at runtime. If in any case the json needs to be generated in files, this profile can be used to generated them and the applicable url and document locations can be editted in the `pom.xml` of the module.
    

Something to mention is that the application will read the `configuration.properties` file that is located in the configurations directory(if it exists) and will ignore the `configuration.properties` located in the WEB-INF/classes directory.

For development builds the permutations have been reduced in the file `/repox-gui/src/main/java/harvesterUI/HarvesterUI.gwt.xml`.  
For other configuration needed, this file needs to be editted.

###Deploying Repox###
- - -
For Repox deployment there is a need of a PostgreSQL or MySQL database server and a servlet container as tomcat.  
Tested versions are:  
&nbsp;&nbsp;&nbsp;&nbsp; <b>MySQL</b> 5.5.40, 5.6.20  
&nbsp;&nbsp;&nbsp;&nbsp; <b>PostgreSQL</b> 9.1.14, 9.3.5  
&nbsp;&nbsp;&nbsp;&nbsp; <b>Tomcat</b> 6.0.41, 7.0.55, 8.0.12  

Additionally a database schema with a username with password that can access and modidy it, is needed in the SQL server.
These information must be added in the `configuration.properties` file mentioned above.  

After the above software is configured the .war file, created by building the <b>repox-system</b> and located under `/repox-gui/target`, can be moved in the `webapps` directory of tomcat.
