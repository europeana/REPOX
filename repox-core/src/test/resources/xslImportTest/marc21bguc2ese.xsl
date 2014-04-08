<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns="http://krait.kb.nl/coop/tel/handbook/telterms.html"
                xmlns:rpx="info:lc/xmlns/marcxchange-v1"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:xalan="http://xml.apache.org/xalan"
                exclude-result-prefixes="xalan rpx dc xsi"
                version="1.0">

	<xsl:import href="new.xsl" />
	<xsl:import href="new2.xsl" />

    <xsl:output method="xml" indent="yes"/>
    <xsl:template match="/">

        <europeana:record xmlns:europeana="http://www.europeana.eu/schemas/ese/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/">
            <xsl:if test="/rpx:record/rpx:datafield[@tag='015']/rpx:subfield[@code='a']">
                <dc:identifier>
                    <xsl:if test="/rpx:record/rpx:datafield[@tag='015']/rpx:subfield[@code='a']">URN:NBN:</xsl:if>
                    <xsl:if test="/rpx:record/rpx:datafield[@tag='015']/rpx:subfield[@code='a']">
                        <xsl:text xml:space="preserve"> </xsl:text>
                        <xsl:value-of select="/rpx:record/rpx:datafield[@tag='015']/rpx:subfield[@code='a']"/>
                    </xsl:if>
                </dc:identifier>
            </xsl:if>
            


            <europeana:hasObject>888<xsl:call-template name="getCallNumber"/></europeana:hasObject>
            <europeana:country>PT</europeana:country>
            


        </europeana:record>
    </xsl:template>



   

</xsl:stylesheet>
