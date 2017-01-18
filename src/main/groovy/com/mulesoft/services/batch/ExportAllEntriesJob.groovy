package com.mulesoft.services.batch

import com.mulesoft.services.backend.Exchange

/**
 * Created by juancavallotti on 1/17/17.
 */
class ExportAllEntriesJob extends BaseExportEntriesJob {

    private def orgEntry

    ExportAllEntriesJob(File outputFile, Exchange exchange, def orgEntry) {
        super(outputFile, exchange)
        this.orgEntry = orgEntry
    }

    @Override
    void doWriteEntries() throws Exception {

        //get all entries, for some reason exchange is not paging.
        def entries = exchange.allEntries(orgEntry, 0, 20)

        logger.debug("Will write ${entries.size()} entries...")

        entries.each {
            writeEntry(it)
        }

    }
}
