<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:p3="http://www.europeana.eu/schemas/ese/" xmlns:p2="http://www.openarchives.org/OAI/2.0/oai_dc/" exclude-result-prefixes="p2 fn p3 xsl" version="1.0">
	<xsl:output indent="yes" encoding="UTF-8" method="xml" version="1.0"/>
	<xsl:template match="/"><xsl:for-each select="p2:dc"><xsl:variable name="tmp1" select="."/><xsl:element name="p3:provider"><xsl:value-of select="$tmp1/text()"/></xsl:element></xsl:for-each></xsl:template>
</xsl:stylesheet>