package com.zsk.zookeeper;

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
public class ZKNodeExists implements Watcher{
    
    private ZooKeeper zooKeeper =null;
    final static Logger log = LoggerFactory.getLogger(ZKConnecSessionWatcher.class);
    private static final String ZK_SERVER_PATH="47.100.184.77:2181,47.100.184.77:2182,47.100.184.77:2183";
    private static final int TIME_OUT=5000;

    public ZKNodeExists() {
    }

    public ZKNodeExists(String connectStr){
        try {
            zooKeeper = new ZooKeeper(connectStr, TIME_OUT, new ZKNodeExists());
        }catch(IOException ex){
            ex.printStackTrace();
            if (zooKeeper!=null){
                try {
                    zooKeeper.close();
                }catch (InterruptedException e){
                    e.printStackTrace();
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
        ZKNodeExists zkNodeExists = new ZKNodeExists(ZK_SERVER_PATH);
        ZooKeeper zooKeeper = zkNodeExists.getZooKeeper();
        Stat exists = zooKeeper.exists("/zsk", true);
        if (exists!=null){
            log.warn("该节点存在，并且该节点的版本号为{}",exists.getVersion());
        }else {
            log.warn("该节点不存在");
        }
        countDownLatch.await();

    }

    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType()== Event.EventType.NodeCreated){
            countDownLatch.countDown();
            log.warn("该节点被创建");
        }else if (watchedEvent.getType()== Event.EventType.NodeDataChanged){
            countDownLatch.countDown();
            log.warn("该节点数据被修改");
        }else if (watchedEvent.getType()== Event.EventType.NodeDeleted){
            countDownLatch.countDown();
            log.warn("该节点数据被删除");

        }
    }
}
