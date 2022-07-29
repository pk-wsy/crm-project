package com.wsy.crm.workbench.service;

import com.wsy.crm.workbench.domain.FunnelVO;
import com.wsy.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranService {

    /**
     * 保存创建交易对象
     * @param map
     */
    void saveCreateTran(Map<String,Object> map);

    /**
     * 根据id查询交易的详细信息
     * @param id
     * @return
     */
    Tran queryTranForDetailById(String id);

    /**
     * 查询交易记录中处于不同阶段的数据量
     * @return
     */
    List<FunnelVO> queryCountOfTranGroupByStage();
}
