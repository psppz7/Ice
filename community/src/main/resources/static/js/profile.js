$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	var id = $(btn).prev().val()
	$.post(
		Context_Path + "/follow",
		{"userId":id},
		function (data){
			data = $.parseJSON(data);
			if(data.code==0)
				window.location.reload()
		}
	)
	if($(btn).hasClass("btn-info")) {
		// 关注TA
		$(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
	} else {
		// 取消关注
		$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
	}
}