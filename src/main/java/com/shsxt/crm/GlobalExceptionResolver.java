package com.shsxt.crm;

import com.alibaba.fastjson.JSON;
import com.shsxt.crm.exceptions.NoLoginException;
import com.shsxt.crm.exceptions.ParamsException;
import com.shsxt.crm.model.ResultInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        /**
         * 首先判断异常类型
         *      如果异常类型为未登录状态免  进行试图转发
         */

        ModelAndView mv = new ModelAndView();
        if (e instanceof NoLoginException){
            NoLoginException ne = (NoLoginException) e;
            mv.setViewName("no_login");
            mv.addObject("msg",ne.getMsg());
            mv.addObject("ctx",httpServletRequest.getContextPath());
            return mv;

        }

        /**
         * 方法返回值类型判断
         *      如果方法级别存在@Responsebody 则方法响应类容为json 否则为视图
         * 返回值
         *      视图：默认错误页面
         *
         *      json：错误的json信息
         */

        mv.setViewName("error");
        mv.addObject("msg","系统异常,请稍后再试");
        mv.addObject("code",400);



        if(o instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod) o;
            ResponseBody responseBody = hm.getMethod().getDeclaredAnnotation(ResponseBody.class);
            if (null==responseBody){
                /**
                 * 视图
                 */

                if (e instanceof  ParamsException){
                    ParamsException pe = (ParamsException) e;
                    mv.addObject("msg",pe.getMsg());
                    mv.addObject("code",pe.getCode());
                }
                return mv;

            }else {
                /**
                 * json
                 */
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("服务器错误，请稍后再试");
                if (e instanceof ParamsException){
                    ParamsException pe = (ParamsException) e;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                httpServletResponse.setCharacterEncoding("utf-8");
                httpServletResponse.setContentType("application/json;charset=utf-8");
                PrintWriter pw = null;
                try {
                    pw= httpServletResponse.getWriter();
                    pw.write(JSON.toJSONString(resultInfo));
                    pw.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }finally {
                    if (null!=pw){
                        pw.close();
                    }
                }
                return null;
            }
        }else {
            return mv;
        }

    }
}
