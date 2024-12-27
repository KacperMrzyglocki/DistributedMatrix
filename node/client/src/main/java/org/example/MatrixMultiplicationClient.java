package org.example;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.Random;

public class MatrixMultiplicationClient {
    public static void main(String[] args) {
        // Configure Hazelcast client
        ClientConfig clientConfig = new ClientConfig();
        String hazelcastAddresses = System.getenv("HAZELCAST_ADDRESSES");
        if (hazelcastAddresses == null || hazelcastAddresses.isEmpty()) {
            System.err.println("HAZELCAST_ADDRESSES environment variable is not set.");
            return;
        }
        clientConfig.getNetworkConfig().addAddress(hazelcastAddresses.split(","));
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);

        try {
            int N = 1024; // Matrix size
            int[][] matrixA = new int[N][N];
            int[][] matrixB = new int[N][N];

            // Generate matrices
            generateMatrix(matrixA);
            generateMatrix(matrixB);

            // Store matrices in Hazelcast maps
            IMap<Integer, int[]> rowsMapA = client.getMap("matrixA");
            for (int i = 0; i < N; i++) {
                rowsMapA.put(i, matrixA[i]);
            }

            IMap<Integer, int[]> colsMapB = client.getMap("matrixB");
            for (int j = 0; j < N; j++) {
                int[] col = new int[N];
                for (int i = 0; i < N; i++) {
                    col[i] = matrixB[i][j];
                }
                colsMapB.put(j, col);
            }

            // Distribute tasks and wait for completion
            long startTime = System.currentTimeMillis();
            TaskExecutor executor = new TaskExecutor(client);
            executor.executeTasks(N, N);
            long endTime = System.currentTimeMillis();

            // Retrieve and print the results
            IMap<String, Integer> resultMap = client.getMap("result");
            System.out.println("Result Matrix:");
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    System.out.print(resultMap.get(i + "," + j) + " ");
                }
                System.out.println();
            }

            // Print timing
            System.out.println("Matrix multiplication completed in " + (endTime - startTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.shutdown();
        }
    }

    private static void generateMatrix(int[][] matrix) {
        Random random = new Random();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = random.nextInt(10);
            }
        }
    }
}
