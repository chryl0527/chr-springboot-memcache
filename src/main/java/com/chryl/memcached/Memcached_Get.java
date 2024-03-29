package com.chryl.memcached;

import net.spy.memcached.MemcachedClient;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

/**
 * memcached get 命令获取存储在 key(键) 中的 value(数据值) ，如果 key 不存在，则返回空。
 * <p>
 * <p>
 * Created By Chr on 2019/7/2.
 */
public class Memcached_Get {

    public static void main(String[] args) {
        try {
            //连接本地memcached服务
            MemcachedClient mcc = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
            System.out.println("memcached服务连接成功！");

            //添加数据
            Future fu = mcc.set("context", 900, "猥琐发育别浪！");
            //输出执行set方法后的状态
            System.out.println("set status: " + fu.get());
            //使用get取出数据
            System.out.println("context value: " + mcc.get("context"));

            //关闭连接
            mcc.shutdown();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
