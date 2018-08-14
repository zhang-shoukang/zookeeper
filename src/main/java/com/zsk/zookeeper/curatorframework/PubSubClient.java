package com.zsk.zookeeper.curatorframework;

import com.zsk.pojo.RedisConfig;
import com.zsk.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.CountDownLatch;

/**
 * Create by zsk on 2018/8/14
 **/
public class PubSubClient {
    public CuratorFramework client=null;
    public static final String ZK_SERVER_PATH="47.100.184.77:2181,47.100.184.77:2182,47.100.184.77:2183";

    public PubSubClient() {
        RetryPolicy retryPolicy =new RetryNTimes(3,5000);
        client = CuratorFrameworkFactory.builder()
                .retryPolicy(retryPolicy)
                .connectString(ZK_SERVER_PATH)
                .sessionTimeoutMs(5000)
                .namespace("admin")
                .build();
        client.start();
    }
    public void closeZkClient(){
        if (client!=null){
            client.close();
        }
    }
    public final static String CONFIG_NODE_PATH="/zsk/zsk1";
    public final static String SUB_PATH="/redis-config";
    public static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        PubSubClient pubSubClient = new PubSubClient();
        System.out.println(pubSubClient.client.getState());
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(pubSubClient.client,CONFIG_NODE_PATH,true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    String configNodePath = event.getData().getPath();
                    System.out.println("监听到配置发生变化，节点路径为:" + configNodePath);

                    String jsonConfig = new String(event.getData().getData());
                    System.out.println("节点" + CONFIG_NODE_PATH + "的数据为: " + jsonConfig);
                    RedisConfig redisConfig = null;
                    if (StringUtils.isNotBlank(jsonConfig)) {
                        redisConfig = JsonUtils.jsonToPojo(jsonConfig, RedisConfig.class);
                    }
                    if (redisConfig != null) {
                        String url = redisConfig.getUrl();
                        String type = redisConfig.getType();
                        String remark = redisConfig.getRemark();
                        if (type.equals("add")) {
                            System.out.println("监听到新增的配置，准备下载...");
                            // ... 连接ftp服务器，根据url找到相应的配置
                            Thread.sleep(500);
                            System.out.println("开始下载新的配置文件，下载路径为<" + url + ">");
                            // ... 下载配置到你指定的目录
                            Thread.sleep(1000);
                            System.out.println("下载成功，已经添加到项目中");
                            // ... 拷贝文件到项目目录
                        } else if (type.equals("update")) {
                            System.out.println("监听到更新的配置，准备下载...");
                            // ... 连接ftp服务器，根据url找到相应的配置
                            Thread.sleep(500);
                            System.out.println("开始下载配置文件，下载路径为<" + url + ">");
                            // ... 下载配置到你指定的目录
                            Thread.sleep(1000);
                            System.out.println("下载成功...");
                            System.out.println("删除项目中原配置文件...");
                            Thread.sleep(100);
                            // ... 删除原文件
                            System.out.println("拷贝配置文件到项目目录...");
                            // ... 拷贝文件到项目目录
                        } else if (type.equals("delete")) {
                            System.out.println("监听到需要删除配置");
                            System.out.println("删除项目中原配置文件...");
                        }
                    }
                }
            }
        });
        countDownLatch.await();
        pubSubClient.closeZkClient();
    }

}
