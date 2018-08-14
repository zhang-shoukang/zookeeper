package com.zsk.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Create by zsk on 2018/8/14
 **/
public class ZKNodeOperator implements Watcher{
    final static Logger log  = LoggerFactory.getLogger(ZKNodeOperator.class);
    static final String ZK_SERVER_PATH="47.100.184.77:2181,47.100.184.77:2182,47.100.184.77:2183";
    static int TIME_OUT=5000;
    private ZooKeeper zooKeeper = null;
    public ZKNodeOperator() {
    }
    public ZKNodeOperator(String  connectPath) {
        try {
            zooKeeper = new ZooKeeper(connectPath, TIME_OUT, new ZKNodeOperator());
        }catch (IOException e){
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

    public void creatZkNode(String path,byte[] data,List<ACL> acls){
        try {
           String result = zooKeeper.create(path, data, acls, CreateMode.PERSISTENT);
            System.out.println("创建节点"+result+"成功");
            new Thread().sleep(1000);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        ZKNodeOperator zkNodeOperator = new ZKNodeOperator(ZK_SERVER_PATH);
        zkNodeOperator.creatZkNode("/zsk/zsk1","zsk1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE);
        String ctx="{\"delete\":\"success\"}";
        zkNodeOperator.getZooKeeper().delete("/zsk/zsk1",0,new DeleteCallBack(),ctx);
        Thread.sleep(2000); //这里一定要sleep 一会，要不然，来不及调用回掉函数，主程序就结束了。
    }

    public void process(WatchedEvent watchedEvent) {

    }
}
