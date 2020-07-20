package com.liu.java.base.j8;

import java.util.ArrayList;
import java.util.Optional;

/**
 * 使用java8 stream 特性操作list.
 */
public class StreamDemo {
    /**
     * 操作样例.
     * @param args args
     */
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("abb");
        list.add("aba");
        list.add("baa");
        list.add("bbb");
        list.add("ccc");
        list.stream().filter(s -> s.startsWith("a")).forEach(s -> System.out.println(s));
        list.stream().sorted().filter(s -> s.startsWith("a")).forEach(s -> System.out.println(s));
        list.stream().map(s -> s.toUpperCase()).forEach(s -> System.out.println(s));
        boolean anyMatch = list.stream().anyMatch(s -> s.startsWith("a"));
        System.out.println(anyMatch);
        boolean allMatch = list.stream().allMatch(s -> s.startsWith("a"));
        System.out.println(allMatch);
        boolean noneMatch = list.stream().noneMatch(s -> s.startsWith("z"));
        System.out.println(noneMatch);
        long count = list.stream().filter(s -> s.startsWith("a")).count();
        System.out.println(count);
        Optional<String> reduce = list.stream().filter(s -> s.startsWith("a")).reduce((s, s2) -> s + "#" + s2);
        System.out.println(reduce.get());
    }
}
