package com.mulesoft.services.backend

import groovy.beans.Bindable
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by juancavallotti on 1/16/17.
 */
class AnypointConnection implements ApiAccessor {

    private static final Logger logger = LoggerFactory.getLogger(AnypointConnection)


    @Bindable String username
    @Bindable String password
    @Bindable String anypointHost = 'anypoint.mulesoft.com'
    @Bindable Boolean ignoreSslErrors = true

    private String accessToken

    public boolean login() {

        HTTPBuilder builder = getPlatformAPIConnection()

        builder.post(path: '/accounts/login', query: [username: username, password: password]) { resp ->


            def jsonResp = fromJsonResponse(resp)

            accessToken = jsonResp.access_token

            logger.debug("Response is: $jsonResp")
        }


    }

    public def getOrganizations() {

    }

    HTTPBuilder getPlatformAPIConnection() {

        HTTPBuilder builder = new HTTPBuilder("https://$anypointHost")

        if (ignoreSslErrors) {
            builder.ignoreSSLIssues()
        }

        return builder
    }

    HTTPBuilder getPlatformAuthAPIConnection() {

        HTTPBuilder ret = getPlatformAPIConnection()

        ret.headers.put('Authorization', "Bearer $accessToken")

        return ret
    }

    public String getAccessToken() {
        return accessToken
    }

}
