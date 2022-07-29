package com.wsy.crm.workbench.service;

import com.wsy.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkService {

    /**
     * 根据市场活动id查询该市场活动的备注信息
     * @param activityId
     * @return
     */
    List<ActivityRemark> queryActivityRemarkForDetailByActivityId(String activityId);

    /**
     * 保存新建的市场活动备注
     * @param remark
     * @return
     */
    int saveCreateActivityRemark(ActivityRemark remark);

    /**
     * 根据id删除某一市场活动备注
     * @param id
     * @return
     */
    int deleteActivityRemarkById(String id);

    /**
     * 保存修改后的市场活动备注
     * @param remark
     * @return
     */
    int saveEditActivityRemark(ActivityRemark remark);
}
