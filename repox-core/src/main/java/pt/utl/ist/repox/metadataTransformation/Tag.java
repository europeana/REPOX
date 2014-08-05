package pt.utl.ist.repox.metadataTransformation;

/**
 */
public class Tag implements Comparable<Tag> {
    protected String name;
    protected String description;
    protected String xpath;

    @SuppressWarnings("javadoc")
    public String getName() {
        return name;
    }

    @SuppressWarnings("javadoc")
    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("javadoc")
    public String getDescription() {
        return description;
    }

    @SuppressWarnings("javadoc")
    public void setDescription(String description) {
        this.description = description;
    }

    @SuppressWarnings("javadoc")
    public String getXpath() {
        return xpath;
    }

    @SuppressWarnings("javadoc")
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
