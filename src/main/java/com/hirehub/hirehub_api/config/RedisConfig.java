package com.hirehub.hirehub_api.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    public static final String JOB_CACHE = "job";
    public static final String JOB_SEARCH_CACHE = "job_search";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {



        BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();
        // Enable default typing so Jackson 3 embeds target class metadata into JSON
        GenericJacksonJsonRedisSerializer jsonSerializer = GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(ptv)
                .build();

        RedisCacheConfiguration defaultConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(15))
                        .disableCachingNullValues()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new StringRedisSerializer()
                                )
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        jsonSerializer
                                )
                        );

        RedisCacheConfiguration jobSearchConfig =
                defaultConfiguration.entryTtl(Duration.ofMinutes(5));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfiguration)
                .withCacheConfiguration(
                        JOB_SEARCH_CACHE,
                        jobSearchConfig
                )
                .build();
    }
}