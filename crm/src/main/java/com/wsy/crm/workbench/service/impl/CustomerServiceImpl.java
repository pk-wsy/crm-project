package com.wsy.crm.workbench.service.impl;

import com.wsy.crm.workbench.mapper.CustomerMapper;
import com.wsy.crm.workbench.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("customerService")
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public List<String> queryAllCustomerNameByName(String customerName) {
        return customerMapper.selectAllCustomerNameByName(customerName);
    }
}
