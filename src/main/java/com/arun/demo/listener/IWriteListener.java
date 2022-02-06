package com.arun.demo.listener;

import com.arun.demo.entity.TestTable;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class IWriteListener implements ItemWriteListener<TestTable> {

    @Override
    public void beforeWrite(List<? extends TestTable> items) {

    }

    @Override
    public void afterWrite(List<? extends TestTable> items) {
        items.stream().map(TestTable::getName).forEach(log::info);
    }

    @Override
    public void onWriteError(Exception exception, List<? extends TestTable> items) {

    }
}
