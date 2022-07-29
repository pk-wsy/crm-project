package com.wsy.crm.settings.service;

import com.wsy.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueService {
    /**
     * 根据typecode查询数据字典值
     * @param typeCode
     * @return
     */
    List<DicValue> queryDicValueByTypeCode(String typeCode);
}
