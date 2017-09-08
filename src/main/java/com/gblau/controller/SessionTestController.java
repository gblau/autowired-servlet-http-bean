package com.gblau.controller;

import com.gblau.common.Log;
import com.gblau.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.gblau.common.ResponseModel.ok;


/**
 * 能不能正常使用一个依赖注入的{@code HttpServletRequest}和 {@code HttpSession}？
 * 问题描述: <a>https://stackoverflow.com/questions/28019650/cant-understand-autowired-httpservletrequest-of-spring-mvc-well<a/>
 * 解释: <a>https://stackoverflow.com/questions/14371335/spring-scoped-proxy-bean<a/>
 *
 * @author gblau
 * @date 2017-09-06
 */
@RestController
public class SessionTestController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpSession session;

    @RequestMapping(value = "/request", method = RequestMethod.GET)
    public ResponseModel testAutowiredRequestController() {
        String parameter = request.getParameter("test");

        Log.info("本次请求的 hashcode: {}", request.hashCode());
        Log.info("传入的test参数: {}", parameter);

        HttpSession requestSession = request.getSession();
        Log.info("request session hashcode: {}", requestSession.hashCode());
        if (parameter != null)
            requestSession.setAttribute("test", parameter);
        Log.info("request session 的参数: {}", requestSession.getAttribute("test"));

        return ok().body(request.hashCode()+ "\n" + request.getParameter("test"));
    }

    @RequestMapping(value = "/session", method = RequestMethod.GET)
    public ResponseModel testAutowiredSessionController() {
        Log.info("autowired session hashcode: {}", session.hashCode());
        Log.info("autowired session 是新的吗？ {}", session.isNew());
        Log.info("autowired session id {}", session.getId());

        if (session.getAttribute("test") == null)
            session.setAttribute("test", "测试一下嘻嘻嘻");

        Log.info("传入的test参数: {}", session.getAttribute("test"));
        return ok().body(session.hashCode());
    }

    @RequestMapping("/compare")
    public ResponseModel testSession(HttpSession methodGetSession) {
        HttpSession requestSession = request.getSession(false);
        Log.info("还没有获取request session");
        Log.info("request session 为空吗？ {}", requestSession == null);
        Log.info("========================================");

        Log.info("autowired session 为空吗？ {}", session == null);
        Log.info("autowired session 是新的吗？ {}", session.isNew());
        Log.info("autowired session id {}", session.getId());
        Log.info("autowired session hash code {}", session.hashCode());
        Log.info("========================================");

        Log.info("methodGetSession session 为空吗？ {}", methodGetSession == null);
        Log.info("methodGetSession session 是新的吗？ {}", methodGetSession.isNew());
        Log.info("methodGetSession session id {}", methodGetSession.getId());
        Log.info("methodGetSession session hash code {}", methodGetSession.hashCode());
        session.setAttribute("request", "autowired拿到的session");

        requestSession = request.getSession();

        Log.info("==============已经获取request session啦！");
        Log.info("{}", requestSession.getAttribute("request"));

        Log.info("session 为空吗？ {}", requestSession == null);
        Log.info("request session 是新的吗？ {}", requestSession.isNew());
        Log.info("request session id {}", requestSession.getId());
        Log.info("request session hash code {}", requestSession.hashCode());

        Log.info("autowired session 为空吗？ {}", session == null);
        Log.info("autowired session 是新的吗？ {}", session.isNew());
        Log.info("autowired session id {}", session.getId());
        Log.info("autowired session hash code {}\n", session.hashCode());

        return ok().build();
    }

    @RequestMapping("/autowiredsession")
    public ResponseModel testAutowiredSession() {
        Log.info("普通方法拿到的session hash code {}", session.hashCode());
        Log.info("普通方法拿到的session 是新的吗？ {}", session.isNew());
        Log.info("普通方法拿到的session id {}", session.getId());

        if (session.getAttribute("test") == null)
            session.setAttribute("test", "测试一下嘻嘻嘻");

        Log.info("看看能不能拿到数据: {}", request.getSession().getAttribute("test"));
        return ok().build();
    }
}
