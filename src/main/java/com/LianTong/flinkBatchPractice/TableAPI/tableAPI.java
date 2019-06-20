package com.LianTong.flinkBatchPractice.TableAPI;
import com.LianTong.flinkBatchPractice.TableAPI.ArupBy.arupBy;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.table.api.BatchTableEnvironment;
import org.apache.flink.table.descriptors.FileSystem;
import org.apache.flink.table.descriptors.Csv;
import org.apache.flink.table.descriptors.Schema;

public class tableAPI {

    public static void main(String[] args) throws Exception {

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tEnv = BatchTableEnvironment.getTableEnvironment(env);
        String sourcePath = tableAPI.class.getClassLoader()
                .getResource("test_original.REQ").getPath();

        //register the testing data into a Flink table
        tEnv.connect(new FileSystem().path(sourcePath))
                .withFormat(
                        new Csv()
                                .field("归属系统", Types.STRING).fieldDelimiter("|")
                                .field("省分", Types.STRING).fieldDelimiter("|")
                                .field("地市", Types.STRING).fieldDelimiter("|")
                                .field("业务号码", Types.STRING).fieldDelimiter("|")
                                .field("产品ID", Types.STRING).fieldDelimiter("|")
                                .field("arup值", Types.FLOAT).fieldDelimiter("|")
                                .field("others", Types.STRING).lineDelimiter("\n")
                )
                .withSchema(
                        new Schema()
                                .field("归属系统", Types.STRING)
                                .field("省分", Types.STRING)
                                .field("地市", Types.STRING)
                                .field("业务号码", Types.STRING)
                                .field("产品ID", Types.STRING)
                                .field("arup值", Types.FLOAT)
                                .field("others", Types.STRING)
                )
                .registerTableSource("fileSource");

        //invoke function to query the registered table
        Integer numOfParallelism = 1;
        arupBy.province(tEnv, numOfParallelism);

        //execute
        long start,end;
        start = System.currentTimeMillis();
        env.execute();
        end = System.currentTimeMillis();
        System.out.println("start time:" + start+ "; end time:" + end+ "; Run Time:" + (end - start) + "(ms)");
    }
}