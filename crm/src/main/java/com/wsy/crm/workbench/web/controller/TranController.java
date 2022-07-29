package com.wsy.crm.workbench.web.controller;

import com.wsy.crm.commons.constant.Constant;
import com.wsy.crm.commons.domain.ReturnObject;
import com.wsy.crm.settings.domain.DicValue;
import com.wsy.crm.settings.domain.User;
import com.wsy.crm.settings.service.DicValueService;
import com.wsy.crm.settings.service.UserService;
import com.wsy.crm.workbench.domain.Tran;
import com.wsy.crm.workbench.domain.TranHistory;
import com.wsy.crm.workbench.domain.TranRemark;
import com.wsy.crm.workbench.service.CustomerService;
import com.wsy.crm.workbench.service.TranHistoryService;
import com.wsy.crm.workbench.service.TranRemarkService;
import com.wsy.crm.workbench.service.TranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Controller
public class TranController {

    @Autowired
    private DicValueService dicValueService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TranService tranService;

    @Autowired
    private TranRemarkService tranRemarkService;

    @Autowired
    private TranHistoryService tranHistoryService;
    /**
     * 查询数据字典值，保存在作用域中，用于在交易主页面显示，并跳转至交易主页面
     * @return
     */
    @RequestMapping("/workbench/transaction/index.do")
    public String index(HttpServletRequest request){
        //查询交易类型的数据字典值
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        //查询交易来源的数据字典值
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        //查询交易阶段的数据字典值
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        //保存在作用域中，用于跳转页面后显示
        request.setAttribute("transactionTypeList",transactionTypeList);
        request.setAttribute("sourceList",sourceList);
        request.setAttribute("stageList",stageList);
        //请求转发
        return "workbench/transaction/index";
    }

    /**
     * 从数据库查询各种所需的动态数据保存到作用域中，并跳转至创建交易界面
     * @param request
     * @return
     */
    @RequestMapping("/workbench/transaction/toSave.do")
    public String toSave(HttpServletRequest request){
        //调用service层方法，查询表单所需要的各种动态数据
        //查询所有用户
        List<User> userList = userService.queryAllUsers();
        //查询交易类型的数据字典值
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        //查询交易来源的数据字典值
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        //查询交易阶段的数据字典值
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        //保存在作用域中，用于跳转页面后显示
        request.setAttribute("userList",userList);
        request.setAttribute("transactionTypeList",transactionTypeList);
        request.setAttribute("sourceList",sourceList);
        request.setAttribute("stageList",stageList);
        //请求转发
        return "workbench/transaction/save";
    }

    /**
     * 根据阶段名称读取配置文件获取可能性，实现可能性的获取是可配置的
     * @param stageValue
     * @return
     */
    @RequestMapping("/workbench/transaction/getPossibilityByStage.do")
    @ResponseBody
    public Object getPossibilityByStage(String stageValue){
        //前端传来的stageValue是阶段的名字而不是id，因为配置文件是用户上传的，关于stage阶段肯定是知道名字而不知道id的，因此需要前端传名字的key，才能获取到可能性
        //解析properties配置文件
        ResourceBundle bundle = ResourceBundle.getBundle("possibility");//在resource资源路径下直接写名即可，而且不需要加后缀
        String possibility = bundle.getString(stageValue);
        //返回可能性
        return possibility;
    }

    /**
     * 模糊匹配查询所有客户的名字，用于前台实现自动补全
     * @return
     */
    @RequestMapping("/workbench/transaction/queryCustomerNameByName.do")
    @ResponseBody
    public Object queryCustomerNameByName(String customerName){
        //调用service方法，查询所有客户名称
        List<String> customerNameList = customerService.queryAllCustomerNameByName(customerName);
        //根据查询结果，返回响应信息
        return  customerNameList;
    }

    /**
     * 保存在创建页面中创建的交易记录
     * @param map
     * @param session
     * @return
     */
    @RequestMapping("/workbench/transaction/saveCreateTran.do")
    @ResponseBody
    public Object saveCreateTran(@RequestParam Map<String,Object> map, HttpSession session){
        //继续封装参数
        map.put(Constant.SESSION_USER,session.getAttribute(Constant.SESSION_USER));
        ReturnObject returnObject = new ReturnObject();
        //调用service方法，保存创建的交易记录
        try {
            tranService.saveCreateTran(map);
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;

    }

    /**
     * 实现跳转至某笔交易的详细信息页面
     * 需要从数据库中取出该条交易的详细信息、该条记录下所有备注信息、该条记录下所有交易历史记录信息，保存至作用域中
     * @param id
     * @param request
     * @return
     */
    @RequestMapping("/workbench/transaction/detailTran.do")
    public String detailTran(String id, HttpServletRequest request){
        //调用service方法，查询交易、交易备注、交易历史记录的信息
        Tran tran = tranService.queryTranForDetailById(id);
        List<TranRemark> remarkList = tranRemarkService.queryTranRemarkForDetailByTranId(id);
        List<TranHistory> historyList = tranHistoryService.queryTranHistoryForDetailByTranId(id);
        //根据交易所处的阶段，去possibility.properties配置文件中查询可能性字段，并赋值
        ResourceBundle bundle = ResourceBundle.getBundle("possibility");
        String possibility = bundle.getString(tran.getStage());
        tran.setPossibility(possibility);//在tran实体类中扩展此属性，保存可能性信息
        //保存到作用域中
        request.setAttribute("tran",tran);
        request.setAttribute("remarkList",remarkList);
        request.setAttribute("historyList",historyList);

        //调用service层方法，将Stage阶段的所有数据字典值排序，保存到作用域中传递到前端，用于交易详细页面中交易图标内容的显示
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        request.setAttribute("stageList",stageList);
        //请求转发
        return "workbench/transaction/detail";
    }
}
