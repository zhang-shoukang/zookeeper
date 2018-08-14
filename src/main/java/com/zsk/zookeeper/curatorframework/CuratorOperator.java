package com.zsk.zookeeper.curatorframework;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Create by zsk on 2018/8/14
 **/
public class CuratorOperator {
    public static final String ZK_SERVER_PATH="47.100.184.77:2181,47.100.184.77:2182,47.100.184.77:2183";
    public CuratorFramework client=null;
    public static String nodePath = "/zsk_curator/zsk1";
    public CuratorOperator() {
        /**
         * @param baseSleepTimeMs initial amount of time to wait between retries
         * @param maxRetries max number of times to retry
         * @param maxSleepMs max time in ms to sleep on each retry
         */
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(5000,5,2000);
        /**
         * 每隔一段时间重试三次
         */
        RetryPolicy retryPolicy1 = new RetryNTimes(3,5000);
        /**
         * 每隔一段时间重试一次
         */
        RetryPolicy retryPolicy2 = new RetryOneTime(5000);
        /**
         * 一直重试，不推荐使用。
         */
        RetryPolicy retryPolicy3 = new RetryForever(5000);
        /**
         * curator链接zookeeper的策略:RetryUntilElapsed
         * maxElapsedTimeMs:最大重试时间
         * sleepMsBetweenRetries:每次重试间隔
         * 重试时间超过maxElapsedTimeMs后，就不再重试
         */
        RetryPolicy retryPolicy4 = new RetryUntilElapsed(2000,3000);
        client = CuratorFrameworkFactory.builder().connectString(ZK_SERVER_PATH)
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy1).namespace("admin").build();
        client.start();
    }
    public void closeZKClient(){
        if (client!=null){
            client.close();
        }
    }

    public static void main(String[] args) throws Exception{
        CuratorOperator curatorOperator = new CuratorOperator();
        boolean started = curatorOperator.client.isStarted();
        System.out.println("当前客户端的状态"+ (started?"链接中":"已关闭"));
        if (started){

        }

    }
    public static void testCreate(CuratorOperator curatorOperator) throws Exception{
        byte[] data = "zsk_curator test".getBytes();
        curatorOperator.client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(nodePath,data);
    }
    public static void testGet(CuratorOperator curatorOperator) throws Exception{
        Stat stat = new Stat();
        byte[] datas = curatorOperator.client.getData().storingStatIn(stat).forPath(nodePath);
        System.out.println("节点" + nodePath + "的数据为: " + new String(datas));
        System.out.println("该节点的版本号为: " + stat.getVersion());
    }

    public static void testChange(CuratorOperator curatorOperator) throws Exception{
        byte[] newData = "zsk_curator test new".getBytes();
        curatorOperator.client.setData().withVersion(0).forPath(nodePath,newData);
    }
    public static void testDelete(CuratorOperator curatorOperator) throws Exception{
        curatorOperator.client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(0).forPath(nodePath);
    }
    public static void checkExists(CuratorOperator curatorOperator)throws Exception{
        curatorOperator.client.checkExists().forPath(nodePath);
    }
    public static void listChilds(CuratorOperator curatorOperator)throws Exception {
        List<String> strings = curatorOperator.client.getChildren().forPath(nodePath);
        for (String childs:strings
             ) {
            System.out.println(childs);
        }
    }
    // watcher 事件  当使用usingWatcher的时候，监听只会触发一次，监听完毕后就销毁
    public static void addWatcher(CuratorOperator curatorOperator) throws Exception{
        curatorOperator.client.getData().usingWatcher(new MyWatcher()).forPath(nodePath);
    }
    // watcher 事件  当使用usingWatcher的时候，监听只会触发一次，监听完毕后就销毁
    public static void addWatcher2(CuratorOperator curatorOperator) throws Exception{
        curatorOperator.client.getData().usingWatcher(new MyCuratorWatcher()).forPath(nodePath);
    }
    public static void nodeCacheTest(CuratorOperator curatorOperator) throws Exception{
        final NodeCache nodeCache = new NodeCache(curatorOperator.client,nodePath);
        nodeCache.start(true);
        if (nodeCache.getCurrentData()!=null){
            System.out.println("节点初始化数据为：" + new String(nodeCache.getCurrentData().getData()));
        }else {
            System.out.println("节点初始化数据为空...");
        }
        nodeCache.getListenable().addListener(()->{
            if (nodeCache.getCurrentData()==null){
                System.out.println("null");
                return;
            }
            String data = new String(nodeCache.getCurrentData().getData());
			System.out.println("节点路径：" + nodeCache.getCurrentData().getPath() + "数据：" + data);
        });
    }

    public static void childrenCacheTest(CuratorOperator curatorOperator) throws Exception{
        final PathChildrenCache childrenCache = new PathChildrenCache(curatorOperator.client,nodePath,true);
        /**
         * StartMode: 初始化方式
         * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
         * NORMAL：异步初始化
         * BUILD_INITIAL_CACHE：同步初始化
         */
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        List<ChildData> currentData = childrenCache.getCurrentData();
        System.out.println("当前数据节点的子节点数据列表：");
        for (ChildData cd : currentData) {
            String childData = new String(cd.getData());
            System.out.println(childData);
        }
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if (event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)){
                    System.out.println("子节点初始化ok...");
                }else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
                    System.out.println("添加子节点:" + event.getData().getPath());
                }else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
                    System.out.println("删除子节点:" + event.getData().getPath());
                }else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
                    System.out.println("修改子节点路径:" + event.getData().getPath());
                }
            }
        });
    }
}
