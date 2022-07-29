package com.wsy.crm.workbench.web.controller;

import com.wsy.crm.workbench.domain.FunnelVO;
import com.wsy.crm.workbench.service.TranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ChartController {

    @Autowired
    private TranService tranService;
    /**
     * 跳转至index.jsp(图标的)
     * @return
     */
    @RequestMapping("/workbench/chart/transaction/index.do")
    public String index(){
        return "workbench/chart/transaction/index";
    }


    @RequestMapping("/workbench/chart/transaction/queryCountOfTranGroupByStage.do")
    @ResponseBody
    public Object queryCountOfTranGroupByStage(){
        //调用service层方法，查询交易在不同阶段下的数据量
        List<FunnelVO> funnelVOList = tranService.queryCountOfTranGroupByStage();
        //根据查询结果，返回响应信息
        return funnelVOList;
    }
}
