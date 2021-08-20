package com.wonders.gateway.config;

import com.wonders.gateway.filter.ChangeRouteUriFromHeaderGatewayFilterFactory;
import com.wonders.gateway.filter.PathControlGatewayFilterFactory;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    @ConditionalOnEnabledFilter
    public ChangeRouteUriFromHeaderGatewayFilterFactory changeHostFromHeaderGatewayFilterFactory() {
        return new ChangeRouteUriFromHeaderGatewayFilterFactory();
    }
    @Bean
    @ConditionalOnEnabledFilter
    public PathControlGatewayFilterFactory pathControlGatewayFilterFactory() {
        return new PathControlGatewayFilterFactory();
    }

}
