package com.arun.demo.partitioner;

import com.arun.demo.config.ReaderConfig;
import com.arun.demo.repository.TestTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MyCustomPartitioner implements Partitioner {
    private final TestTableRepository testTableRepository;
    private final ReaderConfig readerConfig;

    private long recordCount(){
        return testTableRepository.count();
    }
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitionContext = new HashMap<>(gridSize);
        long totalRecords = recordCount();
        int startOffset = 0;
        int partitionPageSize = (int)(totalRecords/gridSize) + 1;
        for(int i=1;i<=gridSize;i++){
            ExecutionContext context = new ExecutionContext();
            context.put("partitionNumber",i);
            context.put("recordStart",startOffset);
            context.put("partitionPageSize",partitionPageSize);
            partitionContext.put("partition"+i,context);
            startOffset = startOffset + partitionPageSize + 1;
        }
        return partitionContext;
    }
}
