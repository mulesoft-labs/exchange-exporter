package com.mulesoft.services.batch

import com.mulesoft.services.backend.Exchange

/**
 * Created by juancavallotti on 1/18/17.
 */
class ExportFixedEntriesJob extends BaseExportEntriesJob {

    protected List<Object> entries

    protected ExportFixedEntriesJob(File outputFile, Exchange exchange, List<Object> entries) {
        super(outputFile, exchange)
        this.entries = entries
    }

    @Override
    void doWriteEntries() throws Exception {

        logger.debug("Will write ${entries.size()} entries...")

        entries.each {
            writeEntry(it)
        }

    }
}
