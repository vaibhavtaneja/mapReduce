package lab.cards.drivers;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.NullWritable;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
//import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
//import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import lab.cards.mappers.RecordMapper;
import lab.cards.reducers.NoKeyRecordCountReducer;


/* This program can be used for
* select count(1) from <table_name>;
* Steps
* 1. Develop mapper function
* 2. Develop reducer function
* 3. Job configuration
*/

public class RowCount extends Configured implements Tool {
	
	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new RowCount(), args);
		//RowCount is the Class whr the job conf is i.e the run() fun containig it
		
		System.exit(exitCode);
	}
	
	//run method is in Class Tool since u r implementing it u hav to oveeride its implementation 
	
	public int run(String[] args) throws Exception {
		Job job = Job.getInstance(getConf(),
		"Row Count using built in mappers and reducers");
		//^ comment that comes in log to see while troubleshoot
		
		job.setJarByClass(getClass());
		//^ set it in class that contain map &reduce fns meaning their instances
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		
		// We are not setting input format class and hence uses
		// (TextInputFormat)
		// job.setInputFormatClass(TextInputFormat.class);
		//^how to read the file int which format
		
		// Down: we need to specify what needs to be done with it
		// Custom mapper (RecordMapper) to assign 1 for each record
		// Input to mapper <Lineoffset as key, entire line as value>
		// Default behavior of default input format TextInputFormat
		
		job.setMapperClass(RecordMapper.class);
		// Output from mapper
		// <count, 1>
		// <count, 1> so on
		// ^RecordMapper has the logic that converts each line into <count, 1>
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		// Built-in reducer
		// Input to reducer <count, {1, 1, 1, ...}>
		// job.setReducerClass(IntSumReducer.class);
		// Output from reducer <count, Number of records>
		// Custom reducer
		// If you do not want to see "count" as part of output
		// and just see the record count as in select count query
		
		job.setReducerClass(NoKeyRecordCountReducer.class);
		//^contains logic to compute sum of each keys
		// Output from reducer <Number of records>
		
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(IntWritable.class);
		//^ format to write to hdfs
		
		// We are not setting output format class and hence uses default
		// (TextOutputFormat)
		// job.setOutputFormatClass(TextOutputFormat.class);
		// ^ How to wrire to hdfs 
		
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		return job.waitForCompletion(true) ? 0 : 1;
	}
}
