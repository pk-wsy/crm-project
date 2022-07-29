package com.wsy.crm.workbench.service.impl;

import com.wsy.crm.workbench.domain.Activity;
import com.wsy.crm.workbench.mapper.ActivityMapper;
import com.wsy.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("activityService")
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    public ActivityMapper activityMapper;

    @Override
    public int saveCreateActivity(Activity activity) {
        return activityMapper.insertActivity(activity);
    }


    /*Service业务层的方法中，如果要进行多个读操作的过程（调用Mapper接口中多个select的sql语句方法）
        在service层最好将这些调用查询sql的方法分开来写，一个service方法对应一个查询的业务，
        这是因为读操作不关心成功与否的问题，不涉及事务，将他们分开更灵活，今后的业务有其他的组合时，可以直接在
        controller层依次调用这些方法
        但是，对于多个写操作的过程，事务的因素至关重要，必须通过事务来保证多个写操作要么同时成功，要么同时失败
        因此，service层方法如果要调用Mapper接口中多个写操作的方法时，在service层必须将这些操作统一放在一个方法中，
        并通过spring的AOP来实现对于事务的管理
     */

    @Override
    public List<Activity> queryActivityByConditionForPage(Map<String, Object> map) {
        return activityMapper.selectActivityByConditionForPage(map);
    }

    @Override
    public int queryCountOfActivityByCondition(Map<String, Object> map) {
        return activityMapper.selectCountOfActivityByCondition(map);
    }

    @Override
    public int deleteActivityByIds(String[] ids) {
        return activityMapper.deleteActivityByIds(ids);
    }

    @Override
    public Activity queryActivityById(String id) {
        return activityMapper.selectActivityById(id);
    }

    @Override
    public int saveEditActivity(Activity activity) {
        return activityMapper.updateActivity(activity);
    }

    @Override
    public List<Activity> queryAllActivitys() {
        return activityMapper.selectAllActivitys();
    }

    @Override
    public int saveCreateActivityByList(List<Activity> activityList) {
        return activityMapper.insertActivityByList(activityList);
    }

    @Override
    public Activity queryActivityForDetailById(String id) {
        return activityMapper.selectActivityForDetailById(id);
    }

    @Override
    public List<Activity> queryActivityForDetailByClueId(String clueId) {
        return activityMapper.selectActivityForDetailByClueId(clueId);
    }

    @Override
    public List<Activity> queryActivityForDetailByNameClueId(Map<String, Object> map) {
        return activityMapper.selectActivityForDetailByNameClueId(map);
    }

    @Override
    public List<Activity> queryActivityForDetailByIds(String[] ids) {
        return activityMapper.selectActivityForDetailByIds(ids);
    }

    @Override
    public List<Activity> queryActivityForConvertByNameClueId(Map<String, Object> map) {
        return activityMapper.selectActivityForConvertByNameClueId(map);
    }
}
