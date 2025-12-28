package com.jetpack.demo;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yuandunbin
 * @date 2022/10/24
 * 生产者和消费者的场景，利用ReentrantLock condition 条件对象，能够指定唤醒某个线程去工作。
 * <p>
 * 生产者是个 boss, 去生产砖，砖的序列号是偶数，工人2去搬， 奇数号让工人1 去搬。
 * 消费者是两个工人，有砖搬就搬，没砖搬就休息。
 */
class ReentrantLockDemo {
    static class ReentrantLockTask {
        private final Condition work1Condition;
        private final Condition work2Condition;
        volatile int flag = 0; // 砖的序列号
        ReentrantLock reentrantLock = new ReentrantLock(true);

        public ReentrantLockTask() {
            work1Condition = reentrantLock.newCondition();
            work2Condition = reentrantLock.newCondition();
        }

        void work1() {
            try {
                reentrantLock.lock();
                if (flag == 0 || flag % 2 == 0) {
                    System.out.println("work1 无砖可搬，休息会");
                    work1Condition.await();
                    System.out.println("work1 搬的砖是：" + flag);
                    flag = 0;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();

            }
        }

        void work2() {
            try {
                reentrantLock.lock();
                if (flag == 0 || flag % 2 == 0) {
                    System.out.println("work2 无砖可搬，休息会");
                    work2Condition.await();
                    System.out.println("work2 搬的砖是：" + flag);
                    flag = 0;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();

            }
        }

        void boss() {
            try {
                reentrantLock.lock();
                flag = new Random().nextInt(100);
                if (flag % 2 == 0) {
                    work2Condition.signal();
                    System.out.println("生产出来了砖，唤醒工人2 去搬：" + flag);
                } else {
                    work1Condition.signal();
                    System.out.println("生产出来了砖，唤醒工人1 去搬：" + flag);
                }
            } finally {
                reentrantLock.unlock();
            }
        }

        public static void main(String[] args) {
            ReentrantLockTask reentrantLockTask = new ReentrantLockTask();
            new Thread(() -> {
                while (true) {
                    reentrantLockTask.work1();
                }
            }).start();
            new Thread(() -> {
                while (true) {
                    reentrantLockTask.work2();
                }
            }).start();

            for (int i = 0; i < 10; i++) {
                reentrantLockTask.boss();
            }
        }
    }
}
