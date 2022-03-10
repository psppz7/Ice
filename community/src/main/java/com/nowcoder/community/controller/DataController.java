package com.nowcoder.community.controller;

import com.nowcoder.community.service.DataService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class DataController {

    @Autowired
    DataService dataService;

    @RequestMapping(path = "/data",method = RequestMethod.GET)
    public String getDataPage()
    {
        return "/site/admin/data";
    }
    @RequestMapping(path = "/data",method = RequestMethod.POST)
    @ResponseBody
    public String getData(String type, @DateTimeFormat(pattern = "yyyy-MM-dd") Date start , @DateTimeFormat(pattern = "yyyy-MM-dd")Date end)
    {
        long data = 0;
        if(type.equals("uv"))
            data = dataService.calulateUv(start,end);
        else if(type.equals("dau"))
            data = dataService.calulateDau(start,end);
        return CommunityUtil.getJsonString(0,String.valueOf(data),null);
    }
}
