package net.minecraft.util.concurrent;

import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class DelegatedTaskExecutor<T> implements ITaskExecutor<T>, AutoCloseable, Runnable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final AtomicInteger flags = new AtomicInteger(0);
    public final ITaskQueue<? super T, ? extends Runnable> queue;
    private final Executor delegate;
    private final String name;

    public static DelegatedTaskExecutor<Runnable> create(Executor p_213144_0_, String p_213144_1_) {
        return new DelegatedTaskExecutor<>(new ITaskQueue.Single<>(new LinkedList<>()), p_213144_0_, p_213144_1_);
    }

    public DelegatedTaskExecutor(ITaskQueue<? super T, ? extends Runnable> queueIn, Executor delegateIn, String nameIn) {
        this.delegate = delegateIn;
        this.queue = queueIn;
        this.name = nameIn;
    }

    private boolean setActive() {
        while (true) {
            int i = this.flags.get();
            if ((i & 3) != 0) {
                return false;
            }

            if (this.flags.compareAndSet(i, i | 2)) {
                break;
            }
        }

        return true;
    }

    private void clearActive() {
        while (true) {
            int i = this.flags.get();
            if (this.flags.compareAndSet(i, i & -3)) {
                break;
            }
        }

    }

    private boolean shouldSchedule() {
        if ((this.flags.get() & 1) != 0) {
            return false;
        } else {
            return !this.queue.isEmpty();
        }
    }

    public void close() {
        while (true) {
            int i = this.flags.get();
            if (this.flags.compareAndSet(i, i | 1)) {
                break;
            }
        }

    }

    private boolean isActive() {
        return (this.flags.get() & 2) != 0;
    }

    private boolean driveOne() {
        if (!this.isActive()) {
            return false;
        } else {
            Runnable runnable = this.queue.poll();
            if (runnable == null) {
                return false;
            } else {
                runnable.run();
                return true;
            }
        }
    }

    public void run() {
        try {
            this.driveWhileDummy();
        } finally {
            this.clearActive();
            this.reschedule();
        }

    }

    private void driveWhileDummy() {
        while (this.driveOne()) {
        }
    }

    public void enqueue(T taskIn) {
        this.queue.enqueue(taskIn);
        this.reschedule();
    }

    private void reschedule() {
        if (this.shouldSchedule() && this.setActive()) {
            try {
                this.delegate.execute(this);
            } catch (Exception var4) {
                try {
                    this.delegate.execute(this);
                } catch (Exception rejectedexecutionexception) {
                    LOGGER.error("Cound not schedule mailbox", (Throwable) rejectedexecutionexception);
                }
            }
        }

    }

    private int driveWhile(Int2BooleanFunction p_213145_1_) {
        int i;
        for (i = 0; p_213145_1_.get(i) && this.driveOne(); ++i) {
            ;
        }

        return i;
    }

    public String toString() {
        return this.name + " " + this.flags.get() + " " + this.queue.isEmpty();
    }

    public String getName() {
        return this.name;
    }
}
