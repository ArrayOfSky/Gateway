package com.gaoyifeng.gateway.filter;

import com.gaoyifeng.gateway.model.Response;
import com.gaoyifeng.gateway.rpc.IDaaSFeignClient;
import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


@Component
public class DefaultHeaderFilter implements GlobalFilter {

    @Resource
    private IDaaSFeignClient iDaaSFeignClient;

    private List<String> whiteList = new ArrayList<>();

    public DefaultHeaderFilter() {
        whiteList.add("/api/IDaaS/auth/authorize");
        whiteList.add("/api/IDaaS/auth/verify");
        whiteList.add("/api/IDaaS/auth/renewval");

        whiteList.add("/api/IDaaS/code/getCode");
        whiteList.add("/api/IDaaS/code/getCodeType");
        whiteList.add("/api/IDaaS/code/validCode");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //根据url判断白名单，白名单直接放行
        // 获取请求路径
        String path = exchange.getRequest().getURI().getPath();

        // 根据url判断白名单，白名单直接放行
        if (whiteList.contains(path)) {
            System.out.println("直接方向");
            return chain.filter(exchange);
        }

        Response<Map<String,String>> token = iDaaSFeignClient.verify("123456");

        Map<String, String> data = token.getData();
        // 获取request和response 将data中的所有entity注入到request的header中

        // 获取request和response
        ServerHttpRequest request = exchange.getRequest();

        // 将data中的所有entity注入到request的header中
        ServerHttpRequest.Builder requestBuilder = request.mutate();
        data.forEach((key, value) -> requestBuilder.header(key, value));

        // 构建新的request并替换原有的request
        ServerHttpRequest newRequest = requestBuilder.build();

        return chain.filter(exchange.mutate().request(newRequest).build())
                .then(Mono.fromRunnable(() -> {
                    System.out.println("响应");
                    // 可以在这里处理response
                }));
    }


}
