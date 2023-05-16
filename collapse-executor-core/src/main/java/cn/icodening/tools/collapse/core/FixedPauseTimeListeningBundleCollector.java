package cn.icodening.tools.collapse.core;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author icodening
 * @date 2023.05.16
 */
public class FixedPauseTimeListeningBundleCollector extends ListeningBundleCollector {

    private static final TimeUnit DEFAULT_UNIT = TimeUnit.MILLISECONDS;

    private final long duration;

    private final TimeUnit timeUnit;

    public FixedPauseTimeListeningBundleCollector(Executor dispatcher, long duration) {
        this(dispatcher, duration, DEFAULT_UNIT);
    }

    public FixedPauseTimeListeningBundleCollector(Executor dispatcher, long duration, TimeUnit timeUnit) {
        super(dispatcher);
        this.duration = duration;
        this.timeUnit = Objects.requireNonNull(timeUnit, "timeUnit must be not null.");
    }

    @Override
    protected void preparedCollect() {
        super.preparedCollect();
        if (duration == 0) {
            Thread.yield();
            return;
        }
        if (duration > 0) {
            try {
                timeUnit.sleep(duration);
            } catch (InterruptedException ignore) {
            }
        }
    }
}
