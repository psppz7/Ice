package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {


    int insertLoginTicket(LoginTicket loginTicket);


    LoginTicket selectLoginTicket(String ticket);

    int updateStatus(String ticket,int status);

}
