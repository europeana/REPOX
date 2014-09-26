package pt.utl.ist.repox.oai;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class OaiListResponse {
    private List<OaiItem> oaiItems = new ArrayList<OaiItem>();
    private int           lastRequestedIdentifier;

    @SuppressWarnings("javadoc")
    public List<OaiItem> getOaiItems() {
        return oaiItems;
    }

    @SuppressWarnings("javadoc")
    public void setOaiItems(List<OaiItem> oaiItems) {
        this.oaiItems = oaiItems;
    }

    @SuppressWarnings("javadoc")
    public int getLastRequestedIdentifier() {
        return lastRequestedIdentifier;
    }

    @SuppressWarnings("javadoc")
    public void setLastRequestedIdentifier(int lastRequestedIdentifier) {
        this.lastRequestedIdentifier = lastRequestedIdentifier;
    }

    /**
     * Creates a new instance of this class.
     */
    public OaiListResponse() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param oaiItems
     * @param lastRequestedIdentifier
     */
    public OaiListResponse(List<OaiItem> oaiItems, int lastRequestedIdentifier) {
        this();
        this.oaiItems = oaiItems;
        this.lastRequestedIdentifier = lastRequestedIdentifier;
    }

    /**
	 */
    public class OaiItem {
        private String  identifier;
        private String  datestamp;
        private String  setSpec;
        private boolean isDeleted;
        private byte[]  metadata;

        @SuppressWarnings("javadoc")
        public String getIdentifier() {
            return identifier;
        }

        @SuppressWarnings("javadoc")
        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        @SuppressWarnings("javadoc")
        public String getDatestamp() {
            return datestamp;
        }

        @SuppressWarnings("javadoc")
        public void setDatestamp(String datestamp) {
            this.datestamp = datestamp;
        }

        @SuppressWarnings("javadoc")
        public String getSetSpec() {
            return setSpec;
        }

        @SuppressWarnings("javadoc")
        public void setSetSpec(String setSpec) {
            this.setSpec = setSpec;
        }

        @SuppressWarnings("javadoc")
        public boolean isDeleted() {
            return isDeleted;
        }

        @SuppressWarnings("javadoc")
        public void setDeleted(boolean isDeleted) {
            this.isDeleted = isDeleted;
        }

        @SuppressWarnings("javadoc")
        public byte[] getMetadata() {
            return metadata;
        }

        @SuppressWarnings("javadoc")
        public void setMetadata(byte[] metadata) {
            this.metadata = metadata;
        }

        /**
         * Creates a new instance of this class.
         */
        public OaiItem() {
            super();
        }

        /**
         * Creates a new instance of this class.
         * 
         * @param identifier
         * @param datestamp
         * @param setSpec
         * @param isDeleted
         * @param metadata
         */
        public OaiItem(String identifier, String datestamp, String setSpec, boolean isDeleted, byte[] metadata) {
            this();
            this.identifier = identifier;
            this.datestamp = datestamp;
            this.setSpec = setSpec;
            this.isDeleted = isDeleted;
            this.metadata = metadata;
        }
    }
}
