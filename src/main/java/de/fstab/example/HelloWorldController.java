package de.fstab.example;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

  @Autowired
  MeterRegistry registry;

  @RequestMapping("/")
  String home() {
    logger.warn("this is a warning");
    logger.error("this is an error");
    Metrics.counter("fabianGlobal").increment();
    registry.counter("fabianDepInj").increment();
    return "Hello World!";
  }
}
