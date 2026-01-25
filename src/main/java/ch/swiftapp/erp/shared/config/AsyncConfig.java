package ch.swiftapp.erp.shared.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Async and scheduling configuration for the ERP system.
 *
 * <p>Defines two dedicated thread pools to keep notification/mail processing
 * isolated from the web request thread pool:</p>
 * <ul>
 *   <li>{@code notificationExecutor} — handles transactional domain-event-driven
 *       notifications (in-app + email) fired after business TX commits</li>
 *   <li>{@code mailBatchExecutor} — handles rate-limited mass-mail campaigns</li>
 * </ul>
 *
 * <p>Never share these executors with the web layer ({@code spring.task.execution.*}).</p>
 */
@Configuration
@EnableAsync
@EnableScheduling
@Slf4j
public class AsyncConfig {

    @Value("${app.notification.executor.core-size:4}")
    private int notifCoreSize;

    @Value("${app.notification.executor.max-size:10}")
    private int notifMaxSize;

    @Value("${app.notification.executor.queue-capacity:500}")
    private int notifQueueCapacity;

    /**
     * Dedicated executor for domain-event-driven notification processing.
     *
     * <p>Used by {@code @Async("notificationExecutor")} on event listeners.
     * Uses {@link ThreadPoolExecutor.CallerRunsPolicy} — if the queue fills up,
     * the calling thread processes the task rather than dropping it.</p>
     */
    @Bean("notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(notifCoreSize);
        executor.setMaxPoolSize(notifMaxSize);
        executor.setQueueCapacity(notifQueueCapacity);
        executor.setThreadNamePrefix("notif-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        log.info("Notification executor initialized: core={}, max={}, queue={}",
                notifCoreSize, notifMaxSize, notifQueueCapacity);
        return executor;
    }

    /**
     * Dedicated executor for bulk mass-mail campaign processing.
     *
     * <p>Kept small to naturally rate-limit outgoing SMTP traffic.
     * Uses {@link ThreadPoolExecutor.DiscardOldestPolicy} to avoid memory pressure
     * when campaigns queue up.</p>
     */
    @Bean("mailBatchExecutor")
    public Executor mailBatchExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("mail-batch-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        log.info("Mail batch executor initialized: core=2, max=4, queue=100");
        return executor;
    }
}

