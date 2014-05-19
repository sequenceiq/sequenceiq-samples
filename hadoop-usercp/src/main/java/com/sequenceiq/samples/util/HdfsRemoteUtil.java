package com.sequenceiq.samples.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for copying and reading files from the hdfs
 * 
 */
public class HdfsRemoteUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HdfsRemoteUtil.class);

    /**
     * Uploads a file to the current user's home directory in the given folder..
     * 
     * @param configuration
     *            the hadoop configuration
     * @param localFileName
     *            the name of the local file to be uploaded
     * @param hdfsFilePathStr
     *            the absolute path for the file
     * @throws IOException
     */
    public static final void copyToHdfs(Configuration configuration, String localFileName, String hdfsFilePathStr) throws IOException {

        // http://paulscott.co.za/blog/uploading-files-through-java-api-to-hdfs-the-easy-way/
        DFSClient client = new DFSClient(NameNode.getAddress(configuration), configuration);
        OutputStream out = null;
        InputStream in = null;
        try {
            if (client.exists(hdfsFilePathStr)) {
                LOGGER.info("File already exists in hdfs: {}", hdfsFilePathStr);
                return;
            }
            out = new BufferedOutputStream(client.create(hdfsFilePathStr, true));
            in = HdfsRemoteUtil.class.getClassLoader().getResourceAsStream(localFileName);
            byte[] buffer = new byte[1024];

            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (client != null) {
                client.close();
            }
        }
    }

    public static final List<String> readLines(Configuration configuration, Path location) throws Exception {
        // http://blog.matthewrathbone.com/2013/12/28/Reading-data-from-HDFS-even-if-it-is-compressed.html
        FileSystem fileSystem = FileSystem.get(location.toUri(), configuration);
        CompressionCodecFactory factory = new CompressionCodecFactory(configuration);
        FileStatus[] items = fileSystem.listStatus(location);
        if (items == null)
            return new ArrayList<String>();
        List<String> results = new ArrayList<String>();
        for (FileStatus item : items) {
            // ignoring files like _SUCCESS
            if (item.getPath().getName().startsWith("_")) {
                continue;
            }
            CompressionCodec codec = factory.getCodec(item.getPath());
            InputStream stream = (codec != null) ?
                    codec.createInputStream(fileSystem.open(item.getPath()))
                    :
                    fileSystem.open(item.getPath());

            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer, "UTF-8");

            for (String str : writer.toString().split("\n")) {
                results.add(str);
            }
        }
        return results;
    }
}
