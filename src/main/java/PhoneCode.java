import java.util.Random;
import redis.clients.jedis.Jedis;

/*
* 6位验证码
* 过期时间2分钟
* 校验验证码与输入是否一致
* 每个手机号码每天只能发送只三次验证码  incr 每次发送之后+1，大于2 提交不能发送
* */
public class PhoneCode {

    public static void main(String[] args){
        //verifyCode("15670120885");
        getRedisCode("15670120885","63596611");

    }

    //生成随机6位验证码
    public static String getCode(){

        Random random= new Random();
        String code="";
        for (int i=0;i<6;i++){
            //生成10以内的值
            int rand=random.nextInt(10);
            code +=rand;
        }
        return code;
    }


    //每个手机每天只能发送三次，验证码放入到redis中，设置过期时间
    public static void verifyCode(String phone){
        //链接redis
        Jedis jedis=new Jedis("172.17.223.248",6379);
        jedis.select(0);
        //拼接key
            //1.手机发送次数key
        String countKey="VerifyCode" + phone +":count";
            //2.验证码key
        String codeKey="VerifyCode" + phone +":code";
        //每次手每天只能发送三次
        String count=jedis.get(countKey);
        if(count==null){
            jedis.setex(countKey,24*60*60,"1");
        }else if(Integer.parseInt(count)<=2){
            jedis.incr(countKey);
        }else if (Integer.parseInt(count)>2){
            System.out.println("今天发送已超过三次，不可再发送！！！");
            jedis.close();
        }
        //发送验证码放入到redis里面
        String vcode = getCode();
        jedis.setex(codeKey,120,vcode);
        jedis.close();
    }

    //验证码校验
    public  static  void  getRedisCode(String phone,String code){
        Jedis jedis = new Jedis("172.17.223.248",6379);
        String codeKey = "VerifyCode" + phone +":code";
        String redisCode = jedis.get(codeKey);
        if(redisCode.equals(code)){
            System.out.println("成功");
        }else {
            System.out.println("验证码输入错误！！！");
        }
        jedis.close();
    }
}
