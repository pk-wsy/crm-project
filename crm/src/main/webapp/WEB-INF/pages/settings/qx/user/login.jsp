<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  String basePath=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<html>
<head>
  <%--设置url的基准，这个base标签表示在整个jsp页面中，对于url路径，前面都默认加上这个基础的base路径--%>
  <%--使用base路径加上资源路径，更容易找到资源，并且代码更加简洁
      对于base标签内部的路径，由于ip和port以及应用名称可以动态性发生变化，所以一般不会写死，而是通过动态获取请求的路径来对
      base标签的href属性进行赋值
  --%>
  <base href="<%=basePath%>">
  <meta charset="UTF-8">
  <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
  <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
  <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <%--入口函数，当整个页面全部加载完后，最后加载入口函数--%>
  <script type="text/javascript">
    $(function () {

      //给整个浏览器窗口添加键盘按下事件
      //实现按下回车键时发送请求的功能
      $(window).keydown(function (e) {
        //如果按的是回车键，则提交登录请求
        if(e.keyCode==13){
          $("#loginBtn").click();
        }
      });

      //给登录按钮添加单击事件(通过id选择器)
      $("#loginBtn").click(function () {
        //单击后需要进行操作，因此需要放入一个参数
        //1.获取需要的参数
        //有时可能多加空格，所以把前后的空格通过$.trim()去掉
        var loginAct=$.trim($("#loginAct").val());
        var loginPwd=$.trim($("#loginPwd").val());
        var isRemPwd=$("#isRemPwd").prop("checked");
        //2.进行表单验证，不能所有验证都放在后端，否则会加重后端的负担
        if(loginAct==""){
          alert("用户名不能为空");
          return;//结束后面函数的执行
        }
        if(loginPwd==""){
          alert("密码不能为空");
          return;//结束后面函数的执行
        }

        //显示正在验证
        //$("#msg").text("正在努力验证，请稍后...");
        //3.发送ajax异步请求
        $.ajax({
          url:'settings/qx/user/login.do',
          data:{
            loginAct:loginAct,
            loginPwd:loginPwd,
            isRemPwd:isRemPwd
          },
          type:'post',
          dataType:'json',
          //解析对象，渲染页面
          success:function (data) {
            if(data.code=="1"){
              //跳转到业务主页面
              window.location.href="workbench/index.do";
            }else{
              //提示信息
              $("#msg").text(data.message);
            }
          },
          beforeSend:function () {//当ajax向后台发送请求之前，会自动执行本函数；
            //该函数的返回值能够决定ajax是否真正向后台发送请求：
            //如果该函数返回true,则ajax会真正向后台发送请求；否则，如果该函数返回false，则ajax放弃向后台发送请求。
            $("#msg").text("正在努力验证....");
            return true;
          }
        });
      });
    });
  </script>
</head>
<body>
<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
  <img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
</div>
<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
  <div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">&copy;2019&nbsp;动力节点</span></div>
</div>

<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
  <div style="position: absolute; top: 0px; right: 60px;">
    <div class="page-header">
      <h1>登录</h1>
    </div>
    <form action="workbench/index.html" class="form-horizontal" role="form">
      <div class="form-group form-group-lg">
        <div style="width: 350px;">
          <%--如果浏览器存在用户名的cookie，说明之前登录过并且选择了免密登录，因此需要将此用户名cookie中的值，也就是将用户名回显在用户名的输入框内--%>
          <input class="form-control" id="loginAct" type="text" value="${cookie.loginAct.value}" placeholder="用户名">
        </div>
        <div style="width: 350px; position: relative;top: 20px;">
          <%--如果浏览器存在用密码的cookie，说明之前登录过并且选择了免密登录，因此需要将此密码cookie中的值，也就是将密码回显在用户名的输入框内--%>
          <input class="form-control" id="loginPwd" type="password" value="${cookie.loginPwd.value}" placeholder="密码">
        </div>
        <div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
          <label>
            <%--通过判断浏览器是否携带关于用户名和账号的cookie，如果存在的话，则应勾选上此框；有一个不存在，则不勾选
                对于条件判断，应当使用jstl标签库进行判断，应当引入jstl标签库
            --%>
            <c:if test="${not empty cookie.loginAct and not empty cookie.loginPwd}">
              <input type="checkbox" id="isRemPwd" checked>
            </c:if>
            <c:if test="${empty cookie.loginAct or empty cookie.loginPwd}">
              <input type="checkbox" id="isRemPwd">
            </c:if>
            十天内免登录
          </label>
          &nbsp;&nbsp;
          <span id="msg" style="color: red"></span>
        </div>
        <%--
			button中的type属性如果是submit，则会代表发送的是同步请求，而如果是button，则传递异步请求
			分析此处需求，如果用户登陆成功则跳转页面；如果登陆失败，则给予一些提示信息，并不发生页面跳转
			明显此处的请求时异步请求，局部刷新页面，因此button标签的type属性应为button而不是submit

			此按钮需要绑定单击事件，可以在标签内部通过onclick属性进行绑定（属性值为一个函数），但是这种方式
			会使得html和js混在一起，维护起来非常困难，因此html写在html的地方，js在js的地方加（入口函数），通过获取
			html元素的jQuery对象来加事件，因此可以考虑给标签设置id或class等
		--%>
        <button type="button" id="loginBtn" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
      </div>
    </form>
  </div>
</div>
</body>
</html>