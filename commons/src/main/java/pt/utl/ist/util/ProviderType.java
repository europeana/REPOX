/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package pt.utl.ist.util;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Data provider types.
 * 
 * @author Nicola Aloia   <nicola.aloia@isti.cnr.it>
 * @since Mar 22, 2010
 */
@XmlEnum(String.class)
public enum ProviderType {
    MUSEUM,
    ARCHIVE,
    LIBRARY,
    AUDIO_VISUAL_ARCHIVE,
    RESEARCH_EDUCATIONAL,
    CROSS_SECTOR,
    PUBLISHER,
    PRIVATE,
    AGGREGATOR,
    UNKNOWN;

    public static ProviderType get(String string) {
        for (ProviderType t : values()) {
            if (t.toString().equalsIgnoreCase(string)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Did not recognize ProviderType: [" + string + "]");
    }
}

