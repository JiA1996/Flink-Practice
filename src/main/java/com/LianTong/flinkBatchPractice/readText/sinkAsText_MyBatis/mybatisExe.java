package com.LianTong.flinkBatchPractice.readText.sinkAsText_MyBatis;

import com.LianTong.flinkBatchPractice.readText.sinkAsText_MyBatis.Bean.Province;
import com.LianTong.flinkBatchPractice.readText.sinkAsText_MyBatis.mapper.mapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;



public class mybatisExe {

    @Test
    public static void insertProvince (Province province) throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            mapper mapper = sqlSession.getMapper(mapper.class);
            mapper.insertToMysql(province);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }
}
