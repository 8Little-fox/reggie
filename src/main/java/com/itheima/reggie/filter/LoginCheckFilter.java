package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
//    路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        1. 获取本次请求的URI
        String requestURI =request.getRequestURI();

//        定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
        };
//        2. 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);


//        3. 如果不需要处理，则直接放行
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }
//        4. 判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") !=null) {
            /**
             * common/BaseContext.java
             * 基于 ThreadLocal 封装工具类，用户保存和获取当前登录用户id
             */
            Long embId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(embId);

//            long id = Thread.currentThread().getId();
//            log.info("线程id 为：{}", id);
            filterChain.doFilter(request, response);
            return;
        }
//        5. 如果未登录则返回未登录结果, 通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，坚持本次请求是否需要放行
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
//            match 匹配的意思
            if (PATH_MATCHER.match(url, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
