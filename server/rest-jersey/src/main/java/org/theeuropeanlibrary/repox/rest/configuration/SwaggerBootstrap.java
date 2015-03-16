/* Bootstrap.java - created on Feb 26, 2015, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.configuration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.wordnik.swagger.jaxrs.config.BeanConfig;

/**
 * 
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Feb 26, 2015
 */

public class SwaggerBootstrap extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setTitle("Swagger UI For Repox");
        beanConfig.setDescription("This is a sample customised Swagger UI used for Repox.");
        beanConfig.setVersion("1.0.0");
        beanConfig.setBasePath("http://localhost:8080/repox/rest");
        beanConfig.setResourcePackage("org.theeuropeanlibrary.repox.rest.servlets");
        beanConfig.setScan(true);
    }
}
