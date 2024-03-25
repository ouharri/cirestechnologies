package com.ouharri.cirestechnologies.core;

import com.ouharri.cirestechnologies.model.entities.User;
import com.ouharri.cirestechnologies.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class userItemWriter implements ItemWriter<User> {

    private final UserRepository repository;

    @Override
    public void write(@NotNull Chunk<? extends User> chunk) throws Exception {
        System.out.println("Writer Thread " + Thread.currentThread().getName());
        repository.saveAll(chunk);
    }
}
