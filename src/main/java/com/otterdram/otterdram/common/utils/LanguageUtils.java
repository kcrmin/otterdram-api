package com.otterdram.otterdram.common.utils;

import com.otterdram.otterdram.common.enums.common.LanguageCode;

import java.util.Map;

public class LanguageUtils {
    public static String getDisplayName(Map<LanguageCode, String> translations, LanguageCode targetLang, String defaultValue) {
        if (targetLang == LanguageCode.EN || translations == null) {
            return defaultValue;
        }
        return translations.getOrDefault(targetLang, defaultValue);
    }
}