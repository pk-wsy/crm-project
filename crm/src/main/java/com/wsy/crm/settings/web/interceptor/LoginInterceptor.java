package com.wsy.crm.settings.web.interceptor;

import com.wsy.crm.commons.constant.Constant;
import com.wsy.crm.settings.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 为了防止其他人通过在浏览器地址栏直接输入项目内部url从而可以直接访问到项目内部资源（这样可以跳过登录验证的环节）
 * 应当在项目所有资源的前面增加一个拦截器，以保证在浏览器访问项目内部资源时，确保浏览器必须登录才能访问（存在用户的相关session）
 * 如果用户在浏览器上未登录就访问项目内部资源，则自动跳转至登录页面
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //获取session中的用户（由于这个方法是实现接口的方法，因而不能随便加入形参，可以通过请求来获取session）
        HttpSession session = httpServletRequest.getSession();
        //取出用户
        User user = (User)session.getAttribute(Constant.SESSION_USER);
        //如果用户不存在，则应使其重定向至登录页面
        if(user == null){
            //由于此处代码不在控制器层，不能经过视图解析，因此应当使用原始的重定向方式进行转发
            //请求转发和请求重定向的原生实现在url中有区别，重定向必须写应用名，转发必须省略应用名（默认已经从应用名如/crm开始）
            //并且此处的项目名称不能写死，万一之后改项目名称，因此要确保其动态性，应当动态获取
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath());
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
