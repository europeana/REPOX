package pt.utl.ist.externalServices;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX. User: Edmundo Date: 12-12-2011 Time: 14:24
 */
public class ServiceParameter {
    private static final Logger log = Logger.getLogger(ServiceParameter.class);

    private String              name;
    private String              type;
    private String              value;
    private String              example;
    private String              semantics;
    private boolean             required;
    private List<String>        comboValues;

    /**
     * Creates a new instance of this class.
     * 
     * @param name
     * @param type
     * @param required
     * @param example
     * @param semantics
     */
    public ServiceParameter(String name, String type, boolean required, String example, String semantics) {
        super();
        this.name = name;
        this.type = type;
        this.required = required;
        this.example = example;
        this.semantics = semantics;
    }

    @SuppressWarnings("javadoc")
    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("javadoc")
    public String getName() {
        return name;
    }

    @SuppressWarnings("javadoc")
    public void setType(String type) {
        this.type = type;
    }

    @SuppressWarnings("javadoc")
    public String getType() {
        return type;
    }

    @SuppressWarnings("javadoc")
    public void setValue(String value) {
        this.value = value;
    }

    @SuppressWarnings("javadoc")
    public String getValue() {
        return value;
    }

    @SuppressWarnings("javadoc")
    public void setRequired(boolean required) {
        this.required = required;
    }

    @SuppressWarnings("javadoc")
    public boolean getRequired() {
        return required;
    }

    @SuppressWarnings("javadoc")
    public String getExample() {
        return example;
    }

    @SuppressWarnings("javadoc")
    public void setExample(String example) {
        this.example = example;
    }

    @SuppressWarnings("javadoc")
    public String getSemantics() {
        return semantics;
    }

    @SuppressWarnings("javadoc")
    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    @SuppressWarnings("javadoc")
    public List<String> getComboValues() {
        if (comboValues == null) comboValues = new ArrayList<String>();
        return comboValues;
    }
}
