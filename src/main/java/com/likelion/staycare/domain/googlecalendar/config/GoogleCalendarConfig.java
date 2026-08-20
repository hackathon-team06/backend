package com.likelion.staycare.domain.googlecalendar.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GoogleCalendarProperties.class)
public class GoogleCalendarConfig {
}
