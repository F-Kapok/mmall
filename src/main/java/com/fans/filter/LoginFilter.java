package com.fans.filter;

import com.fans.common.CommonConstants;
import com.fans.common.RequestHolder;
import com.fans.common.ResponseCode;
import com.fans.common.ServerResponse;
import com.fans.pojo.MmallUser;
import com.fans.utils.JsonMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName LoginFilter
 * @Description: 登录拦截器
 * @Author fan
 * @Date 2018-12-10 16:29
 * @Version 1.0
 **/
@Slf4j
public class LoginFilter implements Filter {
    private Set<String> exclusionUrlSet = Sets.newConcurrentHashSet();
    private static final String MANAGE_URL = "/manage";
    private static final String RICH_TEXT_IMG_UPLOAD = "/richtext_img_upload.do";
    private static final String WILDCARD = "/*";
    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String ALPAY_CALLBACK_URL = "/alipay_callback.do";

    @Override
    public void init(FilterConfig filterConfig) {
        // 初始化白名单的url
        String exclusionUrls = filterConfig.getInitParameter("ignoreUrl");
        List<String> exclusionList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(exclusionUrls);
        exclusionUrlSet = Sets.newConcurrentHashSet(exclusionList);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setContentType(CONTENT_TYPE);
        ServerResponse<Object> serverResponse;
        Map<String, Object> result = Maps.newHashMap();
        String servletPath = request.getServletPath();
        boolean isExclusion = false;
        for (Iterator it = exclusionUrlSet.iterator(); it.hasNext(); ) {
            String str = it.next().toString();
            if (servletPath.startsWith(str.substring(0, str.lastIndexOf(WILDCARD)))) {
                isExclusion = true;
            }
        }
        if (isExclusion) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (servletPath.endsWith(ALPAY_CALLBACK_URL)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpSession session = request.getSession();
        MmallUser user = (MmallUser) session.getAttribute(CommonConstants.CURRENT_USER);
        if (user == null) {
            if (servletPath.endsWith(RICH_TEXT_IMG_UPLOAD)) {
                result.put("success", false);
                result.put("msg", "用户未登录");
                response.getWriter().write(JsonMapper.obj2String(result));
                return;
            }
            serverResponse = ServerResponse.failureCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录请进行登录操作");
            response.getWriter().write(JsonMapper.obj2String(serverResponse));
            return;
        }
        RequestHolder.add(user);
        RequestHolder.add(request);
        if (servletPath.startsWith(MANAGE_URL)) {
            if (user.getRole().intValue() != CommonConstants.Role.ROLE_ADMIN) {
                if (servletPath.endsWith(RICH_TEXT_IMG_UPLOAD)) {
                    result.put("success", false);
                    result.put("msg", "无权限操作，需要管理员权限");
                    response.getWriter().write(JsonMapper.obj2String(result));
                    return;
                }
                serverResponse = ServerResponse.failureMsg("无权限操作，需要管理员权限");
                response.getWriter().write(JsonMapper.obj2String(serverResponse));
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
        return;
    }

    @Override
    public void destroy() {

    }
}
