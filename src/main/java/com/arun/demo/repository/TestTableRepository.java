package com.arun.demo.repository;

import com.arun.demo.entity.TestTable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TestTableRepository extends PagingAndSortingRepository<TestTable,Integer> {
    List<TestTable> findAll();
}
