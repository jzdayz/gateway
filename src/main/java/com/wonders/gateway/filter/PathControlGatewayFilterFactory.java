package com.wonders.gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.cloud.gateway.support.GatewayToStringStyler.filterToStringCreator;

/**
 * @author huqingfeng
 */
@Slf4j
public class PathControlGatewayFilterFactory
        extends AbstractGatewayFilterFactory<PathControlGatewayFilterFactory.Config> {

    public PathControlGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter() {
            @SuppressWarnings("UastIncorrectHttpHeaderInspection")
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                ServerHttpRequest req = exchange.getRequest();
                if (!req.getURI().getPath().startsWith(config.getPath())){
                    return chain.filter(exchange);
                }
                List<String> routeHeader = req.getHeaders().get(config.getHeaderName());
                if (CollectionUtils.isEmpty(routeHeader) || !routeHeader.contains(config.getHeaderValue())) {
                    exchange.getResponse().setStatusCode(HttpStatus.valueOf(config.getErrorCode()));
                    return exchange.getResponse().setComplete();
                }
                return chain.filter(exchange);
            }

            @Override
            public String toString() {
                return filterToStringCreator(PathControlGatewayFilterFactory.this)
                        .append(config.getHeaderName())
                        .append(config.getHeaderValue())
                        .toString();
            }
        };
    }

    @Data
    public static class Config {

        private String path;

        private String headerName;

        private String headerValue;

        private Integer errorCode;

    }

}
