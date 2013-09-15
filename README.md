# MessagePack for Java

An *alternative* implementation of [MessagePack](http://msgpack.org/) for Java.

It is based on the [original Java implementation](https://github.com/msgpack/msgpack-java)

The primary goals of this alternative implementation are to keep the original tightness of MessagePack, while serving additionally the following use cases:

* Enable long-term persistence of data, under the assumption that the full schema (but not the original code) will still be available in the future.
* Enable easy on-the-fly data-migration, when reading data using an old schema.
* Enable partial/incremental de-serialisation.
* Enable serializing *changes* to an object graph, rather the the whole graph itself.
* Enable each type to choose a different MessagePack representation (array/map/raw)
* Enable embedding of other serialisation streams.
* Enable cycles in the object graph.
* Enable serialisation of immutable objects (might not work together with "cycles")
* Enable compatibility with GWT and Hadoop
* Enamble compatibility with HPPC collections (more third-party APIs compatibility to come)

One limitation of the core MessagePack API that causes some inefficiencies is the fact that MessagePack does not nativelly support true streaming of data. What I mean is that data can put into raw, arrays or maps, but all of them require the API user to know how many bytes or values will be written a priory, which goes against the principle of "streaming". There is currently work to make MessagePack an official Internet standard, and as part of this effort, there is work to make the API extensible. This, I hope, will allow use to create raw/arrays/maps of unknown size.

There are generally a few patterns to serializing objects. They can either be written as a bunch of bytes (raw), where the API cannot recognize the actual content of the bytes, and the code deserializing the data must have the full knowledge. The second possibility is that each object's filed is written as a "value" that the API recognize, and so objects are always written in the same format, independent of the content. The third option is for the object to only write it's "non-default" fields to the stream. The last option requires that at least some field ID precedes each field data, so that the object can be reconstructed when de-serializing.

Most serialisation API require force you to use a single one of those patterns for each and every type you want to persist. This API lets you choose, although MessagePack itself puts limitation on "raw" objects, which currently requires wrapping them in an array first, therefore causing one additional byte of overhead per object.

The general design of the object protocol works like this:

* All objects are written and read using an object-specific template.
* At the moment, all templates must be creted manually.
* The (optional) separation of object creation and obect reading allows both for the usage of immutable types and cycles, but both might not work together.
* The ObjectPacker and ObjectUnpacker are hardly more then wrappers for the templates.
* If the object is null, the you write nil
* If the object is a string, the you write a raw
* If the objects wants to be saved as an array, you open the array at size ("object size" + 1), and store the object's type as an integer ID in the first array position.
* If the objects wants to be saved as a map, you open the map at size ("object size" + 1), and store the object's type as an integer ID in the first map entry key (first map entry value might optionally be used by the object's template).
* If the objects wants to be saved as a raw (and is not a string), you create an array at size 2, and store the object's type as an integer ID in the first array position, and the raw at the second position.
* Under certain circumstances, arrays of objects might be able to skip the individual array and type header overhead.
* Being compatable with Java's DataOutput/DataInput interfaces bot for the underlying stream and the MessagePack core API should maximize this API's compatibility to existing code.

## Installation

To build the JAR file of MessagePack, you need to install Maven (http://maven.apache.org), then type the following command:

    $ mvn package

To locally install the project, type

    $ mvn install

To generate project files (.project, .classpath) for Eclipse, do

    $ mvn eclipse:eclipse

then import the folder from your Eclipse.

Next, open the preference page in Eclipse and add the CLASSPATH variable:

    M2_REPO = $HOME/.m2/repository

where $HOME is your home directory. In Windows XP, $HOME is:

    C:/Documents and Settings/(user name)/.m2/repository


## How to release

To relese the project (compile, test, tagging, deploy), please use the commands as follows:

    $ mvn release:prepare
    $ mvn release:perform


