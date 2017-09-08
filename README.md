# singleton对象依赖注入HttpSession和HttpRequest

## 配置

所有的测试代码其实都在`com.gblau.controller.SessionController`中，以下是我个人测试的部分数据，只贴出最简单的测试结果，可以自行进行其他测试，比如整合`Shiro`

```
@Autowired
private HttpServletRequest request;
@Autowired
private HttpSession session;
```

然后测试Request，主要的逻辑就是查看注入的请求对象和从参数上直接获取Request是不是一样的。

```
@RequestMapping(value = "/request", method = RequestMethod.GET)
public ResponseModel testAutowiredRequestController(HttpServletRequest methodGetRequest) {
    String parameter = request.getParameter("test");
    Log.info("本次请求的 hashcode: {}", request.hashCode());
    Log.info("方法参数拿到的请求的 hashcode {}", methodGetRequest.hashCode());
    Log.info("传入的test参数: {}", parameter);
    
    HttpSession requestSession = request.getSession();
    Log.info("request session hashcode: {}", requestSession.hashCode());
    
    HttpSession methodGetRequestSession = methodGetRequest.getSession();
    Log.info("methodGetRequest session hashcode: {}", methodGetRequestSession.hashCode());
    
    if (parameter != null)
        requestSession.setAttribute("test", parameter);
    Log.info("request session 的参数: {}", requestSession.getAttribute("test"));

    return ok().body(request.hashCode()+ "\n" + request.getParameter("test"));
}
```

最后是Session，查看一下session的生成时间以及是不是相同的对象

```
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
```

## 结果

执行`http://localhost:8080/request`

```
[2017-09-08 14:51:36.524 INFO ] (SessionController:91) 本次请求的 hashcode: 758131770
[2017-09-08 14:51:36.525 INFO ] (SessionController:91) 方法参数拿到的请求的 hashcode 1146646659
[2017-09-08 14:51:36.525 INFO ] (SessionController:91) 传入的test参数: null
[2017-09-08 14:51:36.525 INFO ] (SessionController:91) request session hashcode: 473353362
[2017-09-08 14:51:36.526 INFO ] (SessionController:91) methodGetRequest session hashcode: 473353362
[2017-09-08 14:51:36.526 INFO ] (SessionController:91) request session 的参数: null

[2017-09-08 14:51:38.589 INFO ] (SessionController:91) 本次请求的 hashcode: 758131770
[2017-09-08 14:51:38.589 INFO ] (SessionController:91) 方法参数拿到的请求的 hashcode 1725124386
[2017-09-08 14:51:38.589 INFO ] (SessionController:91) 传入的test参数: null
[2017-09-08 14:51:38.590 INFO ] (SessionController:91) request session hashcode: 473353362
[2017-09-08 14:51:38.590 INFO ] (SessionController:91) methodGetRequest session hashcode: 473353362
[2017-09-08 14:51:38.590 INFO ] (SessionController:91) request session 的参数: null
```

执行`http://localhost:8080/session`

```
[2017-09-08 14:52:48.991 INFO ] (SessionController:91) autowired session hashcode: 1098543368
[2017-09-08 14:52:48.992 INFO ] (SessionController:91) autowired session 是新的吗？ false
[2017-09-08 14:52:48.992 INFO ] (SessionController:91) autowired session id 4B12DFE9A64E525B9DA12AC7B7755C79
[2017-09-08 14:52:48.993 INFO ] (SessionController:91) 传入的test参数: 测试一下嘻嘻嘻

[2017-09-08 14:52:51.526 INFO ] (SessionController:91) autowired session hashcode: 1098543368
[2017-09-08 14:52:51.526 INFO ] (SessionController:91) autowired session 是新的吗？ false
[2017-09-08 14:52:51.526 INFO ] (SessionController:91) autowired session id 4B12DFE9A64E525B9DA12AC7B7755C79
[2017-09-08 14:52:51.526 INFO ] (SessionController:91) 传入的test参数: 测试一下嘻嘻嘻
```

这里可以得出：

- @Autowired的`HttpSession`和`HttpServletRequest`，hashcode都是一样的，意思就是说他们已经是singleton的对象了
- 实际获取到的`HttpServletRequest`和注入的`HttpServletRequest`是不一样的

再看看session，于是又写了一个方法作一个比较

```
@RequestMapping("/compare")
public ResponseModel testSession(HttpSession  methodGetSession) {
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
    Log.info("autowired session hash code {}", session.hashCode());

    return ok().build();
}

```

两个浏览器分别请求两次，测试的结果如下

```

[2017-09-08 15:43:30.189 INFO ] (SessionController:91) 还没有获取request session
[2017-09-08 15:43:30.190 INFO ] (SessionController:91) request session 为空吗？ false
[2017-09-08 15:43:30.190 INFO ] (SessionController:91) autowired session 为空吗？ false
[2017-09-08 15:43:30.190 INFO ] (SessionController:91) autowired session 是新的吗？ false
[2017-09-08 15:43:30.192 INFO ] (SessionController:91) autowired session id 1AB895FB0B6C87EC56C0555DBD48DF17
[2017-09-08 15:43:30.192 INFO ] (SessionController:91) autowired session hash code 1098543368
[2017-09-08 15:43:30.192 INFO ] (SessionController:91) methodGetSession session 为空吗？ false
[2017-09-08 15:43:30.192 INFO ] (SessionController:91) methodGetSession session 是新的吗？ false
[2017-09-08 15:43:30.192 INFO ] (SessionController:91) methodGetSession session id 1AB895FB0B6C87EC56C0555DBD48DF17
[2017-09-08 15:43:30.192 INFO ] (SessionController:91) methodGetSession session hash code 1966812743
[2017-09-08 15:43:30.192 INFO ] (SessionController:91) 已经获取request session啦！
[2017-09-08 15:43:30.193 INFO ] (SessionController:91) autowired拿到的session
[2017-09-08 15:43:30.193 INFO ] (SessionController:91) session 为空吗？ false
[2017-09-08 15:43:30.193 INFO ] (SessionController:91) request session 是新的吗？ false
[2017-09-08 15:43:30.193 INFO ] (SessionController:91) request session id 1AB895FB0B6C87EC56C0555DBD48DF17
[2017-09-08 15:43:30.194 INFO ] (SessionController:91) request session hash code 1966812743
[2017-09-08 15:43:30.194 INFO ] (SessionController:91) autowired session 为空吗？ false
[2017-09-08 15:43:30.194 INFO ] (SessionController:91) autowired session 是新的吗？ false
[2017-09-08 15:43:30.194 INFO ] (SessionController:91) autowired session id 1AB895FB0B6C87EC56C0555DBD48DF17
[2017-09-08 15:43:30.194 INFO ] (SessionController:91) autowired session hash code 1098543368


[2017-09-08 15:43:34.129 INFO ] (SessionController:91) 还没有获取request session
[2017-09-08 15:43:34.129 INFO ] (SessionController:91) request session 为空吗？ false
[2017-09-08 15:43:34.130 INFO ] (SessionController:91) autowired session 为空吗？ false
[2017-09-08 15:43:34.130 INFO ] (SessionController:91) autowired session 是新的吗？ false
[2017-09-08 15:43:34.130 INFO ] (SessionController:91) autowired session id 1AB895FB0B6C87EC56C0555DBD48DF17
[2017-09-08 15:43:34.130 INFO ] (SessionController:91) autowired session hash code 1098543368
[2017-09-08 15:43:34.131 INFO ] (SessionController:91) methodGetSession session 为空吗？ false
[2017-09-08 15:43:34.131 INFO ] (SessionController:91) methodGetSession session 是新的吗？ false
[2017-09-08 15:43:34.131 INFO ] (SessionController:91) methodGetSession session id 1AB895FB0B6C87EC56C0555DBD48DF17
[2017-09-08 15:43:34.131 INFO ] (SessionController:91) methodGetSession session hash code 1966812743
[2017-09-08 15:43:34.132 INFO ] (SessionController:91) 已经获取request session啦！
[2017-09-08 15:43:34.132 INFO ] (SessionController:91) autowired拿到的session
[2017-09-08 15:43:34.132 INFO ] (SessionController:91) session 为空吗？ false
[2017-09-08 15:43:34.132 INFO ] (SessionController:91) request session 是新的吗？ false
[2017-09-08 15:43:34.133 INFO ] (SessionController:91) request session id 1AB895FB0B6C87EC56C0555DBD48DF17
[2017-09-08 15:43:34.133 INFO ] (SessionController:91) request session hash code 1966812743
[2017-09-08 15:43:34.133 INFO ] (SessionController:91) autowired session 为空吗？ false
[2017-09-08 15:43:34.133 INFO ] (SessionController:91) autowired session 是新的吗？ false
[2017-09-08 15:43:34.133 INFO ] (SessionController:91) autowired session id 1AB895FB0B6C87EC56C0555DBD48DF17
[2017-09-08 15:43:34.134 INFO ] (SessionController:91) autowired session hash code 1098543368

//换了一个浏览器请求

[2017-09-08 16:14:03.784 INFO ] (SessionController:91) 还没有获取request session
[2017-09-08 16:14:03.784 INFO ] (SessionController:91) request session 为空吗？ false
[2017-09-08 16:14:03.784 INFO ] (SessionController:91) autowired session 为空吗？ false
[2017-09-08 16:14:03.785 INFO ] (SessionController:91) autowired session 是新的吗？ true
[2017-09-08 16:14:03.785 INFO ] (SessionController:91) autowired session id 2E56CDADCF71B46FB530C3B4DF6B90BB
[2017-09-08 16:14:03.785 INFO ] (SessionController:91) autowired session hash code 1098543368
[2017-09-08 16:14:03.785 INFO ] (SessionController:91) methodGetSession session 为空吗？ false
[2017-09-08 16:14:03.786 INFO ] (SessionController:91) methodGetSession session 是新的吗？ true
[2017-09-08 16:14:03.786 INFO ] (SessionController:91) methodGetSession session id 2E56CDADCF71B46FB530C3B4DF6B90BB
[2017-09-08 16:14:03.786 INFO ] (SessionController:91) methodGetSession session hash code 1175224256
[2017-09-08 16:14:03.786 INFO ] (SessionController:91) 已经获取request session啦！
[2017-09-08 16:14:03.787 INFO ] (SessionController:91) autowired拿到的session
[2017-09-08 16:14:03.787 INFO ] (SessionController:91) session 为空吗？ false
[2017-09-08 16:14:03.787 INFO ] (SessionController:91) request session 是新的吗？ true
[2017-09-08 16:14:03.787 INFO ] (SessionController:91) request session id 2E56CDADCF71B46FB530C3B4DF6B90BB
[2017-09-08 16:14:03.788 INFO ] (SessionController:91) request session hash code 1175224256
[2017-09-08 16:14:03.788 INFO ] (SessionController:91) autowired session 为空吗？ false
[2017-09-08 16:14:03.788 INFO ] (SessionController:91) autowired session 是新的吗？ true
[2017-09-08 16:14:03.788 INFO ] (SessionController:91) autowired session id 2E56CDADCF71B46FB530C3B4DF6B90BB
[2017-09-08 16:14:03.789 INFO ] (SessionController:91) autowired session hash code 1098543368


[2017-09-08 16:14:07.180 INFO ] (SessionController:91) 还没有获取request session
[2017-09-08 16:14:07.180 INFO ] (SessionController:91) request session 为空吗？ false
[2017-09-08 16:14:07.180 INFO ] (SessionController:91) autowired session 为空吗？ false
[2017-09-08 16:14:07.180 INFO ] (SessionController:91) autowired session 是新的吗？ false
[2017-09-08 16:14:07.181 INFO ] (SessionController:91) autowired session id 2E56CDADCF71B46FB530C3B4DF6B90BB
[2017-09-08 16:14:07.181 INFO ] (SessionController:91) autowired session hash code 1098543368
[2017-09-08 16:14:07.181 INFO ] (SessionController:91) methodGetSession session 为空吗？ false
[2017-09-08 16:14:07.181 INFO ] (SessionController:91) methodGetSession session 是新的吗？ false
[2017-09-08 16:14:07.181 INFO ] (SessionController:91) methodGetSession session id 2E56CDADCF71B46FB530C3B4DF6B90BB
[2017-09-08 16:14:07.182 INFO ] (SessionController:91) methodGetSession session hash code 1175224256
[2017-09-08 16:14:07.182 INFO ] (SessionController:91) 已经获取request session啦！
[2017-09-08 16:14:07.182 INFO ] (SessionController:91) autowired拿到的session
[2017-09-08 16:14:07.182 INFO ] (SessionController:91) session 为空吗？ false
[2017-09-08 16:14:07.183 INFO ] (SessionController:91) request session 是新的吗？ false
[2017-09-08 16:14:07.183 INFO ] (SessionController:91) request session id 2E56CDADCF71B46FB530C3B4DF6B90BB
[2017-09-08 16:14:07.183 INFO ] (SessionController:91) request session hash code 1175224256
[2017-09-08 16:14:07.183 INFO ] (SessionController:91) autowired session 为空吗？ false
[2017-09-08 16:14:07.183 INFO ] (SessionController:91) autowired session 是新的吗？ false
[2017-09-08 16:14:07.184 INFO ] (SessionController:91) autowired session id 2E56CDADCF71B46FB530C3B4DF6B90BB
[2017-09-08 16:14:07.184 INFO ] (SessionController:91) autowired session hash code 1098543368
```

这里可以看出

- @Autowired session和普通获取session是不一样的
- 方法获取session和request的session是一样的。其实如果删掉方法里的session，第一次false获取session会返回null，`request session 为空吗？`会打印`true`
