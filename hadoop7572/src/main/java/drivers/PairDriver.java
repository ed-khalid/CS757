package drivers;

import compositekeys.PairKey;
import mappers.PairMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import reducers.PairReducer;

/**
 * Created by Ahmed Alabdullah on 3/15/15.
 */
public class PairDriver {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

        if (otherArgs.length != 3) {
            System.err.println("Usage: PairDriver <in> <out> <dataset>");
            System.exit(2);
        }

        String dataset = otherArgs[2];
        String separator = null;

        if (dataset.equalsIgnoreCase("100K")) {
            separator = "\t";
        }
        else {
            separator = "::";
        }
        conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", separator);

        //CONFIGURE THE JOB
        Job job = new Job(conf, "movie pairs");

        job.setJarByClass(PairDriver.class);

        //map-reduce classes
        job.setMapperClass(PairMapper.class);
        job.setCombinerClass(PairReducer.class);
        job.setReducerClass(PairReducer.class);

        //key-val classes
        job.setOutputKeyClass(PairKey.class);
        job.setOutputValueClass(IntWritable.class);

        job.setInputFormatClass(KeyValueTextInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        System.exit(job.waitForCompletion(true)? 0 :1);

    }


}