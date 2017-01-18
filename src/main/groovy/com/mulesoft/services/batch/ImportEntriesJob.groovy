package com.mulesoft.services.batch

import com.mulesoft.services.backend.Exchange

/**
 * Created by juancavallotti on 1/17/17.
 */
class ImportEntriesJob extends BaseBatchJob {

    private File importFile
    private Exchange exchange
    private def orgEntry


    private static final IMPORT_URL_PREFIX = 'imported-'

    private ObjectInput is

    ImportEntriesJob(File importFile, Exchange exchange, orgEntry) {
        this.importFile = importFile
        this.exchange = exchange
        this.orgEntry = orgEntry
    }

    @Override
    void doExecute() throws Exception {
        try {
            beginRead()

            PersistentExchangeEntry entry = null

            while ((entry = readEntry()) != null) {
                //todo publish
                progressListener.updateProgressNotification(0)
                logger.debug("Got entry from input file...")
                logger.trace("Read entry: ${entry.contents}")
                exchange.createEntry(entry.contents, orgEntry, true)
                progressListener.updateProgressNotification(100)
            }


        } finally {
            closeFile()
        }

    }

    protected void beginRead() throws IOException {
        logger.debug("Creating object input stream from: $importFile.absolutePath")
        is = importFile.newObjectInputStream()
    }

    protected void closeFile() throws IOException {
        logger.debug("Closing handle to file: $importFile.absolutePath")
        is.close()
    }

    protected PersistentExchangeEntry readEntry() {

        try {
            PersistentExchangeEntry ret = is.readObject()
            return curateEntry(ret)
        } catch (EOFException ex) {
            return null
        }

    }


    public PersistentExchangeEntry curateEntry(PersistentExchangeEntry entry) {

        def entrymap = exchange.fromJson(entry.contents)

        //owner needs to be the same as the current logged in user
        entrymap.owner = exchange.connection.username

        entrymap.name_url = IMPORT_URL_PREFIX + orgEntry.value.id + "-" + entrymap.name_url

        if (!entrymap.metadata) {
            entrymap.metadata = []
        }

        entry.contents = exchange.toJson(entrymap)
        return entry
    }


}
