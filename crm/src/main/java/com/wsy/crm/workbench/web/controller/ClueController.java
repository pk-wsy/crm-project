package com.wsy.crm.workbench.web.controller;

import com.wsy.crm.commons.constant.Constant;
import com.wsy.crm.commons.domain.ReturnObject;
import com.wsy.crm.commons.utils.DateUtils;
import com.wsy.crm.commons.utils.UUIDUtils;
import com.wsy.crm.settings.domain.DicValue;
import com.wsy.crm.settings.domain.User;
import com.wsy.crm.settings.service.DicValueService;
import com.wsy.crm.settings.service.UserService;
import com.wsy.crm.workbench.domain.Activity;
import com.wsy.crm.workbench.domain.Clue;
import com.wsy.crm.workbench.domain.ClueActivityRelation;
import com.wsy.crm.workbench.domain.ClueRemark;
import com.wsy.crm.workbench.service.ActivityService;
import com.wsy.crm.workbench.service.ClueActivityRelationService;
import com.wsy.crm.workbench.service.ClueRemarkService;
import com.wsy.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class ClueController {
    @Autowired
    private UserService userService;
    @Autowired
    private DicValueService dicValueService;
    @Autowired
    private ClueService clueService;
    @Autowired
    private ClueRemarkService clueRemarkService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ClueActivityRelationService clueActivityRelationService;
    /**
     * 查询相关数据字典值，得到动态数据，跳转至线索的主页面
     * @return
     */
    @RequestMapping("/workbench/clue/index.do")
    public String index(HttpServletRequest request){
        //调用service层方法，查询动态数据
        //查询用户的内容
        List<User> userList = userService.queryAllUsers();
        //查询称呼的数据字典值
        List<DicValue> appellationList = dicValueService.queryDicValueByTypeCode("appellation");
        //查询线索状态的数据字典值
        List<DicValue> clueStateList = dicValueService.queryDicValueByTypeCode("clueState");
        //查询线索来源的数据字典值
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        //把数据保存到作用域中
        request.setAttribute("userList",userList);
        request.setAttribute("appellationList",appellationList);
        request.setAttribute("clueStateList",clueStateList);
        request.setAttribute("sourceList",sourceList);
        //跳转至index.jsp页面
        return "workbench/clue/index";
    }

    /**
     * 保存创建的线索
     * @param clue
     * @return
     */
    @RequestMapping("/workbench/clue/saveCreateClue.do")
    @ResponseBody
    public Object saveCreateClue(Clue clue, HttpSession session){
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        //继续封装前台传来的参数
        clue.setId(UUIDUtils.getUUID());
        clue.setCreateTime(DateUtils.formatDateTime(new Date()));
        clue.setCreateBy(user.getId());
        //调用service层方法，保存创建的线索
        ReturnObject returnObject = new ReturnObject();
        try {
            int ret = clueService.saveCreateClue(clue);
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


    @RequestMapping("/workbench/clue/detailClue.do")
    public String detailClue(String id, HttpServletRequest request){
        //调用service层方法，查询相关数据
        //查询该线索的基本信息
        Clue clue = clueService.queryClueForDetailById(id);
        //查询该线索下的所有备注信息
        List<ClueRemark> remarkList = clueRemarkService.queryClueRemarkForDetailByClueId(id);
        //查询该线索所对应的所有市场活动信息
        List<Activity> activityList = activityService.queryActivityForDetailByClueId(id);
        //保存到作用域中，用于后续页面跳转
        request.setAttribute("clue",clue);
        request.setAttribute("remarkList",remarkList);
        request.setAttribute("activityList",activityList);
        //页面跳转
        return "workbench/clue/detail";
    }

    /**
     * 根据市场活动信息模糊查询相关市场活动，并根据线索的id来排除已经关联的市场活动
     * @param activityName
     * @param clueId
     * @return
     */
    @RequestMapping("/workbench/clue/queryActivityForDetailByNameClueId.do")
    @ResponseBody
    public Object queryActivityForDetailByNameClueId(String activityName, String clueId){
        //封装两个参数到map集合（方便一点，不封装直接将他们传到后面也可以）
        Map<String,Object> map = new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);
        //调用service方法
        List<Activity> activityList = activityService.queryActivityForDetailByNameClueId(map);
        //根据查询结果，返回响应信息
        return activityList;
    }

    /**
     * 保存线索和市场活动之间关系
     * @param activityId
     * @param clueId
     * @return
     */
    @RequestMapping("/workbench/clue/saveBund.do")
    @ResponseBody
    public Object saveBund(String[] activityId, String clueId){
        //封装成关联关系的实体对象
        ClueActivityRelation car = null;
        List<ClueActivityRelation> relationList = new ArrayList<>();
        for(String ai : activityId){
            car = new ClueActivityRelation();
            car.setActivityId(ai);
            car.setClueId(clueId);
            car.setId(UUIDUtils.getUUID());
            relationList.add(car);
        }
        ReturnObject returnObject = new ReturnObject();
        //调用service层，保存线索与市场活动的关联关系
        try {
            int ret = clueActivityRelationService.saveCreateClueActivityRelationByList(relationList);
            if(ret > 0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                //如果成功，则需要把与该线索具有关联关系的市场活动查出来，放入到返回的对象中，用于在前端页面刷新展示
                List<Activity> activityList = activityService.queryActivityForDetailByIds(activityId);
                returnObject.setRetData(activityList);
            }else{
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后...");
        }
        return returnObject;
    }

    /**
     * 实现解除线索与市场活动关联关系的业务
     * @param relation
     * @return
     */
    @RequestMapping("/workbench/clue/saveUnbund.do")
    @ResponseBody
    public Object saveUnbund(ClueActivityRelation relation){
        //调用service层方法，删除关系
        ReturnObject returnObject = new ReturnObject();
        try {
            int ret = clueActivityRelationService.deleteClueActivityRelationByClueIdActivityId(relation);
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

    /**
     * 将该线索的内容保存到作用域中，跳转至转换页面，然后用EL表达式取数据渲染这个页面
     * 同时也查询处阶段stage的数据字典值，用于显示在交易阶段的列表中
     * @param id
     * @return
     */
    @RequestMapping("/workbench/clue/toConvert.do")
    public String toConvert(String id,HttpServletRequest request){
        //调用service方法，查询线索的详细信息以及阶段的数据字典值
        Clue clue = clueService.queryClueForDetailById(id);
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        //把数据保存到request中
        request.setAttribute("clue",clue);
        request.setAttribute("stageList",stageList);
        //请求转发
        return "workbench/clue/convert";
    }

    /**
     * 根据市场活动信息模糊查询相关市场活动，并且需要同时满足与线索的id已经关联，用于在convert页面的市场活动搜索框内显示相关信息
     * @param activityName
     * @param clueId
     * @return
     */
    @RequestMapping("/workbench/clue/queryActivityForConvertByNameClueId.do")
    @ResponseBody
    public Object queryActivityForConvertByNameClueId(String activityName, String clueId){
        //封装参数（可以封装成map）
        Map<String,Object> map = new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);
        //调用service层方法，查询市场活动
        List<Activity> activityList = activityService.queryActivityForConvertByNameClueId(map);
        //根据查询结果返回响应信息
        return activityList;
    }


    @RequestMapping("/workbench/clue/convertClue.do")
    @ResponseBody
    public Object convertClue(HttpSession session, String clueId, String money, String name, String expectedDate, String stage, String activityId, String isCreateTran){
        //封装参数
        Map<String,Object> map = new HashMap<>();
        map.put("clueId",clueId);
        map.put("money",money);
        map.put("name",name);
        map.put("expectedDate",expectedDate);
        map.put("stage",stage);
        map.put("activityId",activityId);
        map.put("isCreateTran",isCreateTran);
        map.put(Constant.SESSION_USER,session.getAttribute(Constant.SESSION_USER));
        //调用service方法
        ReturnObject returnObject = new ReturnObject();
        try {
            clueService.saveConvertClue(map);
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }
}
