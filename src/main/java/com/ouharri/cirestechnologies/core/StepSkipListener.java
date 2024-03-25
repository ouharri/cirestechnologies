package com.ouharri.cirestechnologies.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ouharri.cirestechnologies.model.entities.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.SkipListener;

@Slf4j
public class StepSkipListener implements SkipListener<User, Number> {

    @Override
    public void onSkipInRead(Throwable throwable) {
        log.info("A failure on read {} ", throwable.getMessage());
    }

    @Override
    public void onSkipInWrite(@NotNull Number item, Throwable throwable) {
        log.info("A failure on write {} , {}", throwable.getMessage(), item);
    }

    @Override
    @SneakyThrows
    public void onSkipInProcess(@NotNull User user, Throwable throwable) {
        log.info("Item {}  was skipped due to the exception  {}", new ObjectMapper().writeValueAsString(user),
                throwable.getMessage());
    }
}