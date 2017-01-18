package com.mulesoft.services.backend

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * Created by juancavallotti on 1/17/17.
 */
trait ApiAccessor {
    def fromJson(InputStream is) {
        JsonSlurper slurper = new JsonSlurper()
        return slurper.parse(is)
    }

    def fromJson(String text) {
        JsonSlurper slurper = new JsonSlurper()
        return slurper.parseText(text)
    }

    def fromJsonResponse(def resp) {
        JsonSlurper slurper = new JsonSlurper()
        def jsonResp = slurper.parse(resp.entity.content)
    }

    String toJson(def obj) {
        JsonOutput.toJson(obj)
    }
}