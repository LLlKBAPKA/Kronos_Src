package org.excellent.client.api.interfaces;

import org.excellent.common.impl.thread.ThreadPool;

public interface IExecutor {
    ThreadPool THREAD_POOL = new ThreadPool();
}
