package com.wsy.crm.workbench.service;

import com.wsy.crm.workbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkService {

    /**
     * 根据线索id查询该线索下所有的线索备注
     * @param clueId
     * @return
     */
    List<ClueRemark> queryClueRemarkForDetailByClueId(String clueId);
}
