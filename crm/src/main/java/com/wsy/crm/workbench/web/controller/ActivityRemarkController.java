package com.wsy.crm.workbench.web.controller;

import com.wsy.crm.commons.constant.Constant;
import com.wsy.crm.commons.domain.ReturnObject;
import com.wsy.crm.commons.utils.DateUtils;
import com.wsy.crm.commons.utils.UUIDUtils;
import com.wsy.crm.settings.domain.User;
import com.wsy.crm.workbench.domain.ActivityRemark;
import com.wsy.crm.workbench.service.ActivityRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;

//其实按照原则这个类中的方法应该写在ActivityController类中，但是那个类的代码太多（将近500行），为了便于维护，把与市场活动备注相关的代码放在一个新的Controller中
@Controller
public class ActivityRemarkController {

    @Autowired
    private ActivityRemarkService activityRemarkService;

    /**
     * 处理保存新的市场活动备注的请求
     * @param remark 封装了前台发来的备注信息noteContent，以及市场活动的id
     * @return
     */
    @RequestMapping("/workbench/activity/saveCreateActivityRemark.do")
    @ResponseBody
    public Object saveCreateActivityRemark(ActivityRemark remark, HttpSession session){
        //获取当前用户信息
        User user = (User)session.getAttribute(Constant.SESSION_USER);
        //在前台发来的实体类参数的基础上，继续封装参数
        remark.setId(UUIDUtils.getUUID());
        remark.setCreateTime(DateUtils.formatDateTime(new Date()));
        remark.setCreateBy(user.getId());
        remark.setEditFlag(Constant.REMARK_EDIT_FLAG_NO_EDITED);//封装在常量中，便于维护
        //调用service层方法，保存创建的市场活动备注
        ReturnObject returnObject = new ReturnObject();
        try {
            int ret = activityRemarkService.saveCreateActivityRemark(remark);
            if(ret > 0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setRetData(remark);
            }else{
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/deleteActivityRemarkById.do")
    @ResponseBody
    public Object deleteActivityRemarkById(String id){
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，删除备注
            int ret = activityRemarkService.deleteActivityRemarkById(id);
            if(ret > 0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            }else{
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/saveEditActivityRemark.do")
    @ResponseBody
    public Object saveEditActivityRemark(ActivityRemark remark,HttpSession session){
        //继续封装前端传来的参数
        User user = (User)session.getAttribute(Constant.SESSION_USER);
        remark.setEditTime(DateUtils.formatDateTime(new Date()));
        remark.setEditBy(user.getId());
        remark.setEditFlag(Constant.REMARK_EDIT_FLAG_YES_EDITED);
        ReturnObject returnObject = new ReturnObject();
        //调用service层方法，保存修改的市场活动备注信息
        try {
            int ret = activityRemarkService.saveEditActivityRemark(remark);
            if(ret > 0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setRetData(remark);
            }else{
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }
}
