package com.mulesoft.services.ui

import com.mulesoft.services.backend.AnypointConnection
import com.mulesoft.services.batch.Executor
import groovy.swing.SwingBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JCheckBox
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JTextField

/**
 * Created by juancavallotti on 1/16/17.
 */
class ConnectionProperties {

    private final Logger logger = LoggerFactory.getLogger(ConnectionProperties)

    AnypointConnection model

    ConnectionListener listener

    JFrame parent
    JTextField userField
    JTextField passwordField
    JTextField hostField
    JCheckBox sslIssues

    Executor executor

    ConnectionProperties(ConnectionListener listener, JFrame parent, Executor executor) {
        this.listener = listener
        this.parent = parent
        this.executor = executor
    }


    public void build() {

        model = new AnypointConnection()

        new SwingBuilder().edt {

            dialog(title: 'Anypoint Platform Connection', size: [400, 140], locationRelativeTo: null, show: true, owner: parent) {
                gridLayout cols: 2, rows: 5

                label text: 'Username: ', horizontalAlignment: JLabel.RIGHT
                userField = textField text: bind(source: model, sourceProperty: 'username')

                label text: 'Password: ', horizontalAlignment: JLabel.RIGHT
                passwordField = passwordField text: bind(source: model, sourceProperty: 'password')

                label text: 'Host: ', horizontalAlignment: JLabel.RIGHT
                hostField = textField text: bind(source: model, sourceProperty: 'anypointHost')

                label text: 'Ignore SSL Issues: ', horizontalAlignment: JLabel.RIGHT
                sslIssues = checkBox selected: bind(source: model, sourceProperty: 'ignoreSslErrors')

                button text: 'Exit', actionPerformed: {

                    System.exit(0)

                }
                button text: 'Connect', actionPerformed: {

                    model.username = userField.text
                    model.password = passwordField.text
                    model.anypointHost = hostField.text
                    model.ignoreSslErrors = sslIssues.selected

                    logger.debug("Connection properties: Username: $model.username, Host: $model.anypointHost")

                    listener.onConnectionPropertiesSet(model)

                    dispose()
                }
            }

        }

    }

}
