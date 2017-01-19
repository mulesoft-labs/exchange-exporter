package com.mulesoft.services.backend

import groovyx.net.http.HTTPBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * Created by juancavallotti on 1/16/17.
 */
class Exchange implements ApiAccessor {

    private static final Logger logger = LoggerFactory.getLogger(Exchange)

    final AnypointConnection connection

    def exchangeInfo

    public static final String API_BASE = '/exchange/api'

    public Exchange(AnypointConnection connection) {

        this.connection = connection

        retrieveExchangeInfo()

    }


    private void retrieveExchangeInfo() {

        HTTPBuilder builder = connection.platformAuthAPIConnection

        //get the current token
        def payload = [access_token: connection.accessToken]

        logger.debug("Trying to get exchange token with token: $payload")


        builder.headers.put('Content-Type', 'application/json')
        builder.post(path: "$API_BASE/exchangeToken", body: toJson(payload)) { resp ->
            exchangeInfo = fromJsonResponse(resp)
        }

    }

    public def availableOrganizations() {
        return exchangeInfo.user.account.memberOfOrganizations
    }

    public def allEntries(def orgObject, int page, int responseSize) {
        allEntries(orgObject, page, responseSize, null)
    }

    public def allEntries(def orgObject, int page, int responseSize, String filter) {

        HTTPBuilder builder = configureExchangeAccess(connection.platformAuthAPIConnection)

        def query = [page: page, responseSize: responseSize, includeChildren: false]

        if (filter?.trim()?.length()) {
            query.put('searchTerms', filter)
        }

        if (!orgObject.value) {
            //return public content
            query.put('include', 'public')

            //harcoded org ID for mulesoft public, otherwise it will show content from everywhere, which I don't want.
            query.put('organizations', '68ef9520-24e9-4cf2-b2f5-620025690913')
        } else {
            query.put('organizations', orgObject.value.id)
        }

        def ret = [:]

        logger.debug("Get exchange entries, query string is: $query")

        builder.get(path: "$API_BASE/objects", query: query) { resp ->
            ret = fromJsonResponse(resp)
        }

        return ret
    }

    /**
     * Create
     * @param String
     */
    public void createEntry(String jsonEntry, def orgObject, boolean publish) {

        if (!orgObject.value) {
            throw new IllegalArgumentException("Cannot publish items to public exchange!")
        }


        HTTPBuilder builder = configureExchangeAccess(connection.platformAPIConnection)

        builder.headers.put('Content-Type', 'application/json')
        builder.post(path: "$API_BASE/organizations/${orgObject.value.id}/objects", body: jsonEntry) { resp ->
            logger.debug("Successfully created entry.")
        }

    }

    private String getExchangeToken() {
        exchangeInfo.token
    }

    private HTTPBuilder configureExchangeAccess(HTTPBuilder builder) {

        def exchangeToken = exchangeInfo.token

        //x-token header
        builder?.headers.put('x-token', "Bearer $exchangeToken")

        builder.handler.'403' = { resp ->
            def jsonResp = fromJsonResponse(resp)
            throw new RuntimeException(jsonResp.message)
        }

        return  builder
    }

}
