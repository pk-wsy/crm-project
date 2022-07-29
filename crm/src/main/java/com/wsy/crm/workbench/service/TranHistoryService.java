package com.wsy.crm.workbench.service;

import com.wsy.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryService {
    /**
     * 根据交易的id查询该交易下所有的交易历史的信息
     * @param tranId
     * @return
     */
    List<TranHistory> queryTranHistoryForDetailByTranId(String tranId);
}
