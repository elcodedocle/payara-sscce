# Payara jax-rs SSCCE

This demo project deploys a simple Hello World *Jakarta 11* jax-rs war on the [payara version specified on pom.xml](pom.xml)
on a dockerhub payara full container, then tests the app is deployed correctly without any SEVERE log traces.

## Requirements

 - Maven (Tested on 3) with the specified dependencies and plugins (See [pom.xml](pom.xml))
 - Java (Tested on OpenJDK 17 JDK / OpenJDK 21 JRE)
 - Docker (Tested on Docker engine version 20.10.21)

## How to use

 Run `mvn clean verify`
 
