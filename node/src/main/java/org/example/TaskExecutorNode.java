package org.example;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class TaskExecutorNode {
    public static void main(String[] args) {
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();
        HazelcastInstanceHolder.setInstance(hazelcast);

        System.out.println("Node is up and running...");
    }

    public static class HazelcastInstanceHolder {
        private static HazelcastInstance instance;

        public static void setInstance(HazelcastInstance instance) {
            HazelcastInstanceHolder.instance = instance;
        }

        public static HazelcastInstance getInstance() {
            return instance;
        }
    }
}
