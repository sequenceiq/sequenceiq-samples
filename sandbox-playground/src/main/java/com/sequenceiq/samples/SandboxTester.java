package com.sequenceiq.samples;

import java.io.IOException;
import java.security.PrivilegedAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;

public class SandboxTester {
	private static final Logger LOG = LoggerFactory.getLogger(SandboxTester.class);

	public static void main(String... args) throws IOException {

		if (null == args || args.length == 0) {
			throw new IllegalArgumentException("Add the sandbox user, filename and content as argument");
		}

		String sandBoxUser = args[0];
		final String hdfsFileName = args[1];
		final String content = args[2];

		UserGroupInformation.createRemoteUser(sandBoxUser).doAs(new PrivilegedAction<Object>() {

			@Override
			public Object run() {
				try {
					LOG.info("Creating test file {} with the content {}", hdfsFileName, content);
					// configuration taken from the xmls in the resources
					// folder!
					Configuration configuration = new Configuration();

					// creates a file with the given content on the sandbox'
					// hdfs
					SandboxTester.testHdfs(configuration, hdfsFileName, content);
					LOG.info("Successfully created the test file. Proceeding to cleanup ...");

					// clean up
					SandboxTester.delete(configuration, hdfsFileName);
				} catch (Exception e) {
					LOG.error("Ex ", e);
				}
				return null;
			}
		});

	}

	private static void delete(Configuration configuration, String hdfsFileName) throws IOException {
		Path hdfsFile = new Path(hdfsFileName);
		FileSystem fs = FileSystem.get(hdfsFile.toUri(), configuration);
		if (fs.exists(hdfsFile)) {
			LOG.info("Deleting {}", hdfsFile);
			fs.delete(hdfsFile, true);
		}

	}

	private static final void testHdfs(Configuration configuration, String fileName, String content) throws IOException {

		Path hdfsFile = new Path(fileName);

		FileSystem fs = FileSystem.get(hdfsFile.toUri(), configuration);
		FSDataOutputStream out = fs.create(hdfsFile);
		try {
			out.writeChars(content);
		} finally {
			Closeables.close(out, false);
		}
	}
}
