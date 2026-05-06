package com.start_spring_here.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PurchaseRepository {
    // when spring boot, during runtime, saw we added jdbc starter dependency and a driver dependency
    // it automatically created a JdbcTemplate and a DataSource instances. JdbcTemplate uses a DataSource object inside
    // its implementation while talking with a database
    
    //    JdbcTemplate
    //    ↓
    //    DataSource
    //    ↓
    //    JDBC Driver
    //    ↓
    //    Database
    private JdbcTemplate jdbcTemplate;

    public PurchaseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


}
