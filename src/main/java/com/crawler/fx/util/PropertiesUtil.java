package com.crawler.fx.util;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class PropertiesUtil {

    private final static Properties PROPERTIES = new Properties();

    public static void putAll(Properties properties) {
        if (properties == null) {
            return;
        }
        PROPERTIES.putAll(properties);
    }

    public static String getProperty(String key) {
        Assert.notNull(key, "KEY不能为空");

        return PROPERTIES.getProperty(key);
    }

    public static String getProperty(String key, String defValue) {
        Assert.notNull(key, "KEY不能为空");

        String property = PROPERTIES.getProperty(key);
        if (StringUtils.isEmpty(property)) {
            return defValue;
        } else {
            return property;
        }
    }

    public static List<String> find(String key) {
        Assert.notNull(key, "KEY不能为空");

        List<String> result = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : PROPERTIES.entrySet()) {
            if (entry.getKey().toString().startsWith(key)) {
                result.add(entry.getValue().toString());
            }
        }
        return result.isEmpty() ? null : result;
    }
}
