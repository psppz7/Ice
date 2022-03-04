function like(btn,entityType,entityId,entityUserId,discussPostId)
{
    $.post(
        Context_Path + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"discussPostId":discussPostId},
        function(data)
        {
            data = $.parseJSON(data)
            if(data.code ==0) //成功返回
            {
                $(btn).children("i").text(data.likeCount)
                $(btn).children("b").text(data.likeStatus==1?"已赞":"赞")
            }
            else
            {
                alert(data.msg)
            }
        }
    )
}