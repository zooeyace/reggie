package me.zyy.reggie.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.zyy.reggie.common.BaseContext;
import me.zyy.reggie.common.R;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录过滤器
 */

@WebFilter(filterName = "loginFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    public static AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 获取此次请求uri
        String requestURI = request.getRequestURI();

        // 白名单
        String[] whiteList = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                // 客户端
                "/user/sendMsg",
                "/user/login"
        };

        // 白名单直接放行
        if (check(whiteList, requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 已登录
        Long empId = (Long) request.getSession().getAttribute("employee");
        if (empId != null) {
            log.info("登录的员工为: {}", empId);
            // id放入threadLocal，因为filter是最先走的
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }

        // 没登录
        response.getWriter().write(JSON.toJSONString(R.error(0, "NOT LOGIN")));
    }

    boolean check(String[] arr, String str) {
        boolean flag = false;
        for (String s : arr) {
            if (PATH_MATCHER.match(s, str)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
