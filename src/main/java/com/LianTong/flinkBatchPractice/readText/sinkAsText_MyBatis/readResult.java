package com.LianTong.flinkBatchPractice.readText.sinkAsText_MyBatis;

import java.io.*;
import java.math.BigDecimal;
import com.LianTong.flinkBatchPractice.readText.sinkAsText_MyBatis.Bean.Province;
import org.junit.jupiter.api.Test;

public class readResult {

@Test
    public void getSystem(){

        Province province = new Province();
        String provinceName = "";
        String productID = "";
        BigDecimal count = BigDecimal.ONE;
        BigDecimal sumOfArup = BigDecimal.ZERO;

        String srcPath = "P:\\projects\\flinkDataProcess\\result\\readText\\test_result";
        BufferedReader reader = null;
        String line = null;

        try {
            reader = new BufferedReader(new FileReader(srcPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            while ((line = reader.readLine()) != null) {

                System.out.println(line);

                String half[] = line.split("\\|");
                provinceName = half[0].split(",")[0].substring(1);
                productID = half[0].split(",")[1];
                String sum_par = half[0].split(",")[2];
                sumOfArup = new BigDecimal(sum_par.substring(0,sum_par.length() - 1));
                String count_par = half[1].split(",")[2];
                count = new BigDecimal(count_par.substring(0,count_par.length() - 1));
                BigDecimal avgOfArup = sumOfArup.divide(count,5,BigDecimal.ROUND_HALF_UP);

                province.setCount(count);
                province.setProvinceName(provinceName);
                province.setProductID(productID);
                province.setSumOfArup(sumOfArup);
                province.setAvgOfArup(avgOfArup);

                mybatisExe.insertProvince(province);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}
