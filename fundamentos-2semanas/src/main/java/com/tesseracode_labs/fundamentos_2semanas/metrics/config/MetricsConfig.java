package com.tesseracode_labs.fundamentos_2semanas.metrics.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Configuração de métricas customizadas expostas via Actuator/Prometheus.
 * Ver docs/07-metrics-service.md.
 */
@Component
public class MetricsConfig {

	private final MeterRegistry registry;

	public MetricsConfig(MeterRegistry registry) {
		this.registry = registry;
	}

	@PostConstruct
	public void configurarTagsComuns() {
		registry.config().meterFilter(MeterFilter.commonTags(List.of(Tag.of("application", "fundamentos-2semanas"))));
	}
}
