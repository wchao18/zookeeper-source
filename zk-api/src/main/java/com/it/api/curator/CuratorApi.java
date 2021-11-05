package com.it.api.curator;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class CuratorApi {

    private static final String ZK_HOST = "192.168.16.170:12181,192.168.16.171:12181,192.168.16.173:12181";

    public static void main(String[] args) {
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.builder().connectString(ZK_HOST).sessionTimeoutMs(5000).retryPolicy(
                        new ExponentialBackoffRetry(1000, 3)
                ).build();
        curatorFramework.start();
        System.out.println(curatorFramework);

        try {
            String result = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).
                    forPath("/curator/curator1/curator21", "123".getBytes());
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
