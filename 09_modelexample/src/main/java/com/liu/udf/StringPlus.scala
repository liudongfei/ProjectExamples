package com.liu.udf

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

class StringPlus extends UserDefinedAggregateFunction{
  //输入数据的类型的shcema
  override def inputSchema: StructType = {
    StructType(Array(StructField("str", StringType, true)))
  }

  //中间缓存值的数据类型的schema
  override def bufferSchema: StructType = {
    StructType(Array(StructField("count", StringType, true)))
  }
  //函数返回值的类型
  override def dataType: DataType = {
    StringType
  }

  override def deterministic: Boolean = {
    true
  }

  //为每个组的数据执行数据初始化操作
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = ""
  }

  //指有新的值进来时，如何对分组的进行运算
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = buffer.getAs[String](0) + "+" + input.getAs[String](0)
  }

  //不同节点的相同分组之间的聚合策略
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getAs[String](0) + buffer2.getAs[String](0)
  }

  //如何通过中间的缓存值返回最终结果
  override def evaluate(buffer: Row): Any = {
    buffer.getAs[String](0).replaceFirst("\\+", "")
  }
}
