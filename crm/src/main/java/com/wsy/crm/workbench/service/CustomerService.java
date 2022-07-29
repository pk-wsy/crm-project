package com.wsy.crm.workbench.service;

import java.util.List;

public interface CustomerService {

    /**
     * 模糊匹配查询所有客户的名字
     * @return
     */
    List<String> queryAllCustomerNameByName(String customerName);
}
