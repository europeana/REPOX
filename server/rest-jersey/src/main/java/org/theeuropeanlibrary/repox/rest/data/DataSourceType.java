/* DataSourceType.java - created on Oct 30, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.data;

import javax.xml.bind.annotation.XmlEnum;

/**
 * DataSource types.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 30, 2014
 */
@XmlEnum(String.class)
public enum DataSourceType {
    OAI,
    Z3950,
    DIR,
    SRU;
}
