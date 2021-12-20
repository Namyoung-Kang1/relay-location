package com.lucis.relaylocation;

import com.lucis.relaylocation.service.LocationMapService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RelayLocationApplicationTests {

    @Autowired
    private LocationMapService locationMapService;

    @Test
    void contextLoads() {
//        locationMapService.getAddress("126.978275264", "37.566642192");
    }

}
