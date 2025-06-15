package org.xzx.interceptor;

import io.jsonwebtoken.Claims;

import org.xzx.context.UserContext;

import org.xzx.properties.JwtProperties;
import org.xzx.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private JwtProperties jwtproperties;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //从请求头中根据tokenName找到token
        String token = request.getHeader(jwtproperties.getTokenName());

        try {
            //解析token获取payload，再获取到登录用户ID
            Claims claims = JwtUtil.parseJWT(jwtproperties.getSecretKey(), token);
            Long userId = Long.valueOf(claims.get("userId").toString());
            UserContext.setCurrentId(userId);
            return true;
        } catch (Exception ex) {

            response.setStatus(401);
            return false;
        }
    }
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.removeCurrentId();
    }

}
