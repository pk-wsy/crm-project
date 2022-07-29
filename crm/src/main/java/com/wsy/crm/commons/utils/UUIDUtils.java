package com.wsy.crm.commons.utils;

import java.util.UUID;

public class UUIDUtils {
    /**
     * 生成32位uuid
     * @return
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
