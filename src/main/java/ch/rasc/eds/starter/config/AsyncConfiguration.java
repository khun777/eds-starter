package ch.rasc.eds.starter.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration extends AsyncConfigurerSupport {

	@Autowired
	private AsyncProperties properties;

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		if (properties.getCorePoolSize() != null) {
			executor.setCorePoolSize(properties.getCorePoolSize());
		}

		if (properties.getMaxPoolSize() != null) {
			executor.setMaxPoolSize(properties.getMaxPoolSize());
		}

		if (properties.getQueueCapacity() != null) {
			executor.setQueueCapacity(properties.getQueueCapacity());
		}

		if (properties.getThreadNamePrefix() != null) {
			executor.setThreadNamePrefix(properties.getThreadNamePrefix());
		}

		if (properties.getAllowCoreThreadTimeOut() != null) {
			executor.setAllowCoreThreadTimeOut(properties.getAllowCoreThreadTimeOut());
		}

		if (properties.getWaitForTasksToCompleteOnShutdown() != null) {
			executor.setWaitForTasksToCompleteOnShutdown(properties
					.getWaitForTasksToCompleteOnShutdown());
		}

		if (properties.getAwaitTerminationSeconds() != null) {
			executor.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());
		}

		if (properties.getKeepAliveSeconds() != null) {
			executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
		}

		executor.initialize();
		return executor;
	}

}
