package harvesterUI.server.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SetCharacterEncodingFilter implements Filter {
    private String encoding = null;
    private FilterConfig filterConfig = null;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
    	HttpServletRequest hrequest = (HttpServletRequest) request;

    	if(hrequest.getSession().getAttribute("lang") == null) {
    		hrequest.getSession().setAttribute("lang", hrequest.getLocale().getLanguage());
    		response.setLocale(hrequest.getLocale());
    		
/*    		
    		Enumeration locales = request.getLocales();
    		
    		while(locales.hasMoreElements()) {
    			Locale currentLocale = (Locale) locales.nextElement();
    			System.out.println("* AVAILABLE LOCALE: " + currentLocale.getLanguage());
    		}

    		System.out.println("* SELECTED LOCALE: " + request.getLocale().getLanguage());
 */
    		
    	}
    	String encoding = selectEncoding(request);

    	if (encoding != null) {
    		request.setCharacterEncoding(encoding);
    	}
    	chain.doFilter(request, response);
    }
    
    public void init(FilterConfig filterConfig) throws ServletException {
    	this.filterConfig = filterConfig;
    	this.encoding = filterConfig.getInitParameter("encoding");
    }
    
    protected String selectEncoding(ServletRequest request) {
    	return (this.encoding);
    }

    public void destroy() {
    }

}