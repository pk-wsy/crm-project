package com.wsy.crm.workbench.service.impl;

import com.wsy.crm.workbench.domain.TranHistory;
import com.wsy.crm.workbench.mapper.TranHistoryMapper;
import com.wsy.crm.workbench.service.TranHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tranHistoryService")
public class TranHistoryServiceImpl implements TranHistoryService {
    @Autowired
    private TranHistoryMapper tranHistoryMapper;

    @Override
    public List<TranHistory> queryTranHistoryForDetailByTranId(String tranId) {
        return tranHistoryMapper.selectTranHistoryForDetailByTranId(tranId);
    }
}
