package com.LianTong.flinkBatchPractice.readText.sinkAsText_MyBatis

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE
import org.apache.flink.api.scala._

object readText_scala {

  def main(args: Array[String]) {

    val filePath = "P:\\projects\\flinkDataProcess\\src\\main\\resources\\test_original.REQ"
    val dsenv = ExecutionEnvironment.getExecutionEnvironment
    val textds = dsenv.readTextFile(filePath)

    val test_province = textds.map{ x => (x.split("\\|")(1),
                                          x.split("\\|")(4),
                                          x.split("\\|")(5).toLong)}
        .groupBy(0,1)
        .sum(2)

    val test_count = textds.map{ x => (x.split("\\|")(1),
                                       x.split("\\|")(4),
                                       1.0)}
      .groupBy(0,1)
      .sum(2)

    val combined = test_province.join(test_count).where(0,1).equalTo(0,1)
    combined.print()

    val sinkPath = "P:\\projects\\flinkDataProcess\\result\\readText\\test_result"
    combined.writeAsCsv(sinkPath,"\n", "|", OVERWRITE).setParallelism(1)
    dsenv.execute("My job")
    print("job done")
  }

}
