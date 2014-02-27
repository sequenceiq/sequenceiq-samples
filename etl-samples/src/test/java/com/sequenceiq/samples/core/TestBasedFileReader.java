package com.sequenceiq.samples.core;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Ricsi on 2014.02.13..
 */
public class TestBasedFileReader {

    private static File getFile(String path){
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList("src", "test", "resources"));
        Collections.addAll(list, path.split("/"));
        String[] segments = list.toArray(new String[0]);
        return FileUtils.getFile(segments);
    }

    public static String getFileContent(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(getFile(path).getPath()), Charset.defaultCharset());
        return StringUtils.join("", lines);
    }

    public static List<String> getFileContentAsList(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(getFile(path).getPath()), Charset.defaultCharset());
        return lines;
    }



}
