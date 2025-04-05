package com.happiness.budtree.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.data.host}")
    private String host;

    @Value("${spring.redis.data.port}")
    private int port;

    @Value("${spring.redis.data.database}")
    private int database;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setDatabase(database); //기본적으로 DB 0 사용
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisConnectionFactory redisBlackListConnectionFactory() { // 블랙리스트용 Redis
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setDatabase(1);
        return new LettuceConnectionFactory(config);
    }

    @Bean //AccessToken, RefreshToken 저장
    public StringRedisTemplate redisTemplate() {
        return new StringRedisTemplate(redisConnectionFactory());
    }

    @Bean //AccessToken BlackList
    public StringRedisTemplate redisBlackListTemplate() {
        return new StringRedisTemplate(redisBlackListConnectionFactory());
    }

}
