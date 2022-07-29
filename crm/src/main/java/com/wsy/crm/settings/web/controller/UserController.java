package com.wsy.crm.settings.web.controller;

import com.wsy.crm.commons.constant.Constant;
import com.wsy.crm.commons.domain.ReturnObject;
import com.wsy.crm.commons.utils.DateUtils;
import com.wsy.crm.settings.domain.User;
import com.wsy.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//Service负责所有CRUD，Controller负责根据条件返回数据（阿里规范文档）
@Controller
public class UserController {

    @Autowired
    private UserService userService;
    /**
     * url要和controller方法处理完请求后，响应信息返回的页面的资源目录保持一致
     * 比如这个方法要跳转的页面的路径是WEB-INF/pages/settings/qx/user/login.jsp，那此处的路径就写
     * /WEB-INF/pages/settings/qx/user/toLogin.do(最后资源名与方法名一致，并且习惯带.do)(.do代表控制器的方法)
     * 因为所有页面都在/WEB-INF/pages的目录结构下，因此可以考虑省略这个路径，简化后为
     * /settings/qx/user/toLogin.do
     */
    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin(){
        //请求转发至登陆页面
        return "settings/qx/user/login";
    }

    /**
     * 由于要向前台返回一个json字符串（是对异步请求的页面局部刷新，而不是传一个整个页面，因此需要响应json字符串）
     * 如果定义一个类返回，那不同情境下可能需要传不同的json，复用性较差，因此这样的情景都考虑返回值的类型为object类型
     * 此处requestMapping中的参数为，这是因为该控制器方法负责登录功能，最后响应的页面也是登录页面login.jsp
     * @return
     */
    @RequestMapping("/settings/qx/user/login.do")
    @ResponseBody//向前台响应json因此需要加此注解
    //此方法的参数都是前台表单传入的，传入的参数我们都默认使用String字符串来接收
    //之后要获取客户端的ip地址，因此需要传入形参request来进行获取
    public Object login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request, HttpServletResponse response, HttpSession session){
        //将前台发来的参数封装在一个map集合中
        //经过分析之后，后台sql语句只需要用户名账号（loginAct）和用户名密码(loginPwd)两个参数，不需要是否保存参数（isRemPwd）进行逻辑判断
        //因此只需要将前两个参数封装在map集合中
        Map<String,Object> map = new HashMap<>();
        //注意此处map的key值有讲究，要和mybatis中的sql语句中#{}内取的key完全符合，这样才能保证在mybatis中可以取到相应的值
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);

        //调用service层方法，获取user对象，进行后续判断
        User user = userService.queryUserByLoginActAndPwd(map);

        //需要向前端返回登录是否成功、提示信息等，因此需要创建一个java对象来对结果进行封装，从而返回至前台页面
        //这个类一般放在和各个模块平行的commons包内，因为这个类可能不仅用于系统功能，也可能用于业务功能
        //同样需要在commons包内新建domain子包，并将这个类放入其中，因为后续可能会有公共的工具包、方法包等
        ReturnObject returnObject = new ReturnObject();

        if(user == null){
            //登陆失败，用户名或密码错误
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("用户名或密码错误");
        }else{
            //比较过期时间和当前时间大小，如果已经过期则登陆失败
            //此处比较时间大小是将两个时间均转为字符串进行比较，注意要保证两个时间格式相同均为yyyy-MM-dd HH:mm:ss
            //数据库存储的时间默认已经是这个格式（这是通过在前端页面设置日历来进行保证的），因此我们只需要保证当前时间为这个格式即可
            //借用simpleDateFormat这个类来进行日期向指定格式字符串的转换
            //为了方便后续使用和修改，将此处封装成一个类

            String expireTime = user.getExpireTime();
            if(DateUtils.formatDateTime(new Date()).compareTo(expireTime) > 0){
                //当前时间比过期时间大，意味着此时该账户已经过期
                //登陆失败，账号已过期
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("账号已过期");
            }else if ("0".equals(user.getLockState())){
                //登陆失败，状态被锁定
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("状态被锁定");
            }else if(!user.getAllowIps().contains(request.getRemoteAddr())){
                //允许的ip地址不包括当前客户端的ip地址
                //登陆失败，ip受限
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("ip受限");
            }else{
                //登陆成功
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                //登陆成功后，把user对象放在session作用域中，用于在前端jsp页面上显示用户名
                //并且为了便于后续维护以及修改，将此user对象对应的key值使用常量来表示，在常量类中进行定义
                session.setAttribute(Constant.SESSION_USER,user);
                System.out.println(isRemPwd);
                //当登陆成功时，需要判断用户端是否需要记住密码
                if("true".equals(isRemPwd)){
                    //如果客户端需要记住密码（10天），则需要将用户账号和密码写入到cookie中
                    //这样用户在下次登录时看看有没有这个cookie，如果有这个cookie，则自动填入用户名和密码
                    Cookie c1 = new Cookie("loginAct", user.getLoginAct());
                    //设置用户名cookie的生存时间为10天（由于单位是s，因此需要进行一定的转换）
                    c1.setMaxAge(10*24*60*60);
                    //将cookie随响应发送给客户端
                    response.addCookie(c1);
                    //同样对于密码的cookie也需要保存起来并发送给客户端
                    Cookie c2 = new Cookie("loginPwd", user.getLoginPwd());
                    c2.setMaxAge(10*24*60*60);
                    response.addCookie(c2);
                }else{
                    /*如果选择不记住密码，不代表不放入cookie的意思，因为可能之前放入了cookie，此次登录选择不记住密码，这样在下次登录时应当
                      不显示登陆的信息
                      因此，不记住密码的情形是看此cookie是否存在或过期，如果没有过期，则需要删除相关cookie
                     */
                    //由于cookie保存在客户端，无法在后台直接进行删除，因此考虑创建和要删除的cookie同名的cookie，把这个寿命设置为0
                    //从而实现cookie的覆盖，并且保证cookie已经过期
                    //cookie的值设置什么都行，因为这个cookie只起到覆盖作用并且让其失效，最后也不会取到这个值
                    Cookie c1 = new Cookie("loginAct", "1");
                    c1.setMaxAge(0);
                    response.addCookie(c1);
                    Cookie c2 = new Cookie("loginPwd", "1");
                    c2.setMaxAge(0);
                    response.addCookie(c2);
                }
            }
        }
        //将封装好的java对象，响应至前端
        return returnObject;
    }

    /**
     * 实现安全退出的功能——清除cookie，销毁session
     * @param response
     * @param session
     * @return
     */
    @RequestMapping("/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response, HttpSession session){
        //清除cookie
        //主要清除的cookie就包括用户名和密码两种
        Cookie c1 = new Cookie("loginAct", "1");
        c1.setMaxAge(0);
        response.addCookie(c1);
        Cookie c2 = new Cookie("loginPwd", "1");
        c2.setMaxAge(0);
        response.addCookie(c2);

        //销毁session
        session.invalidate();

        //此处应为重定向到登录页（使得地址栏url发生变化，否则url仍未logout但页面为登录页不符合常规，同时如果url不变则用户刷新页面会持续退出）
        //这里的重定向不用写"/crm"，这是因为借助了springmvc来实现重定向，在底层把应用名加上了，response.sendRedirect("/crm/")
        return "redirect:/";
    }

}
