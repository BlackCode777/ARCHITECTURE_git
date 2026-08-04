package com.tesseracode_labs.fundamentos_2semanas.metrics.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de métricas customizadas expostas via Actuator/Prometheus.
 * Ver docs/07-metrics-service.md.
 */
@Configuration
public class MetricsConfig {

	@Bean
	public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
		return registry -> registry.config()
				.meterFilter(MeterFilter.commonTags("application", "fundamentos-2semanas"));
	}
}
