package com.example.finance_api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * キャッシュ設定
 * <p>
 * TTL設定方針:
 * - 過去月のデータ: 無期限（dbt実行後に変わらない）
 * - 当月のデータ: 1時間（dbt-run-jobの実行頻度に合わせる）
 * <p>
 * 現在の実装では、Caffeineの制約により一律1時間のTTLとしている。
 * 過去月データの無期限キャッシュは、将来的にCacheManagerを分離して実装予定。
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("expenses", "expenseSummary");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(100));
        return manager;
    }
}
