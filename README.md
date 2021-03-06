# jndn-utils

This project is a collection of tools to simplify synchronous and asynchronous data transfer over the NDN network. It relies on the [NDN Protocol](https://named-data.net) and its associated [client library](https://github.com/named-data/jndn).

## Install
With Maven, add the following to your POM:
```
<dependency>
  <groupId>com.intel.jndn.utils</groupId>
  <artifactId>jndn-utils</artifactId>
  <version>RELEASE</version> <!-- or a specific version -->
</dependency>
```

## Use
Use `SimpleClient` or `SegmentedClient` to retrieve data from the network. For example:
```
// retrieve a single Data packet synchronously, will block until complete
Data singleData = Client.getDefault().getSync(face, name);

// retrieve a segmented Data packet (i.e. with a last Component containing a segment number and a valid FinalBlockId) by name
Data segmentedData = SegmentedClient.getDefault().getSync(face, name);
```

Use `SimpleServer` or `SegmentedServer` to serve data on the network. For example:
```
// segment and serve Data packet under a specific prefix
RepositoryServer server = new SegmentedServer(face, prefix);
server.serve(largeDataPacket); // call face.processEvents() in an event loop

// add signatures; this pipeline stage will sign each Data packet prior to being encoded for transport
server.addPipelineStage(new SigningStage(keyChain, signingCertificateName));
```

For full API, see the [Javadoc](http://01org.github.io/jndn-utils/).

## License
Copyright � 2015, Intel Corporation.

This program is free software; you can redistribute it and/or modify it under the terms and conditions of the GNU Lesser General Public License, version 3, as published by the Free Software Foundation.

This program is distributed in the hope it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the [GNU Lesser General Public License](https://github.com/01org/jndn-utils/blob/master/LICENSE) for more details.
