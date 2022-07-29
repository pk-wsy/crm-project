package com.wsy.crm.workbench.service;

import com.wsy.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationService {

    /**
     * 批量保存线索与一些市场活动之间的关系
     * @param list
     * @return
     */
    int saveCreateClueActivityRelationByList(List<ClueActivityRelation> list);

    /**
     * 根据clueid和activityid删除线索与市场活动的关联关系
     * @param relation
     * @return
     */
    int deleteClueActivityRelationByClueIdActivityId(ClueActivityRelation relation);
}
