package pt.utl.ist.repox.dataProvider.dataSource;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.recordPackage.RecordRepoxExternalId;
import pt.utl.ist.repox.recordPackage.RecordRepoxXpathId;
import pt.utl.ist.repox.util.CompareUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class IdExtracted implements RecordIdPolicy {
	private static final Logger log = Logger.getLogger(IdExtracted.class);

	private Map<String, String> namespaces;
	private String identifierXpath; //private String identifierXpath = "/record/identifier"; OAI-DC

	public Map<String, String> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}

	public String getIdentifierXpath() {
		return identifierXpath;
	}

	public void setIdentifierXpath(String identifierXpath) {
		this.identifierXpath = identifierXpath;
	}

	public XPath getIdXpath() {
		String namespaceAwareXPath = identifierXpath;
		
		String correctedIdentifierXpath = "//*[1]" + namespaceAwareXPath + " | " + namespaceAwareXPath;
		log.debug("correctedIdentifierXpath = " + correctedIdentifierXpath);
		XPath idXPath = DocumentHelper.createXPath(correctedIdentifierXpath);

		idXPath.setNamespaceURIs(namespaces);
		return idXPath;
	}

	public IdExtracted() {
		super();
		namespaces = new HashMap<String, String>();
	}

	public IdExtracted(String identifierXpath, Map<String, String> namespaces) {
		super();
		this.identifierXpath = identifierXpath;
		this.namespaces = namespaces;
	}

	public RecordRepox createRecordRepox(Element recordElement, String recordId, boolean forceId, boolean isDeleted) {
		if(forceId) {
			return new RecordRepoxExternalId(recordElement, recordId);
		}
		else {
			RecordRepoxXpathId recordRepoxXpathId = new RecordRepoxXpathId(recordElement, getIdXpath(), isDeleted);
			return recordRepoxXpathId;
		}
	}

	public boolean equalsNamespaces(Map<String, String> namespaces) {
		if(this.namespaces == null && namespaces == null) {
			return true;
		}
		else if(this.namespaces == null || namespaces == null
				|| this.namespaces.size() != namespaces.size()) {
			return false;
		}

		Set<Entry<String, String>> localNamespaceEntries = this.namespaces.entrySet();

		for (Entry<String, String> localNamespaceEntry : localNamespaceEntries) {
			if(!namespaces.containsKey(localNamespaceEntry.getKey())
					|| !CompareUtil.compareObjectsAndNull(localNamespaceEntry.getValue(), namespaces.get(localNamespaceEntry.getKey()))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if(!this.getClass().equals(obj.getClass())) {
			return false;
		}

		IdExtracted mSIdExtracted = (IdExtracted) obj;

		if(CompareUtil.compareObjectsAndNull(this.identifierXpath, mSIdExtracted.getIdentifierXpath())
				&& this.equalsNamespaces(mSIdExtracted.getNamespaces())) {
			return true;
		}
		else {
			return false;
		}
	}

}
