package pt.utl.ist.repox.dataProvider.dataSource;

import pt.utl.ist.repox.marc.Iso2709FileExtract;
import pt.utl.ist.repox.marc.MarcXchangeFileExtract;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;

import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: Gilberto Pedrosa Date: 20-07-2011 Time: 11:11
 * To change this template use File | Settings | File Templates.
 */
public class DataSourceUtil {
    /**
     * Create IdPolicy
     * 
     * @param recordIdPolicyClass
     * @param identifierXpath
     * @param namespaces
     * @return RecordIdPolicy
     */
    public static RecordIdPolicy createIdPolicy(String recordIdPolicyClass, String identifierXpath, Map<String, String> namespaces) {
        RecordIdPolicy recordIdPolicy = null;
        if (recordIdPolicyClass.equals(IdGenerated.class.getSimpleName())) {
            recordIdPolicy = new IdGenerated();
        } else if (recordIdPolicyClass.equals(IdProvided.class.getSimpleName())) {
            recordIdPolicy = new IdProvided();
        } else if (recordIdPolicyClass.equals(IdExtracted.class.getSimpleName())) {
            recordIdPolicy = new IdExtracted(identifierXpath, namespaces);
        }
        return recordIdPolicy;
    }

    /**
     * @param metadataFormat
     * @param isoFormat
     * @return FileExtractStrategy
     */
    public static FileExtractStrategy extractStrategyString(String metadataFormat, String isoFormat) {
        if (metadataFormat.equals(MetadataFormat.ISO2709.toString())) {
            return new Iso2709FileExtract(isoFormat);
        } else if (metadataFormat.equals(MetadataFormat.MarcXchange.toString())) {
            return new MarcXchangeFileExtract();
        } else {
            return new SimpleFileExtract();
        }
    }
}
