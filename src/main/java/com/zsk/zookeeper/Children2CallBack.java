package com.zsk.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Create by zsk on 2018/8/14
 **/
public class Children2CallBack implements AsyncCallback.Children2Callback {
    public void processResult(int i, String path, Object o, List<String> list, Stat stat) {
        for (String str : list){
            System.out.println(str);
        }
        System.out.println("ChildrenCallBack"+path);
        System.out.println((String)o);
        System.out.println(stat.toString());
    }
}
