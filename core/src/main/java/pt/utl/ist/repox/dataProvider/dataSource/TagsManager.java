package pt.utl.ist.repox.dataProvider.dataSource;

import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import pt.utl.ist.repox.configuration.ConfigSingleton;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.dataProvider.MessageType;
import pt.utl.ist.repox.util.XmlUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created to REPOX. User: Edmundo Date: 12-12-2011 Time: 14:18
 */
public class TagsManager {
    private static final Logger log = Logger.getLogger(TagsManager.class);

    private List<DataSourceTag> tags;
    private File                configurationFile;

    /**
     * Creates a new instance of this class.
     * 
     * @param configurationFile
     * @throws IOException
     * @throws DocumentException
     */
    public TagsManager(File configurationFile) throws IOException, DocumentException {
        super();
        this.configurationFile = configurationFile;
        loadAllTags();
    }

    @SuppressWarnings("javadoc")
    public List<DataSourceTag> getTags() {
        if (tags == null) tags = new ArrayList<DataSourceTag>();
        return tags;
    }

    /**
     * @param tagName
     * @return MessageType
     */
    public MessageType removeTag(String tagName) {
        try {
            File tagsFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getXmlConfigPath() + File.separator + "dataSetTags.xml");

            if (!tagsFile.exists()) return MessageType.NOT_FOUND;

            SAXReader reader = new SAXReader();
            Document document = reader.read(tagsFile);

            List list = document.selectNodes("//tags/tag");

            for (Object node : list) {
                Node n = (Node)node;
                String currentTagName = n.valueOf("@name");
                if (currentTagName.equals(tagName)) n.detach();
            }

            XmlUtil.writePrettyPrint(tagsFile, document);

            loadAllTags();
            removeTagInAllDataSets(tagName);

            return MessageType.OK;
        } catch (DocumentException e) {
            e.printStackTrace();
            return MessageType.NOT_FOUND;
        } catch (IOException e) {
            e.printStackTrace();
            return MessageType.NOT_FOUND;
        }
    }

    /**
     * @param isUpdate
     * @param tagName
     * @param oldTagName
     * @return MessageType
     */
    public MessageType saveTag(boolean isUpdate, String tagName, String oldTagName) {
        try {
            File tagsFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getXmlConfigPath() + File.separator + "dataSetTags.xml");
            Document document;
            if (!tagsFile.exists()) {
                document = DocumentHelper.createDocument();
                document.addElement("tags");
            } else {
                SAXReader reader = new SAXReader();
                document = reader.read(tagsFile);
            }

            if (isUpdate && !tagName.equals(oldTagName) && tagAlreadyExists(tagName))
                return MessageType.ALREADY_EXISTS;
            else if (!isUpdate && tagAlreadyExists(tagName)) return MessageType.ALREADY_EXISTS;

            if (isUpdate) {
                List<Node> list = document.selectNodes("//tags/tag");
                for (Node tagNode : list) {
                    String currentTagName = tagNode.valueOf("@name");
                    if (currentTagName.equals(oldTagName)) {
                        tagNode.detach();
                    }
                }
            }

            DataSourceTag dataSourceTag = new DataSourceTag(tagName);

            Element tagNode = document.getRootElement().addElement("tag");
            tagNode.addAttribute("name", tagName);

            XmlUtil.writePrettyPrint(tagsFile, document);

            loadAllTags();

            if (isUpdate) updateTagInAllDataSets(dataSourceTag, oldTagName);

            return MessageType.OK;
        } catch (DocumentException e) {
            e.printStackTrace();
            return MessageType.ERROR_DATABASE;
        } catch (IOException e) {
            e.printStackTrace();
            return MessageType.ERROR_DATABASE;
        }
    }

    private boolean tagAlreadyExists(String tagName) {
        for (DataSourceTag dataSourceTag : getTags()) {
            if (tagName.equals(dataSourceTag.getName())) return true;
        }
        return false;
    }

    /**
     * 
     */
    public synchronized void loadAllTags() {
        if (!configurationFile.exists()) { return; }

        getTags().clear();

        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(configurationFile);
            List<Element> tagsElements = document.getRootElement().elements();

            for (Element currentElement : tagsElements) {
                String name = currentElement.attributeValue("name");
                getTags().add(new DataSourceTag(name));
            }
        } catch (DocumentException e) {
            log.error("Error loading the tags file (dataSetTags.xml).");
        }
    }

    /**
     * @param tagName
     */
    public void removeTagInAllDataSets(String tagName) {
        for (Object object : ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getAllDataList()) {
            if (object instanceof DataSourceContainer) {
                DataSource dataSource = ((DataSourceContainer)object).getDataSource();
                Iterator<DataSourceTag> iterator = dataSource.getTags().iterator();
                while (iterator.hasNext()) {
                    DataSourceTag currentTag = iterator.next();
                    if (tagName.equals(currentTag.getName())) {
                        iterator.remove();
                    }
                }
            }
        }

        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
        } catch (IOException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        } catch (DocumentException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * @param dataSourceTag
     * @param oldTagName
     */
    public void updateTagInAllDataSets(DataSourceTag dataSourceTag, String oldTagName) {
        for (Object object : ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getAllDataList()) {
            if (object instanceof DataSourceContainer) {
                DataSource dataSource = ((DataSourceContainer)object).getDataSource();
                for (DataSourceTag currentTag : dataSource.getTags()) {
                    if (oldTagName.equals(currentTag.getName())) {
                        currentTag.setName(dataSourceTag.getName());
                    }
                }
            }
        }

        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
        } catch (IOException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        } catch (DocumentException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        }
    }
}