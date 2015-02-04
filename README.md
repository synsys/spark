# Apache Spark Extensions

A multi-module Maven project. Each module extends the core functionality of [Apache Spark](http://spark.apache.org) as described below.

## Modules
 Overview | groupId  | artifactId | packaging
 -------- | -------- | ---------- | ---------
Parent module | com.synsys.org.apache.spark | spark | pom
A Spark Streaming module that ingests data from an [Apache Camel](http://camel.apache.org/) component. Any of the many Apache Camel [components](http://camel.apache.org/components.html) supporting message consumption can be used |com.synsys.org.apache.spark | spark-streaming-camel_2.10 | jar

## Building

Builds require [maven](http://maven.apache.org/) version 3.1.1 or higher.

From a command line run:
```
cd /project/root
mvn install
```

## Releases

Released artifacts are deployed to [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ccom.synsys.org.apache.spark) 
