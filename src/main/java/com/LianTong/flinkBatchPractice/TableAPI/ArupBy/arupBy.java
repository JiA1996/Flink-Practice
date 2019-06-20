package com.LianTong.flinkBatchPractice.TableAPI.ArupBy;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.table.api.BatchTableEnvironment;
import org.apache.flink.table.sinks.CsvTableSink;
import org.apache.flink.table.sinks.TableSink;

import static org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE;

public class arupBy {

    private static final TypeInformation[] fieldTypes = {Types.STRING, Types.LONG, Types.FLOAT};

    public static void province(BatchTableEnvironment tEnv, Integer numOfParallelism){

        String sinkPath = "P:\\projects\\flinkDataProcess\\result\\TableAPI\\省分";
        String tableName = "province";
        String[] fieldNames = {"省分", "省分总数", "平均arup值"};
        TableSink<?> sink = new CsvTableSink(sinkPath, "|", numOfParallelism, OVERWRITE);
        tEnv.registerTableSink(tableName, fieldNames, fieldTypes, sink);
        tEnv.sqlQuery(
                "SELECT 省分, count(省分), AVG(arup值) as 平均arup值" +
                        " FROM fileSource" +
                        " group by 省分")
                .insertInto(tableName);
    }
}
