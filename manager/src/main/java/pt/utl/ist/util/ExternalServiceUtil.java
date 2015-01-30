package pt.utl.ist.util;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.List;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 27-09-2012
 * Time: 18:43
 */
public class ExternalServiceUtil {

    public static void replaceAllExternalServices(Document document, String oaiServerUrl) {
        List<Node> aggregators = document.getRootElement().selectNodes("//repox-data/aggregator");
        for(Node aggregatorNode : aggregators){
            List<Node> providers = aggregatorNode.selectNodes("provider");
            for(Node providerNode : providers){
                List<Node> sources = providerNode.selectNodes("source");
                for(Node sourceNode : sources){
                    List<Node> restServicesList = sourceNode.selectNodes("restServices/restService");
                    for(Node restServiceNode : restServicesList){
                        List<Node> parametersList = restServiceNode.selectNodes("parameters/parameter");
                        for(Node parameterNode : parametersList){
                            String semantics = parameterNode.valueOf("@semantics");
                            if(semantics.equals("SERVER_OAI_URL")){
                                Element parameterEl = (Element)parameterNode;
                                parameterEl.addAttribute("value",oaiServerUrl);
                            }
                        }
                    }
                }
            }
        }
    }
}
