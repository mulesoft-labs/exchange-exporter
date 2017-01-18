package com.mulesoft.services.batch

/**
 * Created by juancavallotti on 1/18/17.
 */
interface ProgressListener {

    void jobStarted(String name)

    void updateProgressNotification(int value)

    void jobCompleted(String name)

    void jobErrored(String name, String errorMessage)
}