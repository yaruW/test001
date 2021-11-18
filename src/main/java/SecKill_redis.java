import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.io.IOError;
import java.io.IOException;
import java.util.List;

public class SecKill_redis {
    public static boolean doSecKill(String uid,String prodid) throws IOException{
        //1.uid 和 prodid 非空判断
        if(uid ==null || prodid==null){
            return false;
        }
        //2.链接redis
        Jedis jedis=new Jedis("172.17.223.248",6379);
        jedis.select(0);
        //3 拼接key
        //3.1 库存key
        String kcKey="sk"+prodid+":qt";
        //3.2 秒杀成功用户key
        String userKey="sk"+prodid+":user";

        //监视库存
        jedis.watch(kcKey);
        //4获取库存，如果库存为null,秒杀还没开始
        String kc = jedis.get(kcKey);
        if(kc==null){
            System.out.println("秒杀还没开始！！！");
            jedis.close();
            return false;
        }

        //5.判断用户是否重复秒杀
        if(jedis.sismember(userKey,uid)){
            System.out.println("已经秒杀成功，不能重复秒杀！！！");
            jedis.close();
            return false;
        }
        //6 判断如果商品数量 库存数量小于1 秒杀结束
        if(Integer.parseInt(kc)<=0){
            System.out.println("秒杀over！！！");
            jedis.close();
            return false;
        }
        //7.秒杀过程
        Transaction multi = jedis.multi();
        //组队操作
        multi.decr(kcKey);
        multi.sadd(userKey,uid);
        List<Object> results = multi.exec();
        if(results==null || results.size()==0){
            System.out.println("秒杀失败了....");
            jedis.close();
        }

//        //7.1 库存-1
//        jedis.decr(kcKey);
//        //7.2 把秒杀用户添加到清单里
//        jedis.sadd(userKey,uid);
//        jedis.close();
        return true;
    }
}
