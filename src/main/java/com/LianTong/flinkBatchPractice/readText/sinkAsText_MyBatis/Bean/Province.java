package com.LianTong.flinkBatchPractice.readText.sinkAsText_MyBatis.Bean;

import java.math.BigDecimal;

public class Province {

    private String provinceName;  //省分
    private String productID;     //产品id
    private BigDecimal count;     //个数
    private BigDecimal sumOfArup; //Arup总和
    private BigDecimal avgOfArup; //Arup平均值

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String province) {
        this.provinceName = province;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public BigDecimal getSumOfArup() {
        return sumOfArup;
    }

    public void setSumOfArup(BigDecimal sumOfArup) {
        this.sumOfArup = sumOfArup;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public BigDecimal getAvgOfArup() {
        return avgOfArup;
    }

    public void setAvgOfArup(BigDecimal avgOfArup) {
        this.avgOfArup = avgOfArup;
    }

    @Override
    public String toString() {
        return "Province{" +
                "province='" + provinceName + '\'' +
                ", productID=" + productID +
                ", count=" + count +
                ", sumOfArup=" + sumOfArup +
                ", avgOfArup=" + avgOfArup +
                '}';
    }
}
