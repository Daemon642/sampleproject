 Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.

Welcome to the Oracle Storage Cloud Service Java SDK!

The Oracle Storage Cloud Service enables customers to securely and
reliably store and retrieve files and unstructured data over the
Internet.

This SDK includes the following:
  - Oracle Storage Cloud Service Java library
  - Javadocs for the Java library

The Oracle Storage Cloud Service Java library provides a convenient
way to interact with Oracle Storage Cloud Service instances through
the Java programming language. The Java library can be used from both
on-premise environments as well as applications running in Oracle Java
Cloud Service. The Java library provides the following functionality:
  - Store and Retrieve Objects
    - Optionally encrypt / decrypt Objects transparently on upload and
      download, respectively. 
    - Store segmented Objects (for source files larger than
      5GB). Note: This cannot be used with transparent encryption.
  - Create and Delete Containers and Objects
  - Update Container Access Control Lists (ACLs)
  - Update Container and Object metadata
  - List Containers and Objects

The Oracle Storage Cloud Service Java library has the following
run-time dependencies:
  - jersey-bundle-1.13.jar
  - jersey-multipart-1.13.jar
  - mimepull-1.9.3.jar
