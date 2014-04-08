package harvesterUI.shared.filters;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 24-02-2012
 * Time: 7:51
 */
public enum FilterType implements IsSerializable {
    COUNTRY,
    METADATA_FORMAT,
    DP_TYPE,
    TRANSFORMATION,
    INGEST_TYPE,
    LAST_INGEST,
    RECORDS,
    TAG,

    DATA_PROVIDER_USER
}
