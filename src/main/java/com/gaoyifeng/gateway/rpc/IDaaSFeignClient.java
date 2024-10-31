package com.gaoyifeng.gateway.rpc;


import com.gaoyifeng.gateway.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient("IDaaS")
public interface IDaaSFeignClient {

    @PostMapping("/api/IDaaS/auth/verify")
    Response<Map<String,String>> verify(@RequestHeader("Authorization") String Authorization);


}
