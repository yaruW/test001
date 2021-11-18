import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

public class JedisDemo1 {

    public static  void main(String[] args){

        Jedis jedis=new Jedis("172.17.223.248",6379);
        String value=jedis.ping();
        System.out.println(value);
        jedis.select(0);
        jedis.set("name","Lily");
        jedis.get("name");
        jedis.ttl("name");
        jedis.mset("key1","v1","key2","v2");
        List<String> mget=jedis.mget("key1","key2");
        System.out.println(mget);
        Set<String> keys = jedis.keys("*");
        for(String key: keys){
            System.out.println(key);
        }

        //操作list
        jedis.lpush("k1","v1","v2","v3");
        List<String> values = jedis.lrange("k1",0,-1);
        System.out.println(values);

        //操作set
        jedis.sadd("nickname","lucy","LiLy","Jack");
        Set<String> valu = jedis.smembers("nickname");
        System.out.println(valu);

        //hash
        jedis.hset("users","age","20");
        String hs=jedis.hget("users","age");
        System.out.println(hs);

        //zset
        jedis.zadd("city",100d,"shanghai");
        Set<String> city=jedis.zrange("city",0,-1);
        System.out.println(city);
    }


}
