package com.sequenceiq.samples;

import java.io.IOException;
import java.security.PrivilegedAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.samples.job.SampleMapper;
import com.sequenceiq.samples.util.HdfsRemoteUtil;

public class CpTesterJobClient {

    private static final String MAPREDUCE_JOB_USER_CLASSPATH_FIRST = "mapreduce.job.user.classpath.first";
    private static final String TEST_TXT = "test.txt";
    private static final Logger LOGGER = LoggerFactory.getLogger(CpTesterJobClient.class);
    private static final String SAMPLE_JOB_USER_CLASSPATH = "Sample job - User classpath";
    private static final String INPUT_DIR = "in";
    private static final String OUTPUT_DIR = "out";

    /**
     * Submits a job to a cluster to test the behavior of the
     * "mapreduce.job.user.classpath.first" configuration parameter. The job
     * resources (Mappers/Reducers etc ) are packaged along with all the
     * dependencies.
     * 
     * @param userCpFirst
     *            controls the value of the mapreduce.job.user.classpath.first
     *            configuration entry
     * @return the path to the jar the class is taken from
     * @throws Exception
     *             if any problem occurs
     */
    public String withShadedJar(boolean userCpFirst) throws Exception {
        String retStr = null;
        LOGGER.info("Assembling sample job for testing the user classpath...");
        try {
            Configuration config = getConfiguration();
            userClassPathFirst(config, userCpFirst);
            cleanup(config, new Path(INPUT_DIR));
            cleanup(config, new Path(OUTPUT_DIR));

            Job job = Job.getInstance(config);
            job.setJobName(SAMPLE_JOB_USER_CLASSPATH);

            ensureInput(TEST_TXT);
            FileInputFormat.addInputPath(job, new Path(INPUT_DIR));
            FileOutputFormat.setOutputPath(job, new Path(OUTPUT_DIR));

            job.setMapperClass(SampleMapper.class);
            job.setJarByClass(SampleMapper.class);

            if (job.waitForCompletion(true)) {
                retStr = HdfsRemoteUtil.readLines(config, new Path(OUTPUT_DIR)).toString();
            }
        } catch (Exception e) {
            LOGGER.error("Exception during Sample job", e);
            throw e;
        }

        return retStr;
    }

    public String withSimpleJar(boolean userCpFirst) throws Exception {
        String retStr = null;
        LOGGER.info("Assembling sample job for testing the user classpath...");
        try {
            Configuration config = getConfiguration();
            userClassPathFirst(config, userCpFirst);
            // addDirToCp(config, ".:hadoop-usercp-dependencies-0.1.tar/*:");
            cleanup(config, new Path(INPUT_DIR));
            cleanup(config, new Path(OUTPUT_DIR));

            Job job = Job.getInstance(config);
            job.setJobName(SAMPLE_JOB_USER_CLASSPATH);

            ensureInput(TEST_TXT);
            FileInputFormat.addInputPath(job, new Path(INPUT_DIR));
            FileOutputFormat.setOutputPath(job, new Path(OUTPUT_DIR));

            job.setMapperClass(SampleMapper.class);
            job.setJar("/Users/puski/prj/sequenceiq-samples/hadoop-usercp/build/libs/hadoop-usercp-plain-0.1.jar");
            //job.addArchiveToClassPath(new Path("cp/hadoop-usercp-dependencies-0.1.tar"));
            job.addFileToClassPath(new Path("cp/hadoop-usercp-dependencies-0.1.tar"));

            // job.addFileToClassPath(new
            // Path("cp/jackson-core-asl-1.9.13.jar"));

            if (job.waitForCompletion(true)) {
                retStr = HdfsRemoteUtil.readLines(config, new Path(OUTPUT_DIR)).toString();
            }
        } catch (Exception e) {
            LOGGER.error("Exception during Sample job", e);
            throw e;
        }

        return retStr;
    }

    private Configuration getConfiguration() {
        // Don't load the defaults (various xmls from classpath can cause
        // problems!)
        Configuration conf = new Configuration(false);

        conf.set("mapreduce.framework.name", "yarn");
        conf.set("yarn.resourcemanager.address", "sandbox:8032");

        conf.set("hadoop.socks.server", "127.0.0.1:1099");
        conf.set("hadoop.rpc.socket.factory.class.default", "org.apache.hadoop.net.SocksSocketFactory");

        conf.set("fs.default.name", "hdfs://sandbox:9000");
        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.AbstractFileSystem.hdfs.impl", "org.apache.hadoop.fs.Hdfs");

        conf.set("dfs.client.use.legacy.blockreader", "true");

        return conf;
    }

    public static void main(String[] args) {
        UserGroupInformation.createRemoteUser("root").doAs(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    CpTesterJobClient cl = new CpTesterJobClient();
                    // String defaultCp = cl.withShadedJar(false);
                    // LOGGER.info("Default / NodeJson taken from: {}",
                    // defaultCp);
                    // String usrCpFirst = cl.withShadedJar(true);
                    // LOGGER.info(" Property " +
                    // MAPREDUCE_JOB_USER_CLASSPATH_FIRST +
                    // " set to true / NodeJson taken from: {}", usrCpFirst);

                    String simple = cl.withSimpleJar(true);
                    LOGGER.info("Default / NodeJson taken from: {}", simple);
                } catch (Exception e) {
                    LOGGER.error("Sample job failed: {} ", e);
                    throw new IllegalStateException(e);
                }
                return null;
            }
        });
    }

    private void ensureInput(String fileName) throws Exception {
        FileSystem fs = FileSystem.get(getConfiguration());
        Path inputDirPath = new Path(INPUT_DIR);
        if (!fs.exists(inputDirPath)) {
            if (!(fs.mkdirs(inputDirPath))) {
                throw new IllegalStateException("Couldn't create input dir: " + inputDirPath);
            }
        }
        String remotePath = getRemotePath(fs, fileName);
        HdfsRemoteUtil.copyToHdfs(getConfiguration(), TEST_TXT, remotePath);
    }

    private String getRemotePath(FileSystem fs, String fileName) {
        Path userHome = Path.getPathWithoutSchemeAndAuthority(fs.getHomeDirectory());
        Path remotePath = new Path(userHome, INPUT_DIR + "/" + fileName);
        return remotePath.toString();
    }

    private void cleanup(Configuration configuration, Path path) throws IOException {
        LOGGER.info("Cleanup ... {}", path);
        FileSystem fs = FileSystem.get(configuration);
        if (fs.exists(path)) {
            fs.delete(path, true);
        }
    }

    private void userClassPathFirst(Configuration configuration, boolean usrCpFirst) {
        configuration.setBoolean(MAPREDUCE_JOB_USER_CLASSPATH_FIRST, usrCpFirst);
    }

    private void addDirToCp(Configuration config, String dirName) {
        String classpath = config.get(MRJobConfig.MAPREDUCE_APPLICATION_CLASSPATH);
        config.set(MRJobConfig.MAPREDUCE_APPLICATION_CLASSPATH, classpath != null ? classpath + "," + dirName : dirName);
    }
}
