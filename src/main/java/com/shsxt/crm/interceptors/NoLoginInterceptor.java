package com.shsxt.crm.interceptors;

import com.shsxt.crm.exceptions.NoLoginException;
import com.shsxt.crm.service.UserService;
import com.shsxt.crm.utils.LoginUserUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoLoginInterceptor extends HandlerInterceptorAdapter {
    @Resource
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        /**
         * 获取cookie
         * 存在id，且与数据库匹配，放行。否则，拦截重定向
         *
         */

        int userId = LoginUserUtil.releaseUserIdFromCookie(request);

       if (userId==0 || null==userService.selectByPrimaryKey(userId)){
           throw new NoLoginException();
       }
        return true;
    }
}
