package com.otterdram.otterdram.common.utils;

import com.otterdram.otterdram.common.enums.common.LanguageCode;

import java.util.Map;

public class LanguageUtils {
    public static String getDisplayName(String name, Map<LanguageCode, String> translations, LanguageCode lang) {
        if (lang == LanguageCode.EN || translations == null) {
            return name;
        }
        return translations.getOrDefault(lang, name);
    }
}