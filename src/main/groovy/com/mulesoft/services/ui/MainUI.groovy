package com.mulesoft.services.ui

import com.mulesoft.services.backend.AnypointConnection
import com.mulesoft.services.backend.Exchange
import com.mulesoft.services.batch.Executor
import com.mulesoft.services.batch.ExportAllEntriesJob
import com.mulesoft.services.batch.ExportFixedEntriesJob
import com.mulesoft.services.batch.ImportEntriesJob
import com.mulesoft.services.batch.ProgressListener
import groovy.swing.SwingBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JComboBox
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JProgressBar
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.BorderLayout
import java.awt.event.ItemEvent

/**
 * Created by juancavallotti on 1/16/17.
 */
class MainUI implements ConnectionListener, ProgressListener {

    private static final Logger logger = LoggerFactory.getLogger(MainUI)

    private AnypointConnection apConnection
    private Exchange exchange

    //executor service
    private Executor executorService = new Executor(this)


    //components
    private JProgressBar pb
    private JLabel status
    private JComboBox orgs
    private JTable itemsTable
    private JTextField filterField


    //flags
    private boolean shouldUpdateTable = false

    public void build() {

        def jframe

        new SwingBuilder().edt {

            jframe = frame(title: 'Anypoint Exchange Utilities', extendedState: JFrame.MAXIMIZED_BOTH, defaultCloseOperation: JFrame.EXIT_ON_CLOSE, show: true) {
                borderLayout()

                panel(constraints: BorderLayout.SOUTH) {
                    status = label()
                    pb = progressBar(stringPainted: true)
                }

                panel(constraints: BorderLayout.NORTH) {
                    gridLayout(columns: 6, rows: 2)
                    label "Organization:", horizontalAlignment: JLabel.RIGHT
                    orgs = comboBox()
                    orgs.addItemListener({
                        if (it.stateChange == ItemEvent.SELECTED) {
                            logger.debug("Org selected item $it.item")
                            bgUpdateData(it.item)
                        }
                    })

                    button text: "Export All", actionPerformed: {

                        File outputFile = saveFileDialog()

                        if (!outputFile) {
                            return
                        }

                        ExportAllEntriesJob job = new ExportAllEntriesJob(outputFile, exchange, orgs.selectedItem)
                        job.jobName = 'Export all Entries'
                        executorService.schedule(job)
                    }

                    button text: "Export Selected", actionPerformed: {

                        List<Object> selectedEntries = getSelectedEntries()

                        if (selectedEntries.size() == 0) {
                            return
                        }

                        File outputFile = saveFileDialog()

                        if (!outputFile) {
                            return
                        }

                        ExportFixedEntriesJob job = new ExportFixedEntriesJob(outputFile, exchange, selectedEntries)
                        job.jobName = 'Export selected entries'
                        executorService.schedule(job)
                    }

                    button text: "Import...", actionPerformed: {

                        File inputFile = openFileDialog()

                        if (!inputFile) {
                            return
                        }

                        ImportEntriesJob job = new ImportEntriesJob(inputFile, exchange, orgs.selectedItem)
                        job.jobName = 'Import Entries'
                        shouldUpdateTable = true
                        executorService.schedule(job)

                    }

                    button text: 'Logout', actionPerformed: {
                        new ConnectionProperties(this, jframe).build()
                    }

                    label text: 'Filter:', horizontalAlignment: JLabel.RIGHT
                    filterField = textField()
                    button text: 'Search', actionPerformed: {
                        bgUpdateData(orgs.selectedItem)
                    }
                    label()
                    label()
                    label()

                }

                scrollPane(constraints: BorderLayout.CENTER) {
                    itemsTable = table(id: 'itemsTable') {
                        tableModel {
                            propertyColumn(header: 'Name', propertyName: 'name')
                            propertyColumn(header: 'Summary', propertyName: 'summary')
                            propertyColumn(header: 'Owner', propertyName: 'owner')
                            propertyColumn(header: 'Type', propertyName: 'type_id')
                        }
                    }
                }

            }

        }

        new ConnectionProperties(this, jframe).build()

    }

    @Override
    void onConnectionPropertiesSet(AnypointConnection connection) {


        apConnection = connection

        SwingUtilities.invokeLater({
            startProgress()

            logger.debug("Logging into the platform...")

            connection.login()

            logger.debug("Connecting to exchange...")

            exchange = new Exchange(connection)

            updateUI()

            endProgress()
        })
    }

    void updateUI() {

        updateExchangeOrgs()
    }

    void startProgress() {
        pb.indeterminate = true

    }

    void endProgress() {
        pb.indeterminate = false
    }


    void updateExchangeOrgs() {
        orgs.removeAllItems()

        orgs.addItem(new DropDownItem(title: 'MuleSoft Public', value: null))

        exchange.availableOrganizations().each {

            orgs.addItem(new DropDownItem(title: it.name, value: it))

        }

    }

    void bgUpdateData(def objItem) {
        SwingUtilities.invokeLater {
            updateData(objItem)
        }
    }

    void updateData(def objItem){


        def entries = exchange.allEntries(objItem, 0, 20, filterField.text)

        itemsTable.model.rowsModel == null

        itemsTable.model.with {

            rowsModel.value.clear()
            rowsModel.value.addAll(entries)

            fireTableDataChanged()
        }

    }

    File saveFileDialog() {

        //use a file chooser to get the output file name
        JFileChooser jfc = new JFileChooser(dialogTitle: 'Select output file',
                fileSelectionMode: JFileChooser.FILES_ONLY, fileFilter: new FileNameExtensionFilter("Export files (*.exchange)", "exchange"))

        if (jfc.showSaveDialog(null) == JFileChooser.CANCEL_OPTION) {
            return null
        }

        return jfc.selectedFile
    }

    File openFileDialog() {
        //use a file chooser to get the output file name
        JFileChooser jfc = new JFileChooser(dialogTitle: 'Select Input file',
                fileSelectionMode: JFileChooser.FILES_ONLY, fileFilter: new FileNameExtensionFilter("Export files (*.exchange)", "exchange"))

        if (jfc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
            return null
        }

        return jfc.selectedFile
    }

    List<Object> getSelectedEntries() {

        int[] selected = itemsTable.selectedRows

        logger.debug("Selected entries indexes are: $selected")

        List<Object> ret = []

        for(int i : selected) {
            ret.add(itemsTable.model.rowsModel.value.get(i))
        }

        return ret
    }

    @Override
    void jobStarted(String name) {
        SwingUtilities.invokeLater {
            status.text = "Running job: $name"
        }
    }

    @Override
    void updateProgressNotification(int value) {
        SwingUtilities.invokeLater {
            pb.setValue(value)
        }
    }

    @Override
    void jobCompleted(String name) {
        SwingUtilities.invokeLater {
            status.text = "Completed job: $name"
            if (shouldUpdateTable) {
                bgUpdateData(orgs.selectedItem)
                shouldUpdateTable = false
            }
        }
    }

    @Override
    void jobErrored(String name, String errorMessage) {

    }
}
