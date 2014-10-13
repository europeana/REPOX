/* MyJaxbBean.java - created on Oct 13, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * TEMPORARY TEST BEAN
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 13, 2014
 */
@XmlRootElement
public class MyJaxbBean {
    @XmlElement
    public String name;
    @XmlTransient
    public int age;
 
    public MyJaxbBean() {} // JAXB needs this
 
    public MyJaxbBean(String name, int age) {
        this.name = name;
        this.age = age;
    }
}