package com.mulesoft.services.ui

import java.util.prefs.Preferences

/**
 * Created by juancavallotti on 1/19/17.
 */
class SettingsStore {

    public static final String ANYPOINT_USERNAME = 'ANYPOINT_USERNAME'
    public static final String ANYPOINT_HOST = 'ANYPOINT_HOST'
    Preferences prefs

    SettingsStore() {
        prefs = Preferences.userNodeForPackage(com.mulesoft.services.ui.SettingsStore)
    }

    public void setUsername(String username) {
        prefs.put(ANYPOINT_USERNAME, username)
    }

    public String getUsername() {
        prefs.get(ANYPOINT_USERNAME, '')
    }

    public void setHostName(String hostName) {
        prefs.put(ANYPOINT_HOST, hostName)
    }

    public String getHostName() {
        prefs.get(ANYPOINT_HOST, 'anypoint.mulesoft.com')
    }

    public void update() {
        prefs.flush()
        prefs.sync()
    }

}
