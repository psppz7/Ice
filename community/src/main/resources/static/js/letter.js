$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");
	var toUser = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(Context_Path + "/send/message",
		{"toUsername":toUser,"content":content}
		,function (data)
		{
			data = $.parseJSON(data)
			$("#hintModal").modal("show");
			$("#hintBody").text(data.msg)
			setTimeout(function () {
				$("#hintModal").modal("hide");
				if(data.code==1)
					window.location.reload()
				}, 2000);

		}
		)

}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}