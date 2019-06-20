package com.LianTong.flinkBatchPractice.readText.sinkToMySQL;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple5;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class readText {

    @Test
    public static void main(String[] args) throws Exception {

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        String filePath = "P:\\projects\\flinkDataProcess\\src\\main\\resources\\test_original.REQ";
        DataSet<String> textDs = env.readTextFile(filePath);

        DataSet<Tuple3<String, String, Long>> arupSum =
                textDs.map(new sumTokenizer())
                .groupBy(0,1)
                .sum(2);

        DataSet<Tuple3<String, String, Double>> count =
                textDs.map(new countTokenizer())
                        .groupBy(0,1)
                        .sum(2);

        DataSet<Tuple2<Tuple3<String,String,Long>,Tuple3<String,String,Double>>> combined = arupSum.join(count)
                .where(0,1)
                .equalTo(0,1);

        DataSet<Tuple5<String, String, BigDecimal, BigDecimal, BigDecimal>> result = combined.map(new resultTokenizer());

        result.output(
                new JDBCOutput()
        );

        env.execute("MYJoNB");
    }

    public static class sumTokenizer implements MapFunction<String, Tuple3<String, String, Long>> {

        @Override
        public Tuple3<String, String, Long> map(String s) throws Exception {
            String[] token = s.split("\\|");
                return new Tuple3<String, String, Long>(token[1],token[4],Long.parseLong(token[5]));

        }
    }

    public static class countTokenizer implements MapFunction<String, Tuple3<String, String, Double>> {

        @Override
        public Tuple3<String, String, Double> map(String s) throws Exception {
            String[] token = s.split("\\|");
            return new Tuple3<String, String, Double>(token[1],token[4],1.0);

        }
    }

    public static class resultTokenizer implements MapFunction<Tuple2<Tuple3<String,String,Long>,Tuple3<String,String,Double>>, Tuple5<String, String, BigDecimal, BigDecimal, BigDecimal>> {

        @Override
        public Tuple5<String, String, BigDecimal, BigDecimal, BigDecimal> map(Tuple2<Tuple3<String, String, Long>, Tuple3<String, String, Double>> myTP) throws Exception {
            BigDecimal Count = new BigDecimal(myTP.f1.f2);
            String ProvinceName = myTP.f0.f0;
            String ProductID = myTP.f0.f1;
            BigDecimal SumOfArup = new BigDecimal(myTP.f0.f2);
            BigDecimal AvgOfArup = SumOfArup.divide(Count,5,BigDecimal.ROUND_HALF_UP);
            return new Tuple5<String, String, BigDecimal, BigDecimal, BigDecimal>(ProvinceName, ProductID, Count, SumOfArup, AvgOfArup);
        }
    }

}
