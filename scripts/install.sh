#!/bin/bash
#This script will install all the requirement for you and start tomcat. REPOX will be available at
#localhost:8080/repox
#Usage(Should run under root user) : ./install.sh repoxDatabasePassword
#Install in a clean system without pre-existing tomcat 8 or postgres.
#Usage with own risk.

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
else
	apt-get update
	apt-get install -y software-properties-common
	add-apt-repository -y ppa:webupd8team/java
	apt-get update
	echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
	apt-get install -y oracle-java8-installer
	apt-get install -y tomcat8
	apt-get install -y git
	apt-get install -y maven
	apt-get install -y postgresql postgresql-contrib
	apt-get install -y sudo
	/etc/init.d/postgresql start
	sudo -H -u postgres bash -c "psql -c \"CREATE USER repox PASSWORD '$1'\""
	sudo -H -u postgres bash -c "psql -c \"CREATE DATABASE repox;\""
	sudo -H -u postgres bash -c "psql -c \"GRANT ALL PRIVILEGES ON DATABASE repox to repox;\""
	mkdir -p /data/git
	mkdir -p /data/repoxData
	cd /data/git
	git clone https://github.com/europeana/REPOX.git
	cd /data/git/REPOX
	git checkout development
	sed -i -e "s/^database.password = repox$/database.password = $1/" /data/git/REPOX/resources/src/main/resources/configuration.properties
	mvn clean install -Pproduction
	cp /data/git/REPOX/gui/target/repox.war /var/lib/tomcat8/webapps
	chown tomcat8 -R /data/repoxData
	/etc/init.d/tomcat8 restart
fi

exit 0;
