package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository
public class AlphaDaoImpl implements AlphaDao{
    @Override
    public String select() {
        return "1234";
    }
}
