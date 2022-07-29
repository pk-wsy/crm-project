package com.wsy.crm.settings.service;

import com.wsy.crm.settings.domain.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * 根据用户名和密码查询用户信息
     * @param map
     * @return
     */
    User queryUserByLoginActAndPwd(Map<String,Object> map);

    /**
     * 查询所有用户
     * @return
     */
    List<User> queryAllUsers();
}
