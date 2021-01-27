package com.ptrchain;

import com.google.common.collect.*;

import java.util.List;
import java.util.Set;


public class FileTest {

    public static void main(String[] args) {

        List<String> list = Lists.newArrayList("sun","sun2","sun3");
        System.out.println(list.size());
        list.stream().forEach(System.out::print);

        Set<String> set = Sets.newHashSet();

        Set<String> set2 = Sets.newHashSet("one","two","three","one");

        set2.stream().forEach(System.out::println);
    }
}
