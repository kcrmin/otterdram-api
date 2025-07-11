package com.otterdram.otterdram.common.enums;

import lombok.Getter;

@Getter
public enum LanguageCode {
    EN("en"),
    BR("br"),
    DE("de"),
    ES("es"),
    FA("fa"),
    FR("fr"),
    HR("hr"),
    IT("it"),
    JA("ja"),
    KO("ko"),
    NL("nl"),
    PL("pl"),
    PT("pt"),
    PT_BR("pt-BR"),
    RU("ru"),
    TR("tr"),
    UK("uk"),
    ZH_CN("zh-CN");

    private final String code;

    LanguageCode(String code) {
        this.code = code;
    }

    public static LanguageCode fromCode(String code) {
        for (LanguageCode lang : LanguageCode.values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Unknown language code: " + code);
    }
}
