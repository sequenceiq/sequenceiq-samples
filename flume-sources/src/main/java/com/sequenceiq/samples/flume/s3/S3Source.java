package com.sequenceiq.samples.flume.s3;

import static org.apache.flume.conf.Configurables.ensureRequiredNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.flume.Context;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractEventDrivenSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Source extends AbstractEventDrivenSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3Source.class);
    private static final String ACCESS_KEY_KEY = "accessKey";
    private static final String SECRET_KEY = "secretKey";
    private static final String BUCKET_KEY = "bucket";

    private String accessKey;
    private String secretKey;
    private String bucket;

    @Override
    protected void doConfigure(Context context) {
        ensureRequiredNonNull(context, ACCESS_KEY_KEY, SECRET_KEY, BUCKET_KEY);
        this.accessKey = context.getString(ACCESS_KEY_KEY);
        this.secretKey = context.getString(SECRET_KEY);
        this.bucket = context.getString(BUCKET_KEY);
    }

    @Override
    protected void doStart() {
        AWSCredentials myCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = new AmazonS3Client(myCredentials);
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucket);
        ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
        ChannelProcessor channelProcessor = getChannelProcessor();
        for (S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries()) {
            String file = s3ObjectSummary.getKey();
            LOGGER.info("Read the content of {}", file);
            GetObjectRequest objectRequest = new GetObjectRequest(bucket, file);
            S3Object objectPortion = s3Client.getObject(objectRequest);
            try {
                long startTime = System.currentTimeMillis();
                processLines(channelProcessor, objectPortion.getObjectContent());
                LOGGER.info("Processing of {} took {} ms", file, System.currentTimeMillis() - startTime);
            } catch (IOException e) {
                LOGGER.warn("Cannot process the {}, skipping", file, e);
            }
        }
    }

    @Override
    protected void doStop() {
    }

    private void processLines(ChannelProcessor channelProcessor, InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = reader.readLine()) != null) {
            channelProcessor.processEvent(createEvent(line));
        }
        reader.close();
    }

    private SimpleEvent createEvent(String message) {
        SimpleEvent event = new SimpleEvent();
        event.setBody(message.getBytes());
        return event;
    }

}
