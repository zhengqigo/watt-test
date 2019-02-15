package org.fuelteam.watt.test.http;

import java.util.HashMap;
import java.util.Map;

public enum ReuterKeyEnum {
    
    TRAIL("TRAIL"), SELECT("SELECT");
    
    private String value;
    
    private static Map<String, ReuterKeyEnum> map = new HashMap<String, ReuterKeyEnum>();

    static {
        for (ReuterKeyEnum reuterKeyEnum : ReuterKeyEnum.values()) {
            map.put(reuterKeyEnum.getValue(), reuterKeyEnum);
        }
    }

    public static ReuterKeyEnum of(String value) {
        return map.get(value);
    }

    private ReuterKeyEnum(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
