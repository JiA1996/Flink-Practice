package com.LianTong.flinkBatchPractice.readText

import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.api.java.ExecutionEnvironment
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat
import org.apache.flink.api.java.typeutils.RowTypeInfo

object flink_jdbc_scala {

  val driverClass = "com.mysql.jdbc.Driver"
  val dbUrl = "jdbc:mysql://localhost:3306/test1?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=utf8"
  val userName = "root"
  val passWord = "Pass1996!"

  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    selectAll(env)
  }

  def selectAll(env: ExecutionEnvironment) = {
    val sqlStatement = "select * from st3 where id between 5 and 8"


    val inputBuilder = JDBCInputFormat.buildJDBCInputFormat()
      .setDrivername(driverClass)
      .setDBUrl(dbUrl)
      .setUsername(userName)
      .setPassword(passWord)
      .setQuery(sqlStatement)
      .setRowTypeInfo(new RowTypeInfo(
        BasicTypeInfo.INT_TYPE_INFO,
        BasicTypeInfo.STRING_TYPE_INFO,
        BasicTypeInfo.INT_TYPE_INFO,
        BasicTypeInfo.STRING_TYPE_INFO,
        BasicTypeInfo.STRING_TYPE_INFO,
        BasicTypeInfo.INT_TYPE_INFO,
        BasicTypeInfo.INT_TYPE_INFO,
        BasicTypeInfo.INT_TYPE_INFO))

    val source = env.createInput(inputBuilder.finish())

    source.print()
  }

}
