var TT = TAOTAO = {
	checkLogin : function(){
		//获取cookie中存储用户身份唯一标识token
		var _ticket = $.cookie("E3_TOKEN");
		if(!_ticket){
			return ;
		}
		//发送ajax请求,第六步,发送ajax请求,把token传递给单点登录系统
		$.ajax({
			url : "http://localhost:8088/user/token/" + _ticket,
			dataType : "jsonp",
			type : "GET",
			success : function(data){
				if(data.status == 200){
					var username = data.data.username;
					var html = username + "，欢迎来到宜立方！<a href=\"http://www.taotao.com/user/logout.html\" class=\"link-logout\">[退出]</a>";
					$("#loginbar").html(html);
				}
			}
		});
	}
}

$(function(){
	// 查看是否已经登录，如果已经登录查询登录信息
	TT.checkLogin();
});