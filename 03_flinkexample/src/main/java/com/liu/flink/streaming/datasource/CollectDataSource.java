package com.liu.flink.streaming.datasource;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.SplittableIterator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 使用集合或者迭代器作为datasource.
 * @Auther: liudongfei
 * @Date: 2019/7/5 15:29
 * @Description:
 */
public class CollectDataSource {

    static class MyIterator implements Iterator<Person>, Serializable {
        private List<Person> list = null;
        private int cursor = 0;

        public MyIterator() {
            list = new ArrayList<>();
            list.add(new Person("li", 12));
            list.add(new Person("qian", 14));
            list.add(new Person("sun", 16));
            list.add(new Person("zhao", 18));
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Person next() {
            cursor++;
            return list.get(cursor % list.size());
        }
    }

    static class MySplittableIterator extends SplittableIterator<Person> implements Serializable {
        private List<Person> list = null;
        private int cursor = 0;

        public MySplittableIterator() {
            list = new ArrayList<>();
            list.add(new Person("li", 12));
            list.add(new Person("qian", 14));
            list.add(new Person("sun", 16));
            list.add(new Person("zhao", 18));
        }

        @Override
        public Iterator<Person>[] split(int i) {
            return new Iterator[0];
        }

        @Override
        public int getMaximumNumberOfSplits() {
            return 2;
        }


        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Person next() {
            return null;
        }
    }

    /**
     * .
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        ArrayList<Object> list = new ArrayList<>();
        list.add(new Person("li", 12));
        list.add(new Person("qian", 14));
        list.add(new Person("sun", 16));
        list.add(new Person("zhao", 18));
        // datasource为迭代器
        //DataStreamSource<Person> datasource = env.fromCollection(new MyIterator(), Person.class);
        // datasource为并行迭代器
        env.fromParallelCollection(new MySplittableIterator(), Person.class);
        // datasource为集合
        //DataStreamSource<Object> datasource = env.fromCollection(list);
        // datasource为元素数组
        //DataStreamSource<Person> datasource = env.fromElements(new Person("li", 12),
        //new Person("qian", 14), new Person("sun", 16));
        // datasource为数字序列
        DataStreamSource<Long> datasource = env.generateSequence(1, 100);


        datasource.print();
        env.execute();
    }
}
