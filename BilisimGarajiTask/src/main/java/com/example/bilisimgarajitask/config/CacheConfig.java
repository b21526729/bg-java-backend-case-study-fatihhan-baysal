package com.example.bilisimgarajitask.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory cf, ObjectMapper om) {
        var valueSer = RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer(om));

        var defaults = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(valueSer)
                .entryTtl(Duration.ofMinutes(5))  // default TTL: 5 dk
                .disableCachingNullValues();

        return RedisCacheManager.builder(cf)
                .cacheDefaults(defaults)
                .withInitialCacheConfigurations(Map.of(
                        "brand:byId",      defaults.entryTtl(Duration.ofMinutes(10)),
                        "org:listByBrand", defaults.entryTtl(Duration.ofMinutes(5)),
                        "cls:listByOrg",   defaults.entryTtl(Duration.ofMinutes(5))
                ))
                .build();
    }
}
