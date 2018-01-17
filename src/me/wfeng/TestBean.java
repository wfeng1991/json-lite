package me.wfeng;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestBean {

    private Map<Map<String,String>,String> map;

    private int count;

    private List<List<TestBean>> list;

    private boolean[] booleans;


    @Override
    public String toString() {
        return "TestBean{" +
                "map=" + map +
                ", count=" + count +
                ", list=" + list +
                ", booleans=" + Arrays.toString(booleans) +
                '}';
    }
}
