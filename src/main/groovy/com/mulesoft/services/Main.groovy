package com.mulesoft.services

import com.mulesoft.services.backend.AnypointConnection
import com.mulesoft.services.ui.ConnectionProperties
import com.mulesoft.services.ui.MainUI
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by juancavallotti on 1/16/17.
 */
class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main)

    public static void main(String[] args) {

        logger.info 'Anypoint Exchange Migration Tool'
        logger.info 'Creating Main UI...'

        new MainUI().build()



    }

}
