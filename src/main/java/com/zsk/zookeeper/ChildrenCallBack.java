package com.zsk.zookeeper;

import org.apache.zookeeper.AsyncCallback;

import java.util.List;

/**
 * Create by zsk on 2018/8/14
 **/
public class ChildrenCallBack implements AsyncCallback.ChildrenCallback {
    public void processResult(int i, String path, Object o, List<String> list) {
        for (String str : list){
            System.out.println(str);
        }
        System.out.println("ChildrenCallBack"+path);
        System.out.println((String)o);
    }
}
