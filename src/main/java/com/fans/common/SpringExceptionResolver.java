package com.fans.common;

import com.fans.exception.ParamException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName SpringExceptionResolver
 * @Description: TODO 自定义异常处理
 * @Author fan
 * @Date 2018-11-20 09:40
 * @Version 1.0
 **/
@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        String url = httpServletRequest.getRequestURL().toString();
        ModelAndView modelAndView;
        String defaultMsg = "System Error";
        if (e instanceof ParamException) {
            JsonData result = JsonData.fail(e.getMessage());
            modelAndView = new ModelAndView("jsonView", result.toMap());
        } else {
            log.error("unknown json exception, url:" + url, e);
            JsonData result = JsonData.fail(defaultMsg);
            modelAndView = new ModelAndView("jsonView", result.toMap());
        }
        return modelAndView;
    }
}
