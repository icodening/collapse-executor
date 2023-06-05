package cn.icodening.collapse.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author icodening
 * @date 2023.05.16
 */
public class SuspendableListenableCollector extends ListenableCollector {

    private static final TimeUnit DEFAULT_UNIT = TimeUnit.MILLISECONDS;

    private volatile int threshold = 0;

    private long duration = 0;

    private TimeUnit timeUnit = DEFAULT_UNIT;

    public SuspendableListenableCollector(Executor dispatcher) {
        super(dispatcher);
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = Objects.requireNonNull(timeUnit, "timeUnit must be not null.");
    }

    @Override
    protected Collection<Bundle<Object, Object>> onCollecting(Iterator<Bundle<Object, Object>> iterator) {
        Collection<Bundle<Object, Object>> bundles = super.onCollecting(iterator);
        if (bundles.size() >= threshold) {
            return bundles;
        }
        if (duration == 0) {
            Thread.yield();
        } else if (duration > 0) {
            pause(duration);
        }
        bundles.addAll(super.onCollecting(iterator));
        return bundles;
    }

    private void pause(long duration) {
        try {
            timeUnit.sleep(duration);
        } catch (InterruptedException ignore) {
        }
    }
}
