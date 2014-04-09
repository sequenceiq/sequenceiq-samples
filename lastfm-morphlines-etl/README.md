Last.fm data processing with Kite Morphlines
==============================================

At [SequenceIQ](http://sequenceiq.com) we have a platform and an API which consumes and ingests various types of data from different sources to offer predictive analytics and actionable insights.
The datasets we work with are structured, unstructured, log files and communication records and require constant refining, cleaning and transformation. Since the datasets we load into Hadoop are coming from different sources (industry standard and proprietary adapters, Apache Flume, MQTT, iBeacon, etc.)
we needed a flexible and embeddable framework to support our ETL process chain - welcome [Kite Morphline](https://github.com/kite-sdk/kite/tree/master/kite-morphlines).

Originally the Morphlines library was developed as part of [Cloudera Search](http://www.cloudera.com/content/cloudera/en/products-and-services/cdh/search.html) but eventually graduated into the [Kite SDK](http://kitesdk.org/docs/current/).
To define a `morphline` transformation chain you need to describe the steps in a configuration file, and the framework will turn into an in-memory container of transformation commands.
Commands perform tasks as transforming, loading, parsing and processing records, and they can be linked in a processing chain.

In this blog post we'd like to show an ETL process chain with custom Morphlines commands (defining Morphlines commands using the config file and Java), and use the framework within MapReduce jobs and Apache Flume.
For the sample ETL with Morphlines use case we have picked a publicly available 'million song' [last.fm](http://labrosa.ee.columbia.edu/millionsong/lastfm) dataset.
The raw data consist of one JSON file/entry for each track - the dictionary contains the following keywords: **artist**, **timestamp**, **similars**, **tags**, **track_id** and **title**.
The keys are not always available for each objects.

``` JSON
{
    "artist": "Topazz",
    "timestamp": "2011-08-01 15:42:24.789597",
    "similars": [
        [
            "TRLTTOC128F4238C01",
            0.522931
        ],
        [
            "TRBFOXE128F4238C18",
            0.499313
        ]
    ],
    "tags": [
        [
            "drjazzmrfunkmusic",
            "100"
        ],
        [
            "mid",
            "75"
        ]
    ],
    "track_id": "TRAAEYW128F4238BF3",
    "title": "Behind the Wheel"
}
```

During the ETL steps we will use two process chains with built in and custom commands (we will show two different ways of writing custom commands).
![](https://raw.githubusercontent.com/sequenceiq/sequenceiq-samples/master/lastfm-morphlines-etl/img/sequenceiq4cloudera-fig1.png)

####Import data into a Hadoop cluster

The initial data import flow from an external source into Hadoop/HDFS is the following:

![](https://raw.githubusercontent.com/sequenceiq/sequenceiq-samples/master/lastfm-morphlines-etl/img/sequenceiq4cloudera-fig2.png)

During the import phase we set up an Apache Flume Interceptor with a Morphlines configuration file to load data into HDFS. 
The load process uses a custom Morphlines Java command
to do a preliminary ETL process on the data *(selecting songs before and after a given date)*. For this we have written a simple custom Java command:


The custom [LatestSongCommand](https://github.com/sequenceiq/sequenceiq-samples/blob/master/lastfm-morphlines-etl/src/main/java/com/sequenceiq/lastfm/etl/LatestSongCommand.java) Morphlines command implementation looks like this:

``` java
@Override
protected boolean doProcess(Record record) {
    Map<String, String> attachmentBody = (Map<String, String>) record.get(Fields.ATTACHMENT_BODY).get(0);
    String fieldValue = attachmentBody.get(fieldName).toString();

    try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date fieldDate = sdf.parse(fieldValue);
        Date patternDate = sdf.parse(pattern + " 00:00:00");

        if (operator.equals(HIGHER)) {
            if (!fieldDate.after(patternDate)) {
                return true;
            }
        } else if (operator.equals(LOWER)) {
            if (!fieldDate.before(patternDate)) {
                return true;
            }
        } else if (operator.equals(EQUALS)) {
            if (fieldDate.getYear() != patternDate.getYear()
                    || fieldDate.getMonth() != patternDate.getMonth()
                    || fieldDate.getDay() != patternDate.getDay()) {
                return true;
            }
        } else {
            LOG.info("bad operator syntax");
        }
    } catch (Exception e) {
        LOG.info("parse exception: " + e.getMessage());
        return false;
    }
    record.removeAll(Fields.ATTACHMENT_BODY);
    try {
        record.put(Fields.MESSAGE, OBJECTMAPPER.writeValueAsString(attachmentBody));
    } catch (JsonProcessingException e) {
        LOG.info("parse exception: " + e.getMessage());
        return false;
    }
    return super.doProcess(record);
}
```

To configure your new Morphline command:

``` JSON
    morphlines : [
    {
        id : morphline1
        importCommands : ["org.kitesdk.**", "com.sequenceiq.lastfm.etl.**"]
        commands : [
            {
                readJson {
                    outputClass : java.util.Map
                }
            }
            {
                latestSongs {
                    field : timestamp
                    operator: >
                    pattern: "2011-08-03"
                }
            }
        ]
    }]
```
As the data is coming through the Flume agent, the Morphlines commands are applied to the records and the Flume sink will receive the cleaned data.

*As a quick remark we'd like to note that when using Morphlines with the Flume HdfsSink configuring a custom data serializer for the HdfsSink is often handy, as Flume by default persists the body, not the headers. For your convenience we have written a Flume serializer for Morphlines: [CustomLastfmHeaderAndBodyTextEventSerializer](https://github.com/sequenceiq/sequenceiq-samples/blob/master/lastfm-morphlines-etl/src/main/java/com/sequenceiq/lastfm/etl/CustomLastfmHeaderAndBodyTextEventSerializer.java). This will retain the same input data format, with the ETL commands applied.*

####Post process the entries

Once the data is imported into HDFS we can post process it and apply additional ETL steps. The flow we chose in this example is the following:

![](https://raw.githubusercontent.com/sequenceiq/sequenceiq-samples/master/lastfm-morphlines-etl/img/sequenceiq4cloudera-fig3.png)

The post processing ETL uses a chain of Morphlines commands that are set up in a configuration file. Each track in the last.fm dataset has similar tracks associated. The associated tracks are the results of precomputed song-level similarities. The custom  Morphlines command allows to select only the entries where the similarity is less, equal or over than the filter value (in our case over 0.1).

```JSON
morphlines : [
  {
    id : morphline1
    importCommands : ["org.kitesdk.**"]
    commands : [
      {
        readJson {
          outputClass : com.fasterxml.jackson.databind.JsonNode
        }
      }
      {
        extractJsonPaths {
          flatten : false
          paths : {
            similars : "/similars/[]"
          }
        }
      }
      {
        java {
          imports : """
            import java.util.*;
            import com.fasterxml.jackson.databind.*;
            import com.fasterxml.jackson.databind.node.*;
          """
          code: """
            List<ArrayNode> similars = record.get("similars");
            for (ArrayNode similar : similars) {
                Iterator<JsonNode> iterator = similar.elements();
                while (iterator.hasNext()) {
                    JsonNode pair = iterator.next();
                    JsonNode jsonNode = pair.get(1);
                    //we remove entries with similarity < 0.1
                    if (jsonNode == null || jsonNode.asDouble() < 0.1) {
                        iterator.remove();
                    }
                }
            }
            return child.process(record);
          """
        }
      }
    ]
  }
]
```

In this Morphline config file we have used two default *(readJson, extractJsonPaths)* and one custom command *(written in Java)*. As you can see, custom Morphlines commands can be defined in a command file as well, you don't have to compile them or write a Java class beforehand.
This is an extremely useful feature of Morphlines - using the [JavaBuilder](https://github.com/kite-sdk/kite/blob/master/kite-morphlines/kite-morphlines-core/src/main/java/org/kitesdk/morphline/stdlib/JavaBuilder.java) class the framework compiles the commands at runtime.


To use Morphlines from a MapReduce job is pretty straightforward. During the setup phase of the MapReduce job you build a context, a `morphline`, and that's it.

```java
 @Override
  protected void setup(Context context) throws IOException, InterruptedException {
      File morphLineFile = new File(context.getConfiguration().get(MORPHLINE_FILE));
      String morphLineId = context.getConfiguration().get(MORPHLINE_ID);
      RecordEmitter recordEmitter = new RecordEmitter(context);
      MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
      morphline = new org.kitesdk.morphline.base.Compiler()
              .compile(morphLineFile, morphLineId, morphlineContext, recordEmitter);
  }

```
Also we have created a [RecordEmitter](https://github.com/sequenceiq/sequenceiq-samples/blob/master/lastfm-morphlines-etl/src/main/java/com/sequenceiq/lastfm/etl/MapperCleaner.java) ```java private static final class RecordEmitter implements Command``` inner class in the mapper. The org.kitesdk.morphline.base.Compiler class `public Command compile(File morphlineFile, String morphlineId, MorphlineContext morphlineContext, Command finalChild, Config... overrides)` method takes an important parameter called `finalChild` which is in our example the RecordEmitter. The returned command will feed records into finalChild which means if this parameter is not provided a DropRecord command will be assigned automatically. *(In Flume there is a Collector command to avoid loosing any transformed record)*. 
The only thing left is to outbox the processed record and write the results to HDFS. The RecordEmitter will serve this purpose writing out to HDFS: `context.write(line, null)`.

Once the `morphline` is created, you can now process the records.

``` java
public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
    record.put(Fields.ATTACHMENT_BODY, new ByteArrayInputStream(value.toString().getBytes()));
    if (!morphline.process(record)) {
        LOGGER.info("Morphline failed to process record: {}", record);
    }
    record.removeAll(Fields.ATTACHMENT_BODY);
  }
```

####Testing

Kite Morphlines has a nice test framework built in the SDK - your unit tests can extend the abstract [AbstractMorphlineTest](https://github.com/kite-sdk/kite/blob/master/kite-morphlines/kite-morphlines-core/src/test/java/org/kitesdk/morphline/api/AbstractMorphlineTest.java) class,
thus you can test your custom built commands the same way as the Kite Morphlines does for the built-in ones. You can check our [LatestSongCommandTest](https://github.com/sequenceiq/sequenceiq-samples/blob/master/lastfm-morphlines-etl/src/test/java/com/sequenceiq/lastfm/etl/LatestSongCommandTest.java) test case for a reference.

####Building and running

You can get the code from our [GitHub](https://github.com/sequenceiq/sequenceiq-samples/tree/master/lastfm-morphlines-etl) page and build the project with Maven `mvn clean install`.
Download the Last.fm sample dataset from [S3](https://s3-eu-west-1.amazonaws.com/lastfm-ratings/lastfm_ratings.json) and save it to your computer *(alternatively you can use our other Morphlines [example](http://blog.sequenceiq.com/blog/2014/03/11/data-cleaning-with-mapreduce-and-morphlines/) to process/ETL files directly from an S3 bucket)*.
Start Flume using the following [configuration](https://github.com/sequenceiq/sequenceiq-samples/blob/master/lastfm-morphlines-etl/src/main/resources/flume.conf) - make sure you change the input and output folders accordingly.
Once the data is processed and available on HDFS, you can run the second ETL process, this time using Morhplines from a MapReduce job `yarn jar lastfm-morphlines-etl-1.0.jar com.sequenceiq.lastfm.etl.MapperCleaner input output morphline-file morphlineId` - make sure you change the arguments.

As you can see, embedding Morphlines in your application and using it is very easy - the increasing number of built-in commands usually satisfy most of the needs, but the framework offers flexible ways to write custom commands as well. 

Happy Morphlining!
