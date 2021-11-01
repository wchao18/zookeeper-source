package com.it.api.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ZkClientApi {

    private static final String ZK_HOST = "192.168.16.180:12181";

    public static ZkClient getInstance(){
        return new ZkClient(ZK_HOST,50000);
    }


    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = getInstance();
        //级联创建节点
        //zkClient.createPersistent("/test1/test2",true);


        zkClient.subscribeDataChanges("/test1", new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("datapath: " + dataPath + "," + "data: " + data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("删除节点");
            }
        });
        //修改值
        zkClient.writeData("/test1","aa");

        System.out.println(zkClient.readData("/test1").toString());

        //还没回调程序就结束了,所以加个等待时间
        TimeUnit.SECONDS.sleep(50);
    }
}
