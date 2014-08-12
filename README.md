#REPOX#

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
Repox consists of 4 maven projects:

* __Repox Parent (repox-system)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This project is basically the parent project consisting of the repox-gui, repox-europeana, repox-core.
* __Repox Gui (repox-gui)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The GUI implementation using GWT. It also includes the RESTful services, using HttpServlet.
* __Repox Europeana (repox-europeana)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Contains classes for specific implementation of the services for europeana that should be used from the RESTful services in the repox-gui.
* __Repox Core (repox-core)__  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Contains the core classes and interfaces for use by other projects. It also contains the OAI-PMH HttpServlet implementation.

###Building Repox###
- - -
Repox is a maven project and can be easily build by building the root project where the parent pom is.
The command to build is:

&nbsp;&nbsp;&nbsp;&nbsp; `clean install -Peuropeana -Dmaven.test.skip=true` (since the tests are outdated)

The europeana profile is used for the inclusion of the `configuration.properties` and `gui.properties` as resources in the build.  
These 2 files are located under the directory `/repox-gui/src/main/profiles/europeana`.
The `configuration.properties` file contains configurations for storage directories that Repox will be using as well as PostgreSQL connection settings.  
Changes can be made to the `configuration.properties` file to fit the needs of the developer.
Something to mention is that the application will read the `configuration.properties` file that is located in the configurations directory and will ignore the `configuration.properties` located in the WEB-INF/classes directory.

For development builds the permutations have been reduced in the file `/repox-gui/src/main/java/harvesterUI/HarvesterUI.gwt.xml` and for the end product they need to be uncommented again.

###Deploying Repox###
- - -
For Repox deployment there is a need of a PostgreSQL database and a servlet container as tomcat.  
Tested versions are:  
&nbsp;&nbsp;&nbsp;&nbsp; PostgreSQL 9.1.14  
&nbsp;&nbsp;&nbsp;&nbsp; Tomcat 6.0.41  

Additionally a database with a username with password is needed in the PostgreSQL.  
These information must be added in the `configuration.properties` file mentioned above.  

After the above software is configured the .war file, created by building the <b>repox-parent</b> and located under `/repox-gui/target`, can be moved in the `webapps` directory of tomcat.