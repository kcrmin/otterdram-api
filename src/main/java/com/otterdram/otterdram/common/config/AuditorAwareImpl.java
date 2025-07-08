package com.otterdram.otterdram.common.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        // TODO: Implement logic to retrieve the current user's ID
        // Replace with actual logic to get the current user's ID
        return Optional.of(1L);
    }
}
