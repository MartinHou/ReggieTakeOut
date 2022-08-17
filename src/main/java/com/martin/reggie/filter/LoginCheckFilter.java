package com.martin.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.martin.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否完成登录，也可以用拦截器（据说更简单）
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器,支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);
        //定义不需要处理的请求路径
        String[] uris =new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
        //2.判断此请求是否需要处理
        boolean ck = check(uris, requestURI);
        //3.无需处理则放行
        if (ck) {
            log.info("本次请求不处理：{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4.需要处理：如果已经登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录:id为{}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }
        //5.若未登录，则通过输出流方式向客户端页面返回响应数据，见backend/js/request.js
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 检查请求 是否需要处理
     * @param uris 不进行处理的uri列表
     * @param requestURI 当前请求uri
     * @return true if 当前请求不处理
     */
    public boolean check(String[] uris, String requestURI) {
        for (String uri : uris)
            if (PATH_MATCHER.match(uri, requestURI))
                return true;
        return false;
    }
}
