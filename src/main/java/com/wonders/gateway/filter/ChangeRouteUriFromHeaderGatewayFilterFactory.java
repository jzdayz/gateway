package com.wonders.gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.springframework.cloud.gateway.support.GatewayToStringStyler.filterToStringCreator;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.containsEncodedParts;

/**
 * @author huqingfeng
 */
@Slf4j
public class ChangeRouteUriFromHeaderGatewayFilterFactory
        extends AbstractGatewayFilterFactory<ChangeRouteUriFromHeaderGatewayFilterFactory.Config> {

    public ChangeRouteUriFromHeaderGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("headerName");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter() {
            @SuppressWarnings("UastIncorrectHttpHeaderInspection")
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                ServerHttpRequest req = exchange.getRequest();
                List<String> routeHeader = req.getHeaders().get(config.getHeaderName());
                Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
                if (CollectionUtils.isEmpty(routeHeader) || route == null) {
                    return chain.filter(exchange);
                }
                URI routeUri = route.getUri();
                boolean encoded = containsEncodedParts(routeUri);
                URI newURI = UriComponentsBuilder.fromUri(routeUri)
                        .host(routeHeader.get(0)).build(encoded).toUri();
                exchange.getAttributes().put(GATEWAY_ROUTE_ATTR, newRouteHost(newURI, route));
                return chain.filter(exchange);
            }

            @Override
            public String toString() {
                return filterToStringCreator(ChangeRouteUriFromHeaderGatewayFilterFactory.this).append(config.getHeaderName())
                        .toString();
            }
        };
    }

    private Route newRouteHost(URI newURI, Route old) {
        return Route.async()
                .id(old.getId())
                .uri(newURI)
                .metadata(old.getMetadata())
                .order(old.getOrder())
                .replaceFilters(old.getFilters())
                .asyncPredicate(old.getPredicate()).build();
    }

    @Data
    public static class Config {

        private String headerName;

    }

}
