$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	//获取标题和内容

	var title = $("#recipient-name").val()
	var content = $("#message-text").val()

	//发送异步请求
	$.post(
		Context_Path + "/discuss/add",
	    {"title":title,"content":content}
		,function (data){
			data = $.parseJSON(data)
			//在提示框当中显示消息
			$("#hintBody").text(data.msg)
			//显示提示框后2s自动隐藏提示框
			$("#hintModal").modal("show");
			setTimeout(function(){
				if(data.code == 0)
					window.location.reload()
				$("#hintModal").modal("hide");
			}, 2000);
		}
	);


}