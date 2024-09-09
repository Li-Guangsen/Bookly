package com.lgs.dubbo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class DubboServiceApplicationTests {
	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	@Test
	void contextLoads() {
	}
	@Test
	@DisplayName("测试使用默认的JDK序列化器")
	void testRedisDefaultSerializer() {
		this.redisTemplate.opsForHash().put("001", "player", "孙小美");
		Object player = this.redisTemplate.opsForHash().get("001", "player");
		Assertions.assertEquals("孙小美", player);
	}
	@Test
	@DisplayName("测试Redis存储字符型串数据")
	void testRedisStringType1() {
		String key = "s001";
		//判断Redis中是否包含指定的key
		Boolean b = redisTemplate.hasKey(key);
		System.out.println(b);

		//存储字符串类型的数据。如果value不是字符串，也会被序列化为字符串。
		redisTemplate.opsForValue().set(key, "孙小美");

		String name = (String) redisTemplate.opsForValue().get(key);
		Assertions.assertEquals("孙小美", name);
	}


}
