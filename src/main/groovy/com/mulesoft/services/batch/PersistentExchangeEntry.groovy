package com.mulesoft.services.batch

import groovy.json.JsonBuilder
import groovy.json.JsonOutput

/**
 * Created by juancavallotti on 1/17/17.
 */
class PersistentExchangeEntry implements Serializable {
    String contents

    public static PersistentExchangeEntry fromEntry(Object entry) {
        //Serialize the entry into json
        return new PersistentExchangeEntry(contents: new JsonBuilder(entry).toString())
    }

}
