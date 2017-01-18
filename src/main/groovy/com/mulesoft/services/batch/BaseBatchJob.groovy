package com.mulesoft.services.batch

import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * Created by juancavallotti on 1/17/17.
 */
abstract class BaseBatchJob implements Runnable {

    protected final Logger logger = LoggerFactory.getLogger(getClass())

    String jobName = 'Unknown'

    ProgressListener progressListener

    @Override
    void run() {
        logger.debug("Begin execution of Job")

        progressListener?.jobStarted(jobName)

        try {
            progressListener?.updateProgressNotification(0)
            doExecute()
            logger.debug("Completed execution of batch Job")

            progressListener?.jobCompleted(jobName)
            progressListener?.updateProgressNotification(100)

        } catch (Throwable t) {
            logger.debug("Batch job finished in error state: ${t.message}")
            logger.error("Error while executing batch job.", t)
            progressListener?.jobErrored(jobName, t.message)
        }
    }

    abstract void doExecute() throws Exception;

}
