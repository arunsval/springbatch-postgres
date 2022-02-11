package com.arun.demo.controller;

import com.arun.demo.entity.TestTable;
import com.arun.demo.repository.TestTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.stream.IntStream;

@RestController
@RequiredArgsConstructor
public class BatchController {
private final TestTableRepository testTableRepository;
    @GetMapping("/pushToPostgre")
    public Flux<String> pushToPostgres() {
        IntStream.range(0,Integer.MAX_VALUE).forEach(num ->
                {
                    TestTable test = new TestTable();
                    test.setId(num);
                    test.setName("Hey "+num);
                    testTableRepository.save(test);
                }
                );

        return Flux.empty();
    }
}
