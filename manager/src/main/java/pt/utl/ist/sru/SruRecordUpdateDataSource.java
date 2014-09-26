/* DataSourceSruRecordUpdate.java - created on 24 de Abr de 2013, Copyright (c) 2011 The European Library, all rights reserved */
package pt.utl.ist.sru;

import org.dom4j.Element;

import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.dataSource.RecordIdPolicy;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.task.Task.Status;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author Nuno Freire (nfreire@gmail.com)
 * @since 24 de Abr de 2013
 */
public class SruRecordUpdateDataSource extends DataSource {
    /**
     * Creates a new instance of this class.
     * @param dataProvider
     * @param id
     * @param description
     * @param schema
     * @param namespace
     * @param metadataFormat
     * @param recordIdPolicy
     * @param metadataTransformations
     */
    public SruRecordUpdateDataSource(DataProvider dataProvider, String id, String description, String schema, String namespace, String metadataFormat, RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations) {
        super(dataProvider, id, description, schema, namespace, metadataFormat, recordIdPolicy, metadataTransformations);
    }

    @Override
    public Status ingestRecords(File logFile, boolean fullIngest) throws Exception {
        return Status.OK;
    }

    @Override
    public boolean isWorking() {
        return true;
    }

    @Override
    public Element addSpecificInfo(Element sourceElement) {
        sourceElement.addAttribute("type", "DataSourceSruRecordUpdate");
        return sourceElement;
    }

    @Override
    public int getTotalRecords2Harvest() {
        return 0;
    }

    @Override
    public String getNumberOfRecords2HarvestStr() {
        return "0";
    }

    @Override
    public int getRecordsPerResponse() {
        return 1;
    }

    @Override
    public List<Long> getStatisticsHarvester() {
        return null;
    }

}
