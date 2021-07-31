package com.example.achatroom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;


@Configuration
public class RedisConnectionPoolConfig {

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(){
        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration(),
                LettucePoolingClientConfiguration.builder().build()
        );
    }

    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(){
        return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory());
    }
}
