package com.fans.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName CorsFilter
 * @Description: 开启跨域请求过滤器
 * @Author fan
 * @Date 2019-04-07 16:16
 * @Version 1.0
 **/
public class CorsFilter implements Filter {
    public CorsFilter() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "User-Agent,Origin,Cache-Control,Content-type,Date,Server,withCredentials,AccessToken");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        httpServletResponse.setHeader("Access-Control-Max-Age", "1209600");
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "accesstoken");
        httpServletResponse.setHeader("Access-Control-Request-Headers", "accesstoken");
        httpServletResponse.setHeader("Expires", "-1");
        httpServletResponse.setHeader("Cache-Control", "no-cache");
        httpServletResponse.setHeader("pragma", "no-cache");
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig fConfig) {

    }

    @Override
    public void destroy() {
    }
}
