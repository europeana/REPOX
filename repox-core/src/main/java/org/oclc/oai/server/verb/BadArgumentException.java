/**
 * Copyright 2006 OCLC Online Computer Library Center Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or
 * agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oclc.oai.server.verb;

import org.oclc.oai.util.OAIUtil;

/**
 * An OAI badArgumentException
 * 
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class BadArgumentException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of this class.
     */
    public BadArgumentException() {
        super("<" + OAIUtil.getTag("error") + " code=\"badArgument\">The request includes illegal arguments, is missing required arguments, includes a repeated argument, or values for arguments have an illegal syntax." + "</" + OAIUtil.getTag("error") + ">");
    }
}
