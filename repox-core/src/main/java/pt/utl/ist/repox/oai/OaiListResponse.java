package pt.utl.ist.repox.oai;

import java.util.ArrayList;
import java.util.List;

public class OaiListResponse {
	private List<OaiItem> oaiItems = new ArrayList<OaiItem>();
	private int lastRequestedIdentifier;

	public List<OaiItem> getOaiItems() {
		return oaiItems;
	}

	public void setOaiItems(List<OaiItem> oaiItems) {
		this.oaiItems = oaiItems;
	}

	public int getLastRequestedIdentifier() {
		return lastRequestedIdentifier;
	}

	public void setLastRequestedIdentifier(int lastRequestedIdentifier) {
		this.lastRequestedIdentifier = lastRequestedIdentifier;
	}

	public OaiListResponse() {
		super();
	}

	public OaiListResponse(List<OaiItem> oaiItems, int lastRequestedIdentifier) {
		this();
		this.oaiItems = oaiItems;
		this.lastRequestedIdentifier = lastRequestedIdentifier;
	}

	public class OaiItem {
		private String identifier;
		private String datestamp;
		private String setSpec;
		private boolean isDeleted;
		private byte[] metadata;

		public String getIdentifier() {
			return identifier;
		}

		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}

		public String getDatestamp() {
			return datestamp;
		}

		public void setDatestamp(String datestamp) {
			this.datestamp = datestamp;
		}

		public String getSetSpec() {
			return setSpec;
		}

		public void setSetSpec(String setSpec) {
			this.setSpec = setSpec;
		}

		public boolean isDeleted() {
			return isDeleted;
		}

		public void setDeleted(boolean isDeleted) {
			this.isDeleted = isDeleted;
		}

		public byte[] getMetadata() {
			return metadata;
		}

		public void setMetadata(byte[] metadata) {
			this.metadata = metadata;
		}

		public OaiItem() {
			super();
		}

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
