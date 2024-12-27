package org.example;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class TaskExecutor {
    private final HazelcastInstance hazelcast;

    public TaskExecutor(HazelcastInstance hazelcast) {
        this.hazelcast = hazelcast;
    }

    public void executeTasks(int rows, int cols) {
        IExecutorService executorService = hazelcast.getExecutorService("matrix-multiplication");
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int row = i;
                int col = j;

                Callable<Integer> task = new MatrixMultiplicationTask(row, col);
                futures.add(executorService.submit(task));
            }
        }

        // Wait for all tasks to complete
        for (Future<Integer> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class MatrixMultiplicationTask implements Callable<Integer>, Serializable {
        private final int row;
        private final int col;

        public MatrixMultiplicationTask(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public Integer call() {
            HazelcastInstance hazelcast = TaskExecutorNode.HazelcastInstanceHolder.getInstance();
            int[] rowA = (int[])hazelcast.getMap("matrixA").get(row);
            int[] colB = (int[])hazelcast.getMap("matrixB").get(col);

            int sum = 0;
            for (int k = 0; k < rowA.length; k++) {
                sum += rowA[k] * colB[k];
            }

            hazelcast.getMap("result").put(row + "," + col, sum);
            return sum;
        }
    }
}
