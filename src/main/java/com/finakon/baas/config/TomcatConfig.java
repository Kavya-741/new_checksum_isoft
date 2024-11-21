package com.finakon.baas.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizeTomcat() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.setProperty("relaxedQueryChars", "<>[\\]^`{|}");
            connector.setProperty("relaxedPathChars", "<>[\\]^`{|}");
            connector.setMaxPostSize(20971520); // 20 MB
        });
    }
}