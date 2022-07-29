<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--由于html页面默认使用的iso859-1字符集,jsp默认使用utf-8字符集
	因此前端做好的html页面如果要改为动态jsp页面，不能只更改网页名称，否则会出现中文乱码的情况
	应当新建一个临时的jsp，把它的文件头取代html页面的文件头，最后将html页面命名为jsp页面才可以避免这种情况

-->
<html>
<head>
<meta charset="UTF-8">
</head>
<body>
	<%--访问该应用欢迎页 / ，则默认再跳转至登录页面--%>
	<%--因为这个index.jsp是从首页转发过来的，url为根目录/crm/
	有个规则就是不加/就是你的当前页面的地址加你的目标地址构成新地址，所以这个herf就是个完整的地址
	--%>
	<script type="text/javascript">

		window.location.href = "settings/qx/user/toLogin.do";
	</script>
</body>
</html>