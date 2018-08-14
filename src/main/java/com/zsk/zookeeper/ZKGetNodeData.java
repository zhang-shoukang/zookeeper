package com.zsk.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Create by zsk on 2018/8/14
 **/
public class ZKGetNodeData implements Watcher{
    final static Logger logger = LoggerFactory.getLogger(ZKGetNodeData.class);
    final static String ZK_SERVER_PATH="47.100.184.77:2181,47.100.184.77:2182,47.100.184.77:2183";
    final static int TIME_OUT=5000;
    private static  Stat stat= new Stat();

    private ZooKeeper zooKeeper = null;
    private String ConnectionPath=null;

    public ZKGetNodeData() {
    }

    public ZKGetNodeData(String connectionPath) {
        try {
            zooKeeper = new ZooKeeper(connectionPath,TIME_OUT,new ZKGetNodeData());
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
        ZKGetNodeData zkGetNodeData = new ZKGetNodeData(ZK_SERVER_PATH);
        ZooKeeper zooKeeper = zkGetNodeData.getZooKeeper();
        byte[] data = zooKeeper.getData("/zsk/zsk1", true, stat);
        String result = new String(data);
        System.out.println("当前值："+result);
        countDownLatch.await();
    }


    public void process(WatchedEvent watchedEvent) {
        try {
            if (watchedEvent.getType() == Event.EventType.NodeDataChanged){
                ZKGetNodeData zkGetNodeData = new ZKGetNodeData(ZK_SERVER_PATH);
                byte[] data = zkGetNodeData.getZooKeeper().getData("/zsk/zsk1", false, stat);
                String result = new String(data);
                System.out.println("更改后的值："+result);
                System.out.println("更改后的值版本号："+stat.getVersion());

                countDownLatch.countDown();
               logger.warn("节点数据更改了");
           }else if (watchedEvent.getType() == Event.EventType.NodeCreated){
               countDownLatch.countDown();
               logger.warn("节点被创建");
           }
           else if (watchedEvent.getType() == Event.EventType.NodeDeleted){
               countDownLatch.countDown();
               logger.warn("节点被删除了");
           }
           else if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
               countDownLatch.countDown();
               logger.warn("节点的子节点更改了");
           }
           else if (watchedEvent.getType() == Event.EventType.NodeDataChanged){
               countDownLatch.countDown();
               logger.warn("节点数据更改了");
           }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
