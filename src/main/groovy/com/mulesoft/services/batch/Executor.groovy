package com.mulesoft.services.batch

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

/**
 * Created by juancavallotti on 1/17/17.
 */
class Executor {

    private static final Logger logger = LoggerFactory.getLogger(Executor)

    private final ThreadPoolExecutor executor

    int poolSize = 10

    ProgressListener progressListener

    public Executor(ProgressListener progressListener) {
        logger.debug("Created instance of Executor Service with $poolSize threads")
        executor = Executors.newFixedThreadPool(poolSize)
        this.progressListener = progressListener
    }

    public void schedule(BaseBatchJob r) {
        schedule(r, null)
    }

    public void schedule(BaseBatchJob r, String name) {
        if (name) {
            r.jobName = name
        }
        logger.debug("Will send ${r.toString()} into executor...")
        r.progressListener = progressListener
        executor.execute(r)
    }

}
