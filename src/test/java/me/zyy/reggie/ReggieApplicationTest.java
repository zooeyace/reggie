package me.zyy.reggie;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReggieApplicationTest {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate template;

    @Test
    public void testString() {
        StringRedisSerializer redisSerializer = new StringRedisSerializer();
        template.setKeySerializer(redisSerializer);
        template.setValueSerializer(redisSerializer);
        template.opsForValue().set("city", "jiangsu");
    }

    @Test
    public void testHash() {
        HashOperations hashOperations = template.opsForHash();
        hashOperations.put("001", "name", "zhangsan");
        hashOperations.put("001", "sex", "male");

        String name = (String) hashOperations.get("001", "name");

        Set keys = hashOperations.keys("001"); // 001的所有key
        List values = hashOperations.values("001"); // 001的所有value

    }

    @Test
    public void testList() {
        ListOperations listOperations = template.opsForList();
        listOperations.leftPushAll("myList", "1", "2", "3"); // 左插
        List<String> myList = listOperations.range("myList", 0, -1); // 遍历 所有
        for (String s : myList) {
            System.out.println(s);
        }

        int size = listOperations.size("myList").intValue(); // size
        while (size > 0) {
            String e = (String) listOperations.rightPop("myList"); // lpop/rpop 能够删除并获取被删除元素
            System.out.println(e);
            size--;
        }
    }

    @Test
    public void testSet() {
        SetOperations setOperations = template.opsForSet();
        setOperations.add("mySet", "1", "2", "3", "1"); // "1" "2" "3"
        Set<String> mySet = setOperations.members("mySet");
        for (String s : mySet) {
            System.out.println(s);
        }

        setOperations.remove("mySet", "1", "3"); // left 2 only

    }

    @Test
    public void testZSet() {
        ZSetOperations zSetOperations = template.opsForZSet();
        zSetOperations.add("myZSet", "a", 20.0);
        zSetOperations.add("myZSet", "b", 12.0);
        zSetOperations.add("myZSet", "c", 18.0);
        zSetOperations.add("myZSet", "a", 15.0);
        System.out.println(zSetOperations.size("myZSet").intValue()); // 3

        Set<String> zSet = zSetOperations.range("myZSet", 0, -1);

        for (String s : zSet) {
            System.out.println(s); // b a c
        }

        // 修改分数
        zSetOperations.incrementScore("myZSet", "a", 10.0); // a plus 10 scores

        // remove
        zSetOperations.remove("myZSet", "a", "b"); // left c only
    }
}
