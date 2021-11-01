package com.it.api.nativeapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * 原生客户端问题
 *
 * 会话连接是异步的
 * Watch需要重新注册
 * Session重连机制
 * 开发复杂较高
 */
public class CreateSession implements Watcher {

    private static final String ZK_HOST = "192.168.16.170:12181";

    private static ZooKeeper zookeeper;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception {

        zookeeper = new ZooKeeper(ZK_HOST, 5000, new CreateSession());
        //存在的问题,连接是异步的,只有确定连接才进行操作,所以加入countDownLatch
        countDownLatch.await();

        System.out.println(zookeeper);

        //创建一个节点
        //zooKeeper.create("/0628","test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //获取数据
       /* Stat stat = new Stat();
        byte[] data = zooKeeper.getData("/0628",true,stat);
        System.out.println(stat);
        System.out.println(new String(data,"utf-8"));*/

        //修改数据,-1不做版本控制
        //PS:获取监听watch,wath只有一次
        zookeeper.getData("/0628", true, stat);
        zookeeper.setData("/0628", UUID.randomUUID().toString().getBytes(), -1);

        //删除节点
        //zooKeeper.delete("/0928",-1);

        //获取当前节点下面的字节点
      /*  List<String> children = zooKeeper.getChildren("/aa", true);
        children.forEach(data -> System.out.println(data));*/
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        //如果当前的连接状态是连接成功的，那么通过计数器去控制
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            countDownLatch.countDown();
            System.out.println(watchedEvent.getState() + "-->" + watchedEvent.getType());
        } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
            try {
                System.out.println("数据变更触发路径：" + watchedEvent.getPath() + "->改变后的值：" +
                        new String(zookeeper.getData(watchedEvent.getPath(), true, stat)));
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {//子节点的数据变化会触发
            try {
                System.out.println("子节点数据变更路径：" + watchedEvent.getPath() + "->节点的值：" +
                        zookeeper.getData(watchedEvent.getPath(), true, stat));
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (watchedEvent.getType() == Event.EventType.NodeCreated) {//创建子节点的时候会触发
            try {
                System.out.println("节点创建路径：" + watchedEvent.getPath() + "->节点的值：" +
                        zookeeper.getData(watchedEvent.getPath(), true, stat));
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (watchedEvent.getType() == Event.EventType.NodeDeleted) {//子节点删除会触发
            System.out.println("节点删除路径：" + watchedEvent.getPath());
        }
    }
}
