package com.example.finance_api.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CategoryResolver {

    private final ResourceLoader resourceLoader;
    private Map<String, String> categoryMap = new LinkedHashMap<>();

    @Autowired
    public CategoryResolver(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = resourceLoader.getResource("classpath:store_categories.yml").getInputStream()) {
            Map<String, Object> yamlData = yaml.load(inputStream);
            @SuppressWarnings("unchecked")
            Map<String, String> categories = (Map<String, String>) yamlData.get("categories");
            if (categories != null) {
                this.categoryMap = categories;
            }
        }
    }

    /**
     * 店舗名からカテゴリと正規化名を解決する
     *
     * @param storeName 店舗名
     * @return 解決結果（カテゴリ、正規化名）
     */
    public ResolveResult resolve(String storeName) {
        if (storeName == null || storeName.isEmpty()) {
            return new ResolveResult("未分類", null);
        }

        for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
            String key = entry.getKey();
            if (storeName.startsWith(key)) {
                return new ResolveResult(entry.getValue(), key);
            }
        }

        return new ResolveResult("未分類", null);
    }

    /**
     * 解決結果を保持するレコード
     */
    public record ResolveResult(String category, String normalizedName) {
    }
}
