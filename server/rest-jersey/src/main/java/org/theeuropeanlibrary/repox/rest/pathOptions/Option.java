/* Option.java - created on Oct 13, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.pathOptions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Describes the specific option.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 13, 2014
 */
@XmlRootElement
@XmlType(propOrder={"description", "syntax"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Option {
    @XmlElement(name="description")
    private String description;
    @XmlElement(name="syntax")
    private String syntax;

    /**
     * No argument constructor needed for JAXB.
     */
    public Option() {
    }

    /**
     * Creates a new instance of this class.
     * @param description
     * @param syntax
     */
    public Option(String description, String syntax) {
        super();
        this.description = description;
        this.syntax = syntax;
    }
    
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }
}