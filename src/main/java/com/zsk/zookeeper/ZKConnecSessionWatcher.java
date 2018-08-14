package com.zsk.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create by zsk on 2018/8/14
 **/
public class ZKConnecSessionWatcher implements Watcher {

    final static Logger log = LoggerFactory.getLogger(ZKConnecSessionWatcher.class);
    private static final String ZK_SERVER_PATH="47.100.184.77:2181,47.100.184.77:2182,47.100.184.77:2183";
    private static final int TIME_OUT=5000;
    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper = new ZooKeeper(ZK_SERVER_PATH,TIME_OUT,new ZKConnecSessionWatcher());
        long sessionId = zookeeper.getSessionId();
        String ssid="0x"+Long.toHexString(sessionId);
        System.out.println(ssid);
        byte[] sessionPasswd = zookeeper.getSessionPasswd();

        log.warn("客户端开始连接zk集群");
        log.warn("连接状态"+zookeeper.getState());
        Thread.sleep(1000);
        log.warn("连接状态"+zookeeper.getState());
        Thread.sleep(200);

        log.warn("开始会话重连");
        ZooKeeper zkSession = new ZooKeeper(ZK_SERVER_PATH, TIME_OUT, new ZKConnecSessionWatcher(),sessionId,sessionPasswd);
        log.warn("重新连接状态"+zkSession.getState());
        Thread.sleep(1000);
        log.warn("重新连接状态"+zkSession.getState());
    }
    public void process(WatchedEvent watchedEvent) {
            log.warn("接受watch通知。。。{}",watchedEvent);
    }
}
