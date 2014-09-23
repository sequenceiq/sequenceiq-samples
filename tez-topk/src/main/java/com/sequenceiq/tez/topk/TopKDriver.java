package com.sequenceiq.tez.topk;

import java.text.DecimalFormat;

import org.apache.hadoop.util.ProgramDriver;

public class TopKDriver {

    private static final DecimalFormat formatter = new DecimalFormat("###.##%");

    public static void main(String argv[]) {
        int exitCode = -1;
        ProgramDriver pgd = new ProgramDriver();
        try {
            pgd.addClass("topk", TopK.class, "topk");
            pgd.addClass("topkgen", TopKDataGen.class, "topkgen");
            exitCode = pgd.run(argv);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        System.exit(exitCode);
    }

}

