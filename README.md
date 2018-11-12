# FTL
Format Transform Layer: A DSL for easy data transforms.

## Abstract

When moving data it's often necessary to transform the data from one format to another, or to simply map a formatted file to an in memory structure.  This can be converting or restructuring serial formats such as between XML, JSON, CSV, and Protobuf.  It could also mean simply remapping dictionaries of data.  This process tends to be unwieldy and requires many lines of code.  Consider the following procedure.

1. Deserializing the input format
2. Retrieving individual fields from the deserialized document
3. Mapping those fields to a native object
4. Reserializing the native object to the new format

The purpose of the FTL standard is to provide easily readable, declarative, reading and transformation of data.  FTL is a terse DSL that defines both how to extract data from serialized formats and how to write data back to the same formats.  Core FTL does not understand the nuances of individual file formats and an FTL definition should generally work to serialize or deserialize data to any of the implemented formats.

## The FTL Standard
Version 0.1

Data leaving this system can be easily grouped into two structures: value tuples, and key-value maps.  In either format each line represents one field that will be output from the transformation.

### Key-Value Map Definition

Key-Value Maps MUST be defined with two parts separated by a colon.  

* An alphanumeric string before a colon will be a key that will idenitify the value in memory.
  * These keys SHOULD not start with a number.
  * These keys SHOULD not have white space or extended characters.
* Following the colon there should be a single space and an FTL Selector.

```
id: id
title: title
descriptions: descriptions/description
premiere: year
genres: genres/genre
```

### Value Tuple Definition

The leading key (each line up to and including the colon) may be left out if the desired outcome is a value tuple, list, or array.  Having no declared keys to identify any field, retrieved fields will be returned as an ordered collection.  

Inferring a default key from the selectors may be possible, but is not addressed in this version of this document.

```
id
title
descriptions/description
year
genres/genre*
cast:
  cast/member:
    full_name: "$first $last"
```


### FTL Selectors

FTL Selectors are modeled closely to `xpath` selectors, but they are expressing a mapping to a range of different formats.  The implementation of selectors will vary by whatever is obvious in each format.  Guidance will be given by FTL on what special selectors mean for all formats.

#### XML Selectors

XML Elements and their attributes are referenced by FTL Selectors.  These elements and attributes are separated by a forward slash.

Consider the above `Key-Value Map Definition`.  It can be used to both define this document and it also could be used to fetch values from it.

```
<movie id="123">
  <title>Heat</title>
  <year>1995</year>
  <descriptions>
    <description lang="en">A great movie.</description>
    <description lang="es">Una gran película</description>
  </descriptions>
  <genres>
    <genre>Action</genre>
    <genre>Crime</genre>
  </genres>
  <cast>
    <member>
      <first>Richard</first>
      <middle>Dean</middle>
      <last>Anderson</last>
    </member>
</movie>
```

The FTL definition could be used to fetch these values:

```
id: 123
title: Heat
descriptions: "A great Movie"
premiere: 1995
genres: ["Action", "Crime"]
cast: ["Richard Anderson"]
```

The same KV Definition can be used to render another XML document.  In this case, it would render an XML document, but may not necessarily render the same XML document.

##### Array Responses

Due to the nature of XML, multiple elements could match, such as in the `genres/genre` case.  Selectors are written with
the assumption that a single value is more often desirable to be returned, but a multi-value response is possible.  If you would like all matching 
elements to be returned, the last character of the selector should be an `*`.  Note, a `/` should not preceed the `*`. 

#### JSON Selectors

Futhermore the same KV Definition could be used with a different format, such as JSON.

By using the fetched values above, and the same KV definition we would render the following JSON:

```
{
  "id": 123,
  "title": "Heat",
  "descriptions": ["A great Movie", "Una gran película"],
  "premiere": 1995,
  "genres": ["Action", "Crime"]
}
```

### Cascading Selectors

Much of the volume of mapping code comes from the business logic to select values.  Default values and cascading selection of fields that may or may not be present lead to unweildy conditional trees.  FTL should provide tools to fall back on values when fields are missing.

```

title: title|name
description: descriptions/description[lang=en,es]
```

### ENUM Maps

### HashMap Values

You may want to extract more complicated values out of your documents.  You may construct sub-HashMaps

```$xslt
sub_mapping:
    - name: value

```

## Comparison

### Serializing XML in Java

If we look at rendering the same XML dynamically in Java, we find that it takes minimally 3x as much code.
```
Element root=new Element("movie");
Document doc=new Document();

Element title=new Element("title");
title.addContent("Heat");
root.addContent(title);

... repeat for each element ...

doc.setRootElement(root);
```
