package com.zsk.zookeeper.curatorframework;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

/**
 * Create by zsk on 2018/8/14
 **/
public class MyCuratorWatcher implements CuratorWatcher {
    public void process(WatchedEvent event) {
        System.out.println("触发watcher 节点路径为"+event.getPath());
    }
}
