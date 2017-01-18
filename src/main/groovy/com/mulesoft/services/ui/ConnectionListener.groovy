package com.mulesoft.services.ui

import com.mulesoft.services.backend.AnypointConnection

/**
 * Created by juancavallotti on 1/16/17.
 */
interface ConnectionListener {
    void onConnectionPropertiesSet(AnypointConnection connection)
}