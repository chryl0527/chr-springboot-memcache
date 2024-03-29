package com.chryl.controller;

import com.schooner.MemCached.MemcachedItem;
import com.whalin.MemCached.MemCachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * 总结：memcache通过set方法把值存入都memcache缓存中；通过get方法把值取出来；通过设置过期时间，使其失效。
 * <p>
 * Created By Chr on 2019/7/2.
 */
@RestController
public class MemcacheController {

    @Autowired
    private MemCachedClient memCachedClient;

    /**
     * memcache缓存
     */
    @RequestMapping("/memcache")
    public String memcache() throws InterruptedException {
        // 放入缓存
        boolean flag = memCachedClient.set("mem", "name");
        // 取出缓存
        Object value = memCachedClient.get("mem");
        System.out.println(value);
        // 3s后过期
        memCachedClient.set("num", "666", new Date(3000));
        /*memCachedClient.set("num", "666", new Date(System.currentTimeMillis()+3000));与上面的区别是当设置了这个时间点
        之后，它会以服务端的时间为准，也就是说如果本地客户端的时间跟服务端的时间有差值，这个值就会出现问题。
        例：如果本地时间是20:00:00,服务端时间为20:00:10，那么实际上会是40秒之后这个缓存key失效*/
        Object key = memCachedClient.get("num");
        System.out.println(key);
        //多线程睡眠3s
        Thread.sleep(3000);
        key = memCachedClient.get("num");
        System.out.println(value);
        System.out.println(key);
        return "success";
    }

    //set
    @GetMapping("/show1")
    public Object show1() {
        boolean set = memCachedClient.set("name", "chryl");
        if (set) {
            return memCachedClient.get("name");
        }
        return null;
    }

    //get
    @GetMapping("/show2")
    public Object show2() {
        boolean set = memCachedClient.set("name", "chryl22", new Date(20 * 1000));
        if (set) {
            return memCachedClient.get("name");
        }
        return null;
    }

    /**
     * 1、memcache::add 方法：add方法用于向memcache服务器添加一个要缓存的数据。
     * <p>
     * 注意：如果memcache服务器中已经存在要存储的key，此时add方法调用失败。
     * <p>
     * 2、memcache::set 方法：set方法用于设置一个指定key的缓存内容，set方法是add方法和replace方法的集合体。
     * <p>
     * 注意：
     * <p>
     * 1）、如果要设置的key不存在时，则set方法与add方法的效果一致；
     * <p>
     * 2）、如果要设置的key已经存在时，则set方法与replace方法效果一样。
     * <p>
     * 3、 mmecache::replace方法： replace方法用于替换一个指定key的缓存内容，如果key不存在则返回false
     *
     * @return
     */
    //add
    @GetMapping("/show3")
    public Object show3() {
        //该key有value就add失败-返回false,没有就add成功-返回true
        boolean see = memCachedClient.add("see", "0123");
        boolean name = memCachedClient.add("name", "cht");
        if (see || name) {
            return memCachedClient.get("see") + ":" + name;
        }
        return null;
    }

    //append
    //在原来的 key的value之后追加
    @GetMapping("/show4")
    public Object show4() {
        boolean see = memCachedClient.append("see", "-ess");
        if (see) {
            return memCachedClient.get("see");
        }
        return null;
    }

    @GetMapping("/show5")
    public Object show5() {
//        boolean see = memCachedClient.cas("see", "bingo", new Date(20 * 1000), 1000);
        boolean set = memCachedClient.set("see", "new see");

        if (set) {
            return memCachedClient.get("see");
        }
        return null;
    }

    //cas
    @GetMapping("/show6")
    public Object show6() {
        memCachedClient.set("sbf", "249");
//        通过gets方法获取CAS token(令牌)
        MemcachedItem seeMI = memCachedClient.gets("sbf");
        System.out.println(seeMI);
        //cas操作
        memCachedClient.cas("sbf", "24", new Date(10 * 1000), seeMI.getCasUnique());

        return memCachedClient.get("sbf");
    }

    //incr-decr
    @GetMapping("/show7")
    public void show7() {
        boolean set = memCachedClient.set("kb", "24");
        if (set) {
            long kb = memCachedClient.decr("kb", 16);
            System.out.println(kb);
            long kb1 = memCachedClient.incr("kb", 16);
            System.out.println(kb1);
        }
    }

    //prepend
    @GetMapping("/show8")
    public Object show8() {
        boolean set = memCachedClient.set("str", "my name");
        if (set) {
            boolean str = memCachedClient.prepend("str", "see ");
            if (str) {
                return memCachedClient.get("str");
            }
        }
        return null;
    }

    //replace
    @GetMapping("/show9")
    public Object show9() {
        boolean set = memCachedClient.set("sst", "nameN");
        if (set) {
            boolean replace = memCachedClient.replace("sst", "mess");
            if (replace) {
                return memCachedClient.get("sst");
            }
        }
        return null;
    }

    //delete
    @GetMapping("/show10")
    public void show10() {
        memCachedClient.delete("str");
    }

    //清空所有
    @GetMapping("/show11")
    public void show11() {
        memCachedClient.flushAll();
    }

    //add decr/incr
    @GetMapping("/show12")
    public void show12() {
        long kb = memCachedClient.addOrDecr("kb", 3);
        System.out.println(kb);
        long kb1 = memCachedClient.addOrIncr("kb", 6);
        System.out.println(kb1);
    }

    //getMulti . ...
    @GetMapping("/show13")
    public void show13() {
        //未知
        long kb = memCachedClient.getCounter("name");
        System.out.println(kb);
        //获取多个 key value
        Map<String, Object> multi = memCachedClient.getMulti(new String[]{"kb", "name"});
        System.out.println(multi);
    }

    //getMultiArray
    @GetMapping("/show14")
    public void show14() {
        //只获取 value
        Object[] multiArray = memCachedClient.getMultiArray(new String[]{"kb", "name"});
        System.out.println(multiArray);
    }

    //keyExists
    @GetMapping("/show15")
    public void show15() {
        //是否存在key
        boolean b = memCachedClient.keyExists("see");
        boolean b2 = memCachedClient.keyExists("see12");
        System.out.println(b);
        System.out.println(b2);
        //未知
        boolean useBinaryProtocol = memCachedClient.isUseBinaryProtocol();
        System.out.println(useBinaryProtocol);
    }

    //查看memcache的配置信息
    @GetMapping("/show16")
    public void show16() {
        Map<String, Map<String, String>> stats = memCachedClient.stats();
        System.out.println(stats);
    }

    //未知
    @GetMapping("/show17")
    public void show17() {
        //{}
        Map<String, Map<String, String>> statsItems = memCachedClient.statsItems();
        //slabs信息
        Map<String, Map<String, String>> statsSlabs = memCachedClient.statsSlabs();
        System.out.println(statsItems);
        System.out.println(statsSlabs);
    }
}