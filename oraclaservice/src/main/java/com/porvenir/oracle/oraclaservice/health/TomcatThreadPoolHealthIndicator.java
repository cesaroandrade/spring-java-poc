package com.porvenir.oracle.oraclaservice.health;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Component("tomcatThreadPool")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class TomcatThreadPoolHealthIndicator implements HealthIndicator {

    private final ObjectProvider<ServletWebServerApplicationContext> contextProvider;

    public TomcatThreadPoolHealthIndicator(
            ObjectProvider<ServletWebServerApplicationContext> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public Health health() {
        ServletWebServerApplicationContext context = contextProvider.getIfAvailable();

        if (context == null || context.getWebServer() == null) {
            return Health.up()
                    .withDetail("webServer", "not-initialized")
                    .build();
        }

        if (!(context.getWebServer() instanceof TomcatWebServer tomcat)) {
            return Health.up()
                    .withDetail("webServer", "not-tomcat")
                    .build();
        }

        var executor = tomcat.getTomcat()
                .getConnector()
                .getProtocolHandler()
                .getExecutor();

        if (executor instanceof ThreadPoolExecutor tpe) {
            int active = tpe.getActiveCount();
            int max = tpe.getMaximumPoolSize();

            if (max > 0 && active >= max * 0.9) {
                return Health.down()
                        .withDetail("activeThreads", active)
                        .withDetail("maxThreads", max)
                        .build();
            }
        }

        return Health.up().build();
    }
}
