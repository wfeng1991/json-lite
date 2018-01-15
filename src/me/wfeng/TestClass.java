package me.wfeng;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestClass {

    private Map<Map<String,String>,String> map;

    private int count;

    private List<List<TestClass>> list;

    private boolean[] booleans;


    @Override
    public String toString() {
        return "TestClass{" +
                "map=" + map +
                ", count=" + count +
                ", list=" + list +
                ", booleans=" + Arrays.toString(booleans) +
                '}';
    }
}
