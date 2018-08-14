package com.zsk.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Create by zsk on 2018/8/14
 **/
public class ZKGetChildrenList implements Watcher {

    private final static Logger logger = LoggerFactory.getLogger(ZKGetChildrenList.class);
    private ZooKeeper zooKeeper = null;
    private static final String ZK_SEREVER_PATH="47.100.184.77:2181,47.100.184.77:2182,47.100.184.77:2183";
    private static final int TTIME_OUT=5000;
    public ZKGetChildrenList() {
    }
    public ZKGetChildrenList(String connectPath) {
        try {
            zooKeeper = new ZooKeeper(connectPath,TTIME_OUT,new ZKGetChildrenList());
        } catch (IOException e) {
            e.printStackTrace();
            if (zooKeeper!=null){
                try {
                    zooKeeper.close();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }
    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    public static void main(String[] args) throws Exception{
        ZKGetChildrenList zkServer = new ZKGetChildrenList(ZK_SEREVER_PATH);
        String ctx="{'callback':'childrencallback'}";
        //zkServer.getZooKeeper().getChildren("/zsk",true,new ChildrenCallBack(),ctx);
        zkServer.getZooKeeper().getChildren("/zsk",true,new Children2CallBack(),ctx);
        countDownLatch.await();
    }
    public void process(WatchedEvent watchedEvent) {
        try {
            if (watchedEvent.getType() == Event.EventType.NodeDataChanged){
                ZKGetNodeData zkGetNodeData = new ZKGetNodeData(ZK_SEREVER_PATH);
                List<String> children = zkGetNodeData.getZooKeeper().getChildren(watchedEvent.getPath(), false);
               for (String str : children){
                   System.out.println(str);
               }
                countDownLatch.countDown();
                logger.warn("子节点数据更改了");
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
