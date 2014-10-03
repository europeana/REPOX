/*
 * MysqlFactory.java
 *
 * Created on 21 de Julho de 2002, 3:07
 */

package pt.utl.ist.marc;

/**
 * @deprecated
 * @author  Nuno Freire
 */
@Deprecated
public interface MarcObjectFactory {

    /**
     * @return get new Record
     */
    public MarcRecord newRecord();

    /**
     * @return get new Field
     */
    public MarcField newField();

    /**
     * @return get new SubField
     */
    public MarcSubfield newSubfield();
    
    /**
     * @param rec
     * @return boolean indicating if the record is from this factory
     */
    public boolean isFromThisFactory(MarcRecord rec);
}
