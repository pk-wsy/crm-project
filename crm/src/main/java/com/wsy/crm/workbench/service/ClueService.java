package com.wsy.crm.workbench.service;

import com.wsy.crm.workbench.domain.Clue;

import java.util.Map;

public interface ClueService {
    /**
     * 保存创建的线索
     * @param clue
     * @return
     */
    int saveCreateClue(Clue clue);

    /**
     * 根据id查询线索的详细内容
     * @param id
     * @return
     */
    Clue queryClueForDetailById(String id);

    /**
     * 线索转换业务
     * @param map
     */
    void saveConvertClue(Map<String,Object> map);
}
