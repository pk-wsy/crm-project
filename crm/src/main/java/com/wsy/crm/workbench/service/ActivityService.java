package com.wsy.crm.workbench.service;


import com.wsy.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    /**
     * service层 插入Activity对象
     * @param activity
     * @return
     */
    int saveCreateActivity(Activity activity);

    /**
     * service层中根据查询条件和分页要求查询相关的市场活动记录
     * @param map 封装了查询条件以及分页要求的map集合
     * @return 返回符合要求的记录集合
     */
    List<Activity> queryActivityByConditionForPage(Map<String, Object> map);

    /**
     * service层中根据查询条件查询符合条件的总记录数
     * @param map 封装了查询条件
     * @return 返回符合条件的总记录数
     */
    int queryCountOfActivityByCondition(Map<String,Object> map);

    /**
     * 根据id批量删除市场活动
     * @param ids
     * @return
     */
    int deleteActivityByIds(String[] ids);

    /**
     * 根据id来查询某一个市场活动，用于模态窗口显示要修改的市场活动原始信息
     * @param id
     * @return
     */
    Activity queryActivityById(String id);

    /**
     * 更新市场活动信息
     * @param activity
     * @return
     */
    int saveEditActivity(Activity activity);

    /**
     * 查询所有市场活动，用于excel批量导出
     * @return
     */
    List<Activity> queryAllActivitys();

    /**
     * 批量保存市场活动
     * @param activityList
     * @return
     */
    int saveCreateActivityByList(List<Activity> activityList);

    /**
     * 根据市场活动id某一查询市场活动的详细信息
     * @param id
     * @return
     */
    Activity queryActivityForDetailById(String id);

    /**
     * 根据线索Id查询该线索对应的多个市场活动,显示在detail页面
     * @param clueId
     * @return
     */
    List<Activity> queryActivityForDetailByClueId(String clueId);

    /**
     * 根据市场活动名称模糊查询市场活动，并排除已经和此线索关联过的市场活动，用于后续和市场活动关联的业务
     * @param map
     * @return
     */
    List<Activity> queryActivityForDetailByNameClueId(Map<String,Object> map);

    /**
     * 根据市场活动的一些id，查询一些市场活动
     * @param ids
     * @return
     */
    List<Activity> queryActivityForDetailByIds(String[] ids);

    /**
     * 根据市场活动名称模糊查询市场活动，并且查询的一定是满足和该线索（clueId）关联过的市场活动
     * @param map
     * @return
     */
    List<Activity> queryActivityForConvertByNameClueId(Map<String,Object> map);


}
