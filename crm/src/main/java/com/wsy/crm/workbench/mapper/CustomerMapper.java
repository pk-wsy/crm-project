package com.wsy.crm.workbench.mapper;

import com.wsy.crm.workbench.domain.Customer;

import java.util.List;

public interface CustomerMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_customer
     *
     * @mbggenerated Wed Jul 27 12:50:37 CST 2022
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_customer
     *
     * @mbggenerated Wed Jul 27 12:50:37 CST 2022
     */
    int insert(Customer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_customer
     *
     * @mbggenerated Wed Jul 27 12:50:37 CST 2022
     */
    int insertSelective(Customer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_customer
     *
     * @mbggenerated Wed Jul 27 12:50:37 CST 2022
     */
    Customer selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_customer
     *
     * @mbggenerated Wed Jul 27 12:50:37 CST 2022
     */
    int updateByPrimaryKeySelective(Customer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_customer
     *
     * @mbggenerated Wed Jul 27 12:50:37 CST 2022
     */
    int updateByPrimaryKey(Customer record);

    /**
     * 保存创建的客户对象
     * @param customer
     * @return
     */
    int insertCustomer(Customer customer);

    /**
     * 模糊匹配查询所有客户名称
     * @return
     */
    List<String> selectAllCustomerNameByName(String customerName);

    /**
     * 根据客户的名字精确查询客户
     * @param name
     * @return
     */
    Customer selectCustomerByName(String name);
}