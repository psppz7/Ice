jQuery(function($){
    $("#cacuUv").click(function(){
        var start = $("#uvstart").val()
        var end = $("#uvend").val()
        var type = $("#uvtype").val()
        $.post(
            Context_Path + "/data",
            {"start":start,"end":end,"type":type},
            function (data)
            {
                data = $.parseJSON(data)
                if(data.code==0)
                    $("#uv").text(data.msg)
            }
        )
    })
})

jQuery(function($){
    $("#cacuDau").click(function(){
        var start = $("#daustart").val()
        var end = $("#dauend").val()
        var type = $("#dautype").val()
        $.post(
            Context_Path + "/data",
            {"start":start,"end":end,"type":type},
            function (data)
            {
                data = $.parseJSON(data)
                if(data.code==0)
                    $("#dau").text(data.msg)
            }
        )
    })
})