package com.fans.filter;

import com.fans.common.CommonConstants;
import com.fans.common.RequestHolder;
import com.fans.common.ResponseCode;
import com.fans.pojo.MmallUser;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @ClassName LoginFilter
 * @Description: 登录拦截器
 * @Author fan
 * @Date 2018-12-10 16:29
 * @Version 1.0
 **/
public class LoginFilter implements Filter {
    private Set<String> exclusionUrlSet = Sets.newConcurrentHashSet();

    @Override
    public void init(FilterConfig filterConfig) {
        // 初始化白名单的url
        String exclusionUrls = filterConfig.getInitParameter("ignoreUrl");
        List<String> exclusionList = Splitter.on(",").omitEmptyStrings().splitToList(exclusionUrls);
        exclusionUrlSet = Sets.newConcurrentHashSet(exclusionList);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String servletPath = request.getServletPath();
        if (exclusionUrlSet.contains(servletPath)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpSession session = request.getSession();
        MmallUser user = (MmallUser) session.getAttribute(CommonConstants.CURRENT_USER);
        if (user == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("[{\"msg\":\"未登录请进行登录操作\",\"status\":\"" + ResponseCode.NEED_LOGIN.getCode() + "\"}]");
            return;
        }
        RequestHolder.add(user);
        RequestHolder.add(request);
        filterChain.doFilter(servletRequest, servletResponse);
        return;
    }

    @Override
    public void destroy() {

    }
}
