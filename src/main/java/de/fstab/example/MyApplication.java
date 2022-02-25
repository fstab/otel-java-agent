package de.fstab.example;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = MyApplication.class)
public class MyApplication {

    /**
     * Expose the OpenTelemetry Java agent's MeterRegistry as a Bean.
     * <p>
     * If you attach the OpenTelemetry Java agent to your Spring application, it will automatically
     * register an {@code OpenTelemetryMeterRegistry} with the {@link Metrics#globalRegistry}.
     * As a result, OTel will pick up all metrics that you access via the static methods in {@link Metrics},
     * for example like this:
     * <pre>
     * Metrics.counter("example").increment();
     * </pre>
     * However, OTel will not pick up metrics that you access via dependency injection like this:
     * <pre>
     * @Autowired
     * MeterRegistry registry;
     *
     * registry.counter("fabianDepInj").increment();
     * </pre>
     * All built-in metrics in Micrometer are accessed via dependency injection, so by default they are
     * not picked up by the OTel Java agent.
     * <p>
     * This method removes the {@code OpenTelemetryMeterRegistry} from {@link Metrics#globalRegistry} and
     * exposes it as a Bean so that it works with dependency injection.
     * <p>
     * Note that all {@link MeterRegistry} exposed via dependency injection will implicitly be registered
     * with {@link Metrics#globalRegistry} by Spring, so access via the static methods in {@link Metrics}
     * will still work (unless you set {@code management.metrics.use-global-registry=false}).
     *
     * @return The {@code OpenTelemetryMeterRegistry} coming from the OpenTelemetry Java agent,
     *         or {@code null} if the agent is not attached.
     */
    @Bean
    public MeterRegistry otelRegistry() {
        Optional<MeterRegistry> otelRegistry = Metrics.globalRegistry.getRegistries().stream()
            .filter(r -> r.getClass().getName().contains("OpenTelemetryMeterRegistry"))
            .findAny();
        otelRegistry.ifPresent(Metrics.globalRegistry::remove);
        return otelRegistry.orElse(null);
    }

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

}
