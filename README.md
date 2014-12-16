Venue Search App
================

A web application to search community venues in Bristol by name and postcode, built on MongoDB, Dropwizard, AngularJS and more.

Built and demonstrated live at MongoDB's Public Sector Masterclass in Bristol in November 2014 to show how open source 
technology, as well as open data, may be used to rapidly build digital services. 

Open Data
---------

The application uses [open community venue data](http://data.gov.uk/dataset/bristol-city-council-community-venues) from 
Bristol City Council and open postcode data from the Office of National Statistics Postcode Directory ([ONSPD](https://geoportal.statistics.gov.uk/geoportal/catalog/main/home.page)).
Please follow the links to download and read and understand the licences/terms of use.

Architecture
------------

The server application comprises a number of 'microservices' that expose RESTful API for venues and postcodes. An API gateway 
provides an interface to these services to the AngularJS-based frontend. 


Getting Started
---------------

Clone this Github repo:
```shell
$ git clone http://www.github.com/mattbates/venuehireapp
```

Using Maven, package up a 'fat' JAR. This will build the source code and download dependencies.
```shell
$ cd venuehireapp
$ mvn package
```

Start a local MongoDB server, using init/service commands, or manually at the shell:
```shell
$ mkdir –p data/db
$ mongod --dbpath=data/db --fork --logpath=mongod.log
```

Initially, the ONSPD will need to be downloaded from the [ONS Geography Portal](https://geoportal.statistics.gov.uk/geoportal/catalog/main/home.page)
and prepared. Firstly, unzip the package and use `mongoimport` to import the CSV into MongoDB.

```shell
$ mongoimport --type csv ONSPD_xxx_xxxx_csv/Data/ONSPD_xxx_xxxx_UK.csv --db demo --collection postcodes –-headerline
```

Using a Groovy script, Bristol postcodes are extracted into a separate collection and geocoded from OS grid references to latitude/longitude
points. Ensure an index exists on the `pcd` field.

```shell
$ mongo demo
> db.postcodes.ensureIndex({ "pcd " : 1})
> exit
$ groovy geocodeGridRefs.groovy
```

A Groovy script is now used to import the venues direct from the Bristol City Council XML web service. Using the Bristol postcode 
collection previously imoprted and geocoded, the venue documents are enriched with latitude/longitude point data. The script
creates the necessary text and geospatial indexes.

```shell
$ groovy importVenues.groovy
```

Now run the server using the JAR and the server YAML configuration:
```shell
$ java -jar target/venuehireapp-1.0-SNAPSHOT.jar server venue-service.yml
```

All being well, the web application should now be accessible at [http://localhost:8080/index.htm](http://localhost:8080/index.html). The RESTful API may also be accessed - e.g. all venues [http://localhost:8080/api/venue](http://localhost:8080/api/venue).

Disclaimer
----------

**Important note:** This application is example source code and should not be used for production purposes. MongoDB does not support or maintain the application.
