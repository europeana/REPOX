/* Bootstrap.java - created on Feb 26, 2015, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.configuration;

import javax.servlet.http.HttpServlet;

import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.model.ApiInfo;

/**
 * 
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Feb 26, 2015
 */

public class SwaggerBootstrap extends HttpServlet {
    static {
        // do any additional initialization here, such as set your base path programmatically as such:
        // ConfigFactory.config().setBasePath("http://www.foo.com/");

        ApiInfo info = new ApiInfo(
                "Swagger UI For Repox", /* title */
                "This is a sample customised Swagger UI used for Repox.",
                "", /* Terms Of Sservice URL */
                "", /* Contact */
                "", /* license */
                "" /* license URL */
        );

        //    List<AuthorizationScope> scopes = new ArrayList<AuthorizationScope>();
        //    scopes.add(new AuthorizationScope("email", "Access to your email address"));
        //    scopes.add(new AuthorizationScope("pets", "Access to your pets"));
        //
        //    List<GrantType> grantTypes = new ArrayList<GrantType>();

        //    ImplicitGrant implicitGrant = new ImplicitGrant(
        //      new LoginEndpoint("http://petstore.swagger.wordnik.com/oauth/dialog"), 
        //      "access_code");
        //
        //    grantTypes.add(implicitGrant);

        //    AuthorizationType oauth = new OAuthBuilder().scopes(scopes).grantTypes(grantTypes).build();
        //
        //    ConfigFactory.config().addAuthorization(oauth);
        ConfigFactory.config().setApiInfo(info);
    }
}
