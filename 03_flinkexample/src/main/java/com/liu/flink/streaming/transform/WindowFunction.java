package com.liu.flink.streaming.transform;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/7/6 15:09
 * @Description:
 */
public class WindowFunction {
    /**
     * 非分组流的处理时间窗口样例.
     */
    public static void processTimeWindowAll() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 数据流使用处理时间
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        env.setParallelism(1);

        SingleOutputStreamOperator<String> datasource = env
                .socketTextStream("localhost", 4444, "\n")
                .name("socket source");

        System.out.println("原始数据文件为：");
        datasource.print();

        datasource.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(10))).reduce(
                new ReduceFunction<String>() {
                @Override
                public String reduce(String s, String t1) throws Exception {
                    return s + "!" + t1;
                }
            }, new AllWindowFunction<String, String, TimeWindow>() {
                @Override
                public void apply(TimeWindow timeWindow, Iterable<String> iterable, Collector<String> collector)
                        throws Exception {
                    System.out.println("enter window");
                    System.out.println("starttime:\t" + timeWindow.getStart());
                    System.out.println("endtime:\t" + timeWindow.getEnd());
                    Iterator<String> iterator = iterable.iterator();
                    while (iterator.hasNext()) {
                        String next = iterator.next();
                        System.out.println(next);
                        collector.collect(next);
                    }
                    System.out.println("leave window");
                }
            }).print();
        env.execute();
    }

    /**
     * 非分组流的事件时间窗口样例.
     * 数据输入：
     * 000001,1461756862000
     * 000001,1461756863000
     * 000001,1461756866000
     * 000001,1461756872000
     * 000001,1461756873000
     * 000001,1461756874000
     */
    public static void eventTimeWindowAll() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 数据流使用事件时间
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        //
        env.setParallelism(1);
        SingleOutputStreamOperator<String> datasource = env
                .socketTextStream("localhost", 4444, "\n")
                .name("socket source");

        System.out.println("原始数据文件为：");
        datasource.print();
        SingleOutputStreamOperator<Tuple2<String, Long>> streamOperator1 = datasource.map(
                new RichMapFunction<String, Tuple2<String, Long>>() {
                @Override
                public Tuple2<String, Long> map(String s) throws Exception {
                    String[] split = s.split(",");
                    return new Tuple2<>(split[0], Long.valueOf(split[1]));
                }
            });

        SingleOutputStreamOperator<Tuple2<String, Long>> watermarks = streamOperator1
                .assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks<Tuple2<String, Long>>() {
                    Long currentMaxTimestamp = 0L;
                    Long maxOutOfOrderness = 10000L;
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    Watermark watermark = null;

                    @Override
                    public Watermark getCurrentWatermark() {
                        watermark = new Watermark(currentMaxTimestamp - maxOutOfOrderness);
                        //System.out.println(watermark.toString());
                        return watermark;
                    }

                    @Override
                    public long extractTimestamp(Tuple2<String, Long> tuple2, long l) {
                        System.out.println("enter timestamp extract");
                        Long timestamp = tuple2.f1;
                        currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
                        System.out.println("timestamp:" + tuple2.f0 + "," + tuple2.f1 + "|" + format.format(tuple2.f1)
                                + "," + currentMaxTimestamp + "|" + format.format(currentMaxTimestamp)
                                + "," + watermark.toString());
                        return timestamp;
                    }
                });
        watermarks.windowAll(TumblingEventTimeWindows.of(Time.seconds(3))).apply(
                new AllWindowFunction<Tuple2<String, Long>, String, TimeWindow>() {
                @Override
                public void apply(TimeWindow timeWindow, Iterable<Tuple2<String, Long>> iterable,
                                  Collector<String> collector) throws Exception {
                    System.out.println("startwindow:\t" + timeWindow.getStart());
                    System.out.println("endwindow:\t" + timeWindow.getEnd());
                    Iterator<Tuple2<String, Long>> iterator = iterable.iterator();
                    while (iterator.hasNext()) {
                        Tuple2<String, Long> next = iterator.next();
                        System.out.println(next);
                        collector.collect(next.toString());
                    }
                }
            }).print();
        env.execute();

    }




    /**
     * .
     * @param args args
     */
    public static void main(String[] args) throws Exception {
        //StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 数据流使用处理时间
        //env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        // 事件时间
        //env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        // 进入flink的时间
        // env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
        //DataStreamSource<String> datasource = env.socketTextStream("localhost", 4444, "\n");

        //非分组window
        //WindowFunction.processTimeWindowAll();
        WindowFunction.eventTimeWindowAll();


        //datasource.windowAll(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)));
        //datasource.windowAll(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)));

        //datasource.windowAll(EventTimeSessionWindows.withGap(Time.seconds(10)));
        //datasource.windowAll(ProcessingTimeSessionWindows.withGap(Time.seconds(10)));


        //分组window
        //keyedStream.window(TumblingEventTimeWindows.of(Time.seconds(10)));//事件时间滚动窗口
        //keyedStream.window(TumblingEventTimeWindows.of(Time.seconds(10), Time.hours(8)));//事件时间滚动偏移窗口
        //keyedStream.window(TumblingProcessingTimeWindows.of(Time.seconds(10)));//处理时间滚动窗口
        //keyedStream.window(TumblingProcessingTimeWindows.of(Time.seconds(10), Time.hours(8)));//处理时间滚动偏移窗口

        //keyedStream.window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)));//事件时间滑动窗口
        //keyedStream.window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5), Time.hours(8)));//事件时间滑动偏移窗口
        //keyedStream.window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)));//处理时间滑动窗口
        //处理时间滑动偏移窗口
        //keyedStream.window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5), Time.hours(8)));

        //keyedStream.window(EventTimeSessionWindows.withGap(Time.seconds(10)));//事件时间会话窗口
        //keyedStream.window(ProcessingTimeSessionWindows.withGap(Time.seconds(10)));//处理时间会话窗口


        //非分组流
        //datasource.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Student>() {
        //@Override
        //public long extractAscendingTimestamp(Student student) {
        //return student.getId();
        //}
        //});
        //datasource.timeWindowAll(Time.seconds(10));// 按照时间窗口，滚动
        //datasource.timeWindowAll(Time.seconds(10), Time.seconds(5));//按照时间窗口，滑动

        //datasource.countWindowAll(10);//按照数量，滚动
        //datasource.countWindowAll(10, 5);//按照数量，滑动

        //分组流

        //keyedStream.timeWindow(Time.seconds(10));//按照时间窗口，滚动
        //keyedStream.timeWindow(Time.seconds(10), Time.seconds(5));//按照时间窗口，滑动

        //keyedStream.countWindow(10);//按照数量，滚动
        //keyedStream.countWindow(10, 5);//按照数量，滑动

    }
}
