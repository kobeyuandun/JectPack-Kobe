package com.jetpack.demo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yuandunbin
 * @date 2022/10/23
 * 一个用原子类修饰，一个volatile 修饰，在多线程的情况下做自增，然后输出最后的值。
 */
public class AtomicDemo {
    public static void main(String[] args) throws InterruptedException {
        AtomicTask atomicTask = new AtomicTask();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    atomicTask.atomicIncrement();
                    atomicTask.volatileIncrement();
                }
            }
        };
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        thread1.start();
        thread2.start();
        thread1.join();
        thread1.join();
        System.out.println("原子类的结果："+atomicTask.atomicInteger.get());
        System.out.println("volatile 的结果："+atomicTask.volatileCount);
    }

    static class AtomicTask {
        AtomicInteger atomicInteger = new AtomicInteger();
        volatile int volatileCount = 0;

        void atomicIncrement() {
            atomicInteger.getAndIncrement();
        }

        void volatileIncrement() {
            volatileCount++;
        }

    }
}
