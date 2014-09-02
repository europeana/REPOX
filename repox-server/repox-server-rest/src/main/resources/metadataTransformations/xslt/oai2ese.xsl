<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns="http://purl.org/dc/elements/1.1/"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
                version="2.0"
                exclude-result-prefixes="dcterms">

    <xsl:output method="xml" indent="yes"/>


    <xsl:template match="/">
        <europeana:record xmlns:europeana="http://www.europeana.eu/schemas/ese/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/">
           
            <xsl:for-each select="/oai_dc:dc//*">
                <xsl:element name="local-name()">
                    <xsl:copy-of select="." />
                </xsl:element>
            </xsl:for-each>

            <xsl:for-each select="/oai_dc:dc//*">
                <xsl:if test="local-name() = 'identifier' and starts-with(current(), 'http://')">
                    <europeana:type>IMAGE</europeana:type>
                    <europeana:isShownAt><xsl:value-of select="."/></europeana:isShownAt>
                </xsl:if>
            </xsl:for-each>

        </europeana:record>
    </xsl:template>
</xsl:stylesheet>
