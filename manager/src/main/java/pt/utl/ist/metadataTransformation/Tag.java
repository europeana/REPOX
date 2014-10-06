package pt.utl.ist.metadataTransformation;

/**
 */
public class Tag implements Comparable<Tag> {
    protected String name;
    protected String description;
    protected String xpath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    /**
     * Creates a new instance of this class.
     */
    public Tag() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param name
     * @param description
     * @param xpath
     */
    public Tag(String name, String description, String xpath) {
        this();
        this.description = description;
        this.name = name;
        this.xpath = xpath;
    }

    @Override
    public int compareTo(Tag otherTag) {
        return (otherTag != null ? this.getXpath().compareTo(otherTag.getXpath()) : 1);
    }

}
