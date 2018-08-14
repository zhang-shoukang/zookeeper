package com.zsk.zookeeper;


import org.apache.zookeeper.AsyncCallback;

/**
 * Create by zsk on 2018/8/14
 **/
public class DeleteCallBack implements AsyncCallback.VoidCallback {
    public void processResult(int i, String s, Object o) {
        System.out.println("删除节点"+s);
        System.out.println(o.toString());
    }
}
