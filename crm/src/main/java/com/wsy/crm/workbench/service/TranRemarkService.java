package com.wsy.crm.workbench.service;

import com.wsy.crm.workbench.domain.TranRemark;

import java.util.List;

public interface TranRemarkService {

    /**
     * 根据交易的id查询该交易下的所有备注明细信息
     * @param tranId
     * @return
     */
    List<TranRemark> queryTranRemarkForDetailByTranId(String tranId);
}
