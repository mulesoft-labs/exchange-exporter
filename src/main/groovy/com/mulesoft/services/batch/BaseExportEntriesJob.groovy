package com.mulesoft.services.batch

import com.mulesoft.services.backend.Exchange
import groovy.json.JsonBuilder

/**
 * Created by juancavallotti on 1/17/17.
 */
abstract class BaseExportEntriesJob extends BaseBatchJob {

    private File outputFile
    protected Exchange exchange

    private ObjectOutputStream os

    protected BaseExportEntriesJob(File outputFile, Exchange exchange) {
        this.outputFile = outputFile
        this.exchange = exchange
    }

    protected void beginWrite() throws IOException {
        logger.debug("Creating object output stream into: $outputFile.absolutePath")
        os = outputFile.newObjectOutputStream()
    }

    protected void closeFile() throws IOException {
        logger.debug("Closing handle to file: $outputFile.absolutePath")
        os.flush()
        os.close()
    }

    protected void writeEntry(Object entry) throws IOException {
        logger.trace("Write $entry into file")
        os.writeObject(PersistentExchangeEntry.fromEntry(entry))
    }

    @Override
    void doExecute() throws Exception {
        try {
            beginWrite()

            logger.debug("Call write entries...")
            doWriteEntries()
            logger.debug("Write entires called successfully")
        } finally {
            closeFile()
        }
    }

    abstract void doWriteEntries() throws Exception
}
