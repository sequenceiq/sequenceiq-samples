package com.sequenceiq.yarntest.mr;

/**

 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

/**
 * A map/reduce program that estimates the value of Pi
 * using a quasi-Monte Carlo (qMC) method.
 * Arbitrary integrals can be approximated numerically by qMC methods.
 * In this example,
 * we use a qMC method to approximate the integral $I = \int_S f(x) dx$,
 * where $S=[0,1)^2$ is a unit square,
 * $x=(x_1,x_2)$ is a 2-dimensional point,
 * and $f$ is a function describing the inscribed circle of the square $S$,
 * $f(x)=1$ if $(2x_1-1)^2+(2x_2-1)^2 <= 1$ and $f(x)=0$, otherwise.
 * It is easy to see that Pi is equal to $4I$.
 * So an approximation of Pi is obtained once $I$ is evaluated numerically.
 * 
 * There are better methods for computing Pi.
 * We emphasize numerical approximation of arbitrary integrals in this example.
 * For computing many digits of Pi, consider using bbp.
 *
 * The implementation is discussed below.
 *
 * Mapper:
 *   Generate points in a unit square
 *   and then count points inside/outside of the inscribed circle of the square.
 *
 * Reducer:
 *   Accumulate points inside/outside results from the mappers.
 *
 * Let numTotal = numInside + numOutside.
 * The fraction numInside/numTotal is a rational approximation of
 * the value (Area of the circle)/(Area of the square) = $I$,
 * where the area of the inscribed circle is Pi/4
 * and the area of unit square is 1.
 * Finally, the estimated value of Pi is 4(numInside/numTotal).  
 */
public class QuasiMonteCarlo  {
  static final String DESCRIPTION
      = "A map/reduce program that estimates Pi using a quasi-Monte Carlo method.";
  /** tmp directory for input/output */
  public static final String TMP_DIR_PREFIX = QuasiMonteCarlo.class.getSimpleName();
  
  /** 2-dimensional Halton sequence {H(i)},
   * where H(i) is a 2-dimensional point and i >= 1 is the index.
   * Halton sequence is used to generate sample points for Pi estimation. 
   */
  private static class HaltonSequence {
    /** Bases */
    static final int[] P = {2, 3}; 
    /** Maximum number of digits allowed */
    static final int[] K = {63, 40}; 

    private long index;
    private double[] x;
    private double[][] q;
    private int[][] d;

    /** Initialize to H(startindex),
     * so the sequence begins with H(startindex+1).
     */
    HaltonSequence(long startindex) {
      index = startindex;
      x = new double[K.length];
      q = new double[K.length][];
      d = new int[K.length][];
      for(int i = 0; i < K.length; i++) {
        q[i] = new double[K[i]];
        d[i] = new int[K[i]];
      }

      for(int i = 0; i < K.length; i++) {
        long k = index;
        x[i] = 0;
        
        for(int j = 0; j < K[i]; j++) {
          q[i][j] = (j == 0? 1.0: q[i][j-1])/P[i];
          d[i][j] = (int)(k % P[i]);
          k = (k - d[i][j])/P[i];
          x[i] += d[i][j] * q[i][j];
        }
      }
    }

    /** Compute next point.
     * Assume the current point is H(index).
     * Compute H(index+1).
     * 
     * @return a 2-dimensional point with coordinates in [0,1)^2
     */
    double[] nextPoint() {
      index++;
      for(int i = 0; i < K.length; i++) {
        for(int j = 0; j < K[i]; j++) {
          d[i][j]++;
          x[i] += q[i][j];
          if (d[i][j] < P[i]) {
            break;
          }
          d[i][j] = 0;
          x[i] -= (j == 0? 1.0: q[i][j-1]);
        }
      }
      return x;
    }
  }

  /**
   * Mapper class for Pi estimation.
   * Generate points in a unit square
   * and then count points inside/outside of the inscribed circle of the square.
   */
  public static class QmcMapper extends 
      Mapper<LongWritable, LongWritable, BooleanWritable, LongWritable> {

    /** Map method.
     * @param offset samples starting from the (offset+1)th sample.
     * @param size the number of samples for this map
     * @param context output {ture->numInside, false->numOutside}
     */
    public void map(LongWritable offset,
                    LongWritable size,
                    Context context) 
        throws IOException, InterruptedException {

      final HaltonSequence haltonsequence = new HaltonSequence(offset.get());
      long numInside = 0L;
      long numOutside = 0L;

      for(long i = 0; i < size.get(); ) {
        //generate points in a unit square
        final double[] point = haltonsequence.nextPoint();

        //count points inside/outside of the inscribed circle of the square
        final double x = point[0] - 0.5;
        final double y = point[1] - 0.5;
        if (x*x + y*y > 0.25) {
          numOutside++;
        } else {
          numInside++;
        }

        //report status
        i++;
        if (i % 1000 == 0) {
          context.setStatus("Generated " + i + " samples.");
        }
      }

      //output map results
      context.write(new BooleanWritable(true), new LongWritable(numInside));
      context.write(new BooleanWritable(false), new LongWritable(numOutside));
    }
  }

  /**
   * Reducer class for Pi estimation.
   * Accumulate points inside/outside results from the mappers.
   */
  public static class QmcReducer extends 
      Reducer<BooleanWritable, LongWritable, WritableComparable<?>, Writable> {
    
    private long numInside = 0;
    private long numOutside = 0;
      
    /**
     * Accumulate number of points inside/outside results from the mappers.
     * @param isInside Is the points inside? 
     * @param values An iterator to a list of point counts
     * @param context dummy, not used here.
     */
    public void reduce(BooleanWritable isInside,
        Iterable<LongWritable> values, Context context)
        throws IOException, InterruptedException {
      if (isInside.get()) {
        for (LongWritable val : values) {
          numInside += val.get();
        }
      } else {
        for (LongWritable val : values) {
          numOutside += val.get();
        }
      }
    }

    /**
     * Reduce task done, write output to a file.
     */
    @Override
    public void cleanup(Context context) throws IOException {
      //write output to a file
      Configuration conf = context.getConfiguration();
      Path outDir = new Path(conf.get(FileOutputFormat.OUTDIR));
      Path outFile = new Path(outDir, "reduce-out");
      FileSystem fileSys = FileSystem.get(conf);
      SequenceFile.Writer writer = SequenceFile.createWriter(fileSys, conf,
          outFile, LongWritable.class, LongWritable.class, 
          CompressionType.NONE);
      writer.append(new LongWritable(numInside), new LongWritable(numOutside));
      writer.close();
    }
  }

  /**
   * Run a map/reduce job for estimating Pi.
   *
   * @return the estimated value of Pi
   */
  public static JobID submitPiEstimationMRApp(String jobName, int numMaps, long numPoints,
      Path tmpDir, Configuration conf
      ) throws IOException, ClassNotFoundException, InterruptedException {
    Job job = new Job(conf);
    //setup job conf
    job.setJobName(jobName);
    job.setJarByClass(QuasiMonteCarlo.class);

    job.setInputFormatClass(SequenceFileInputFormat.class);

    job.setOutputKeyClass(BooleanWritable.class);
    job.setOutputValueClass(LongWritable.class);
    job.setOutputFormatClass(SequenceFileOutputFormat.class);

    job.setMapperClass(QmcMapper.class);

    job.setReducerClass(QmcReducer.class);
    job.setNumReduceTasks(1);

    // turn off speculative execution, because DFS doesn't handle
    // multiple writers to the same file.
    job.setSpeculativeExecution(false);

    //setup input/output directories
    final Path inDir = new Path(tmpDir, "in");
    final Path outDir = new Path(tmpDir, "out");
    FileInputFormat.setInputPaths(job, inDir);
    FileOutputFormat.setOutputPath(job, outDir);

    final FileSystem fs = FileSystem.get(conf);
    if (fs.exists(tmpDir)) {
    	fs.delete(tmpDir, true);
//      throw new IOException("Tmp directory " + fs.makeQualified(tmpDir)
//          + " already exists.  Please remove it first.");
    }
    if (!fs.mkdirs(inDir)) {
      throw new IOException("Cannot create input directory " + inDir);
    }

  //  try {
      //generate an input file for each map task
      for(int i=0; i < numMaps; ++i) {
        final Path file = new Path(inDir, "part"+i);
        final LongWritable offset = new LongWritable(i * numPoints);
        final LongWritable size = new LongWritable(numPoints);
        final SequenceFile.Writer writer = SequenceFile.createWriter(
            fs, conf, file,
            LongWritable.class, LongWritable.class, CompressionType.NONE);
        try {
          writer.append(offset, size);
        } finally {
          writer.close();
        }
        System.out.println("Wrote input for Map #"+i);
      }
  
      //start a map/reduce job
      System.out.println("Starting Job");
      final long startTime = System.currentTimeMillis();
      job.submit();
//      final double duration = (System.currentTimeMillis() - startTime)/1000.0;
//      System.out.println("Job Finished in " + duration + " seconds");
      return job.getJobID();
    
//    } finally {
//      fs.delete(tmpDir, true);
//    }
  }


}
