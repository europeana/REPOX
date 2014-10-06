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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean getRequired() {
        return required;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getSemantics() {
        return semantics;
    }

    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    public List<String> getComboValues() {
        if (comboValues == null) comboValues = new ArrayList<String>();
        return comboValues;
    }
}
