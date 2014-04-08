package harvesterUI.shared.mdr;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 24-02-2012
 * Time: 7:51
 */
public enum MarcFormat implements IsSerializable {
    MARC21,
    UNIMARC,
    Ibermarc,
    danMARC2,
    None
}
