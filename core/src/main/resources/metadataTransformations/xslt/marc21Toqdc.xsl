<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:qdc="http://repox.ist.utl.pt/qdc" xmlns:marc="info:lc/xmlns/marcxchange-v1"
	xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="marc" version="1.0">
	<!--
		Stylesheet based on:
		http://dublincore.org/documents/usageguide/qualifiers.shtml
		http://www.loc.gov/marc/marc2dc.html#qualifiedlist
	-->
	<xsl:output method="xml" indent="yes" />
	<xsl:template match="/">
		<qdc:qualifieddc
			xsi:schemaLocation="http://dublincore.org/schemas/xmls/qdc/2008/02/11/qualifieddc.xsd">
			<xsl:apply-templates />
		</qdc:qualifieddc>
	</xsl:template>
	<xsl:template match="marc:record">
		<xsl:variable name="leader" select="marc:leader" />
		<xsl:variable name="leader6" select="substring($leader,7,1)" />
		<xsl:variable name="leader7" select="substring($leader,8,1)" />
		<xsl:variable name="controlField008" select="marc:controlfield[@tag=008]" />
		<xsl:variable name="codes">
			abcdefghijklmnopqrstuvwxyz
		</xsl:variable>

		<!-- DCMES Element -->
		<xsl:for-each select="marc:datafield[@tag='245']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dc:title>
					<xsl:value-of select="." />
				</dc:title>
			</xsl:for-each>
		</xsl:for-each>
		<!-- -->

		<!-- Element Refinement(s) -->
		<!-- Title - Alternative -->
		<xsl:for-each
			select="marc:datafield[@tag='130']|marc:datafield[@tag='210']|marc:datafield[@tag='240']|marc:datafield[@tag='242']|marc:datafield[@tag='246']|marc:datafield[@tag='730']|marc:datafield[@tag='740']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:alternative>
					<xsl:value-of select="." />
				</dcterms:alternative>
			</xsl:for-each>
		</xsl:for-each>

		<!-- Description - Table Of Contents -->
		<xsl:for-each select="marc:datafield[@tag='505']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:tableOfContents>
					<xsl:value-of select="." />
				</dcterms:tableOfContents>
			</xsl:for-each>
		</xsl:for-each>

		<!-- Description - Abstract -->
		<xsl:for-each
			select="marc:datafield[@tag='520' and (@ind1='#' or @ind1='3')]">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:abstract>
					<xsl:value-of select="." />
				</dcterms:abstract>
			</xsl:for-each>
		</xsl:for-each>

		<!-- Date - Created -->
		<xsl:for-each
			select="marc:datafield[@tag='260']/marc:subfield[@code='c' or @code='g']|marc:datafield[@tag='533']/marc:subfield[@code='d']">
			<dcterms:created>
				<xsl:value-of select="." />
			</dcterms:created>
		</xsl:for-each>

		<!-- Date - Valid -->
		<xsl:for-each
			select="marc:datafield[@tag='046']/marc:subfield[@code='m' or @code='n']">
			<dcterms:valid>
				<xsl:value-of select="." />
			</dcterms:valid>
		</xsl:for-each>

		<!-- Date - Issued -->
		<xsl:if test="string-length(substring($controlField008,8,4)) = 4">
			<dcterms:issued>
				<xsl:value-of select="substring($controlField008,8,4)" />
			</dcterms:issued>
		</xsl:if>
		<xsl:for-each select="marc:datafield[@tag='260']/marc:subfield[@code='c']">
			<dcterms:issued>
				<xsl:value-of select="." />
			</dcterms:issued>
		</xsl:for-each>

		<!-- Date - Modified -->
		<xsl:for-each select="marc:datafield[@tag='046']/marc:subfield[@code='j']">
			<dcterms:modified>
				<xsl:value-of select="." />
			</dcterms:modified>
		</xsl:for-each>

		<!-- Date - Date Copyrighted -->
		<xsl:for-each
			select="marc:datafield[@tag='260']/marc:subfield[@code='c']|marc:datafield[@tag='542']/marc:subfield[@code='g']">
			<dcterms:dateCopyrighted>
				<xsl:value-of select="." />
			</dcterms:dateCopyrighted>
		</xsl:for-each>

		<!-- Format - Extent -->
		<xsl:for-each
			select="marc:datafield[@tag='300']/marc:subfield[@code='a']|marc:datafield[@tag='533']/marc:subfield[@code='e']">
			<dcterms:extent>
				<xsl:value-of select="." />
			</dcterms:extent>
		</xsl:for-each>

		<!-- Format - Medium -->
		<xsl:for-each select="marc:datafield[@tag='340']/marc:subfield[@code='a']">
			<dcterms:medium>
				<xsl:value-of select="." />
			</dcterms:medium>
		</xsl:for-each>

		<!-- Relation - Is Version Of -->
		<xsl:for-each select="marc:datafield[@tag='775']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:isVersionOf>
					<xsl:value-of select="." />
				</dcterms:isVersionOf>
			</xsl:for-each>
		</xsl:for-each>
		<xsl:for-each
			select="marc:datafield[@tag='786']/marc:subfield[@code='n' or @code='t']">
			<dcterms:isVersionOf>
				<xsl:value-of select="." />
			</dcterms:isVersionOf>
		</xsl:for-each>

		<!-- Relation - Has Version -->
		<xsl:for-each
			select="marc:datafield[@tag='775']/marc:subfield[@code='n' or @code='t']">
			<dcterms:hasVersion>
				<xsl:value-of select="." />
			</dcterms:hasVersion>
		</xsl:for-each>

		<!-- Relation - Is Replaced By -->
		<xsl:for-each
			select="marc:datafield[@tag='785']/marc:subfield[@code='n' or @code='t']">
			<dcterms:isReplacedBy>
				<xsl:value-of select="." />
			</dcterms:isReplacedBy>
		</xsl:for-each>

		<!-- Relation - Replaces -->
		<xsl:for-each
			select="marc:datafield[@tag='780']/marc:subfield[@code='n' or @code='t']">
			<dcterms:replaces>
				<xsl:value-of select="." />
			</dcterms:replaces>
		</xsl:for-each>

		<!-- Relation - Is Referenced By -->
		<xsl:for-each select="marc:datafield[@tag='510']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:isReferencedBy>
					<xsl:value-of select="." />
				</dcterms:isReferencedBy>
			</xsl:for-each>
		</xsl:for-each>

		<!-- Relation - Requires -->
		<xsl:for-each select="marc:datafield[@tag='538']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:requires>
					<xsl:value-of select="." />
				</dcterms:requires>
			</xsl:for-each>
		</xsl:for-each>

		<!-- Relation - Is Part Of -->
		<xsl:for-each
			select="marc:datafield[@tag='440']|marc:datafield[@tag='490']|marc:datafield[@tag='800']|marc:datafield[@tag='810']|marc:datafield[@tag='811']|marc:datafield[@tag='830']|marc:datafield[@tag='760']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:isPartOf>
					<xsl:value-of select="." />
				</dcterms:isPartOf>
			</xsl:for-each>
		</xsl:for-each>
		<xsl:for-each
			select="marc:datafield[@tag='773']/marc:subfield[@code='n' or @code='t']">
			<dcterms:isPartOf>
				<xsl:value-of select="." />
			</dcterms:isPartOf>
		</xsl:for-each>

		<!-- Relation - Has Part -->
		<xsl:for-each
			select="marc:datafield[@tag='774']/marc:subfield[@code='n' or @code='t']">
			<dcterms:hasPart>
				<xsl:value-of select="." />
			</dcterms:hasPart>
		</xsl:for-each>

		<!-- Relation - Is Format Of -->
		<xsl:for-each select="marc:datafield[@tag='530']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:isFormatOf>
					<xsl:value-of select="." />
				</dcterms:isFormatOf>
			</xsl:for-each>
		</xsl:for-each>
		<xsl:for-each
			select="marc:datafield[@tag='776']/marc:subfield[@code='n' or @code='t']">
			<dcterms:isFormatOf>
				<xsl:value-of select="." />
			</dcterms:isFormatOf>
		</xsl:for-each>

		<!-- Relation - Has Format -->
		<xsl:for-each select="marc:datafield[@tag='530']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:hasFormat>
					<xsl:value-of select="." />
				</dcterms:hasFormat>
			</xsl:for-each>
		</xsl:for-each>
		<xsl:for-each
			select="marc:datafield[@tag='776']/marc:subfield[@code='n' or @code='t']">
			<dcterms:hasFormat>
				<xsl:value-of select="." />
			</dcterms:hasFormat>
		</xsl:for-each>

		<!-- Coverage - Spatial -->
		<xsl:for-each
			select="marc:datafield[@tag='255']|marc:datafield[@tag='034']|marc:datafield[@tag='522']|marc:datafield[@tag='650']/marc:subfield[@code='z']|marc:datafield[@tag='651']|marc:datafield[@tag='662']|marc:datafield[@tag='751']|marc:datafield[@tag='752']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:spatial>
					<xsl:value-of select="." />
				</dcterms:spatial>
			</xsl:for-each>
		</xsl:for-each>
		<xsl:for-each select="marc:datafield[@tag='650']/marc:subfield[@code='z']">
			<dcterms:spatial>
				<xsl:value-of select="." />
			</dcterms:spatial>
		</xsl:for-each>

		<!-- Coverage - Temporal -->
		<xsl:for-each
			select="marc:datafield[@tag='033']/marc:subfield[@code='a']|marc:datafield[@tag='533']/marc:subfield[@code='b']">
			<dcterms:temporal>
				<xsl:value-of select="." />
			</dcterms:temporal>
		</xsl:for-each>

		<!-- Rights - Access Rights -->
		<xsl:for-each
			select="marc:datafield[@tag='506']/marc:subfield[@code='a' or @code='d']|marc:datafield[@tag='540']/marc:subfield[@code='a' or @code='d']">
			<dcterms:accessRights>
				<xsl:value-of select="." />
			</dcterms:accessRights>
		</xsl:for-each>

		<!-- Audience  -->
		<xsl:for-each select="marc:datafield[@tag='521']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:audience>
					<xsl:value-of select="." />
				</dcterms:audience>
			</xsl:for-each>
		</xsl:for-each>

		<!-- Provenance -->
		<xsl:for-each select="marc:datafield[@tag='561']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:provenance>
					<xsl:value-of select="." />
				</dcterms:provenance>
			</xsl:for-each>
		</xsl:for-each>

		<!-- Rights Holder -->
		<xsl:for-each select="marc:datafield[@tag='542']/marc:subfield[@code='d']">
			<dcterms:rightsHolder>
				<xsl:value-of select="." />
			</dcterms:rightsHolder>
		</xsl:for-each>

		<!-- Accrual Method -->
		<xsl:for-each select="marc:datafield[@tag='541']/marc:subfield[@code='c']">
			<dcterms:accrualMethod>
				<xsl:value-of select="." />
			</dcterms:accrualMethod>
		</xsl:for-each>

		<!-- Accrual Periodicity -->
		<xsl:for-each select="marc:datafield[@tag='310']/marc:subfield[@code='a']">
			<dcterms:accrualPeriodicity>
				<xsl:value-of select="." />
			</dcterms:accrualPeriodicity>
		</xsl:for-each>

		<!-- Element Encoding Scheme(s) -->
		<!-- Subject - LCSH -->
		<xsl:for-each
			select="marc:datafield[@tag='600' and @ind2='0']|marc:datafield[@tag='610' and @ind2='0']|marc:datafield[@tag='611' and @ind2='0']|marc:datafield[@tag='630' and @ind2='0']|marc:datafield[@tag='650' and @ind2='0']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dc:subject xsi:type="dcterms:LCSH">
					<xsl:value-of select="." />
				</dc:subject>
			</xsl:for-each>
		</xsl:for-each>

		<!-- Subject - MeSH -->
		<xsl:for-each
			select="marc:datafield[@tag='600' and @ind2='2']|marc:datafield[@tag='610' and @ind2='2']|marc:datafield[@tag='611' and @ind2='2']|marc:datafield[@tag='630' and @ind2='2']|marc:datafield[@tag='650' and @ind2='2']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dc:subject xsi:type="dcterms:MESH">
					<xsl:value-of select="." />
				</dc:subject>
			</xsl:for-each>
		</xsl:for-each>

		<!-- Subject - NLM -->
		<!--
			no such type in dcterms -->
		<!--
			<xsl:for-each select="marc:datafield[@tag='060']"> <xsl:for-each
			select="marc:subfield[contains($codes, @code)]"> <dc:subject
			xsi:type="dcterms:NLM"> <xsl:value-of select="." /> </dc:subject>
			</xsl:for-each> </xsl:for-each>
		-->

		<!-- Subject - DDC -->
		<xsl:for-each select="marc:datafield[@tag='082']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dc:subject xsi:type="dcterms:DDC">
					<xsl:value-of select="." />
				</dc:subject>
			</xsl:for-each>
		</xsl:for-each>

		<!-- Subject - LCC -->
		<xsl:for-each select="marc:datafield[@tag='050']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dc:subject xsi:type="dcterms:LCC">
					<xsl:value-of select="." />
				</dc:subject>
			</xsl:for-each>
		</xsl:for-each>

		<!-- Subject - UDC -->
		<xsl:for-each select="marc:datafield[@tag='080']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dc:subject xsi:type="dcterms:UDC">
					<xsl:value-of select="." />
				</dc:subject>
			</xsl:for-each>
		</xsl:for-each>

		<!-- Subject - TGN -->
		<!--
			no such combination in
			http://dublincore.org/documents/usageguide/qualifiers.shtml
		-->

		<!-- Type - DCMI Type Vocabulary -->
		<xsl:if test="$leader6='a' or $leader6='c' or $leader6='d' or $leader6='t'">
            <dc:type xsi:type="dcterms:DCMIType">Text</dc:type>
        </xsl:if>
        <xsl:if test="$leader6='e' or $leader6='f' or $leader6='g' or $leader6='k'">
            <dc:type xsi:type="dcterms:DCMIType">Image</dc:type>
        </xsl:if>
        <xsl:if test="$leader6='i' or $leader6='j'">
            <dc:type xsi:type="dcterms:DCMIType">Sound</dc:type>
        </xsl:if>
        <!--
            <xsl:if test="$leader6='m' or $leader6='o' or $leader6='p' or
            $leader6='r'"><dc:type xsi:type="dcterms:DCMIType">no type
            provided</dc:type> </xsl:if>
        -->
        <xsl:if test="$leader6='p' or $leader7='c' or $leader7='s'">
            <dc:type xsi:type="dcterms:DCMIType">Collection</dc:type>
        </xsl:if>
		<xsl:for-each select="marc:datafield[@tag='655']">
			<xsl:if test="marc:subfield[@code='2'] = 'dct'">
				<xsl:for-each select="marc:subfield[contains($codes, @code)]">
					<xsl:if
						test=".='Collection' or .='Dataset' or .='Event' or .='Image' or .='MovingImage' or .='StillImage' or .='InteractiveResource' or .='Service' or .='Software' or .='Sound' or .='Text' or .='PhysicalObject'">
						<dc:type xsi:type="dcterms:DCMIType">
							<xsl:value-of select="." />
						</dc:type>
					</xsl:if>
				</xsl:for-each>
			</xsl:if>
		</xsl:for-each>

		<!-- Format - IMT -->
		<xsl:for-each select="marc:datafield[@tag='856']/marc:subfield[@code='q']">
			<dc:format xsi:type="dcterms:IMT">
				<xsl:value-of select="." />
			</dc:format>
		</xsl:for-each>

		<!-- Identifier - URI -->
		<xsl:for-each select="marc:datafield[@tag='856']/marc:subfield[@code='u']">
			<dc:identifier xsi:type="dcterms:URI">
				<xsl:value-of select="." />
			</dc:identifier>
		</xsl:for-each>

		<!-- Source - URI -->
		<xsl:for-each select="marc:datafield[@tag='786']/marc:subfield[@code='o']">
			<dc:source xsi:type="dcterms:URI">
				<xsl:value-of select="." />
			</dc:source>
		</xsl:for-each>

		<!-- IS0369-2 - no such type in dcterms -->
		<!--
			<xsl:if test="string-length(substring($controlField008, 36, 4)) = 4">
			<dc:language xsi:type="dcterms:ISO639-2"> <xsl:value-of
			select="substring($controlField008, 36, 4)" /> </dc:language>
			</xsl:if>
		-->

		<!-- Language - ISO639-2 -->
		<xsl:for-each select="marc:datafield[@tag='041']">
			<xsl:if test="string-length(marc:subfield[@code='2']) = 0">
				<xsl:for-each select="marc:subfield[contains($codes, @code)]">
					<dc:language xsi:type="dcterms:ISO639-2">
						<xsl:value-of select="." />
					</dc:language>
				</xsl:for-each>
			</xsl:if>
		</xsl:for-each>

		<!-- Language - ISO639-3 -->
		<xsl:for-each select="marc:datafield[@tag='041']">
			<xsl:if test="marc:subfield[@code='2'] = 'iso639-3'">
				<xsl:for-each select="marc:subfield[contains($codes, @code)]">
					<dc:language xsi:type="dcterms:ISO639-3">
						<xsl:value-of select="." />
					</dc:language>
				</xsl:for-each>
			</xsl:if>
		</xsl:for-each>

		<!-- Language - RFC1766 -->
		<xsl:for-each select="marc:datafield[@tag='041']">
			<xsl:if test="marc:subfield[@code='2'] = 'rfc1766'">
				<xsl:for-each select="marc:subfield[contains($codes, @code)]">
					<dc:language xsi:type="dcterms:RFC1766">
						<xsl:value-of select="." />
					</dc:language>
				</xsl:for-each>
			</xsl:if>
		</xsl:for-each>

		<!-- Language - RFC3066 -->
		<xsl:for-each select="marc:datafield[@tag='041']">
			<xsl:if test="marc:subfield[@code='2'] = 'rfc3066'">
				<xsl:for-each select="marc:subfield[contains($codes, @code)]">
					<dc:language xsi:type="dcterms:RFC3066">
						<xsl:value-of select="." />
					</dc:language>
				</xsl:for-each>
			</xsl:if>
		</xsl:for-each>

		<!-- Language - RFC4646 -->
		<xsl:for-each select="marc:datafield[@tag='041']">
			<xsl:if test="marc:subfield[@code='2'] = 'rfc4646'">
				<xsl:for-each select="marc:subfield[contains($codes, @code)]">
					<dc:language xsi:type="dcterms:RFC4646">
						<xsl:value-of select="." />
					</dc:language>
				</xsl:for-each>
			</xsl:if>
		</xsl:for-each>

		<!-- Relation - HasFormat - URI -->
		<xsl:for-each
			select="marc:datafield[@tag='530']/marc:subfield[@code='u']|marc:datafield[@tag='776']/marc:subfield[@code='o']">
			<dcterms:hasFormat xsi:type="dcterms:URI">
				<xsl:value-of select="." />
			</dcterms:hasFormat>
		</xsl:for-each>

		<!-- Relation - Has Part - URI -->
		<xsl:for-each select="marc:datafield[@tag='774']/marc:subfield[@code='o']">
			<dcterms:hasPart xsi:type="dcterms:URI">
				<xsl:value-of select="." />
			</dcterms:hasPart>
		</xsl:for-each>

		<!-- Relation - Has Version - URI -->
		<xsl:for-each select="marc:datafield[@tag='775']/marc:subfield[@code='o']">
			<dcterms:hasVersion xsi:type="dcterms:URI">
				<xsl:value-of select="." />
			</dcterms:hasVersion>
		</xsl:for-each>

		<!-- Relation - Is Format Of - URI -->
		<xsl:for-each
			select="marc:datafield[@tag='530']/marc:subfield[@code='u']|marc:datafield[@tag='776']/marc:subfield[@code='o']">
			<dcterms:isFormatOf xsi:type="dcterms:URI">
				<xsl:value-of select="." />
			</dcterms:isFormatOf>
		</xsl:for-each>

		<!-- Relation - Is Part Of - URI -->
		<xsl:for-each select="marc:datafield[@tag='760']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:isPartOf xsi:type="dcterms:URI">
					<xsl:value-of select="." />
				</dcterms:isPartOf>
			</xsl:for-each>
		</xsl:for-each>
		<xsl:for-each select="marc:datafield[@tag='773']/marc:subfield[@code='o']">
			<dcterms:isPartOf xsi:type="dcterms:URI">
				<xsl:value-of select="." />
			</dcterms:isPartOf>
		</xsl:for-each>

		<!-- Relation - Is Replaced By - URI -->
		<xsl:for-each select="marc:datafield[@tag='785']/marc:subfield[@code='o']">
			<dcterms:isReplacedBy xsi:type="dcterms:URI">
				<xsl:value-of select="." />
			</dcterms:isReplacedBy>
		</xsl:for-each>

		<!-- Relation - Is Version Of - URI -->
		<xsl:for-each select="marc:datafield[@tag='775']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:isVersionOf xsi:type="dcterms:URI">
					<xsl:value-of select="." />
				</dcterms:isVersionOf>
			</xsl:for-each>
		</xsl:for-each>
		<xsl:for-each select="marc:datafield[@tag='786']/marc:subfield[@code='o']">
			<dcterms:isVersionOf xsi:type="dcterms:URI">
				<xsl:value-of select="." />
			</dcterms:isVersionOf>
		</xsl:for-each>

		<!-- Relation - Replaces - URI -->
		<xsl:for-each select="marc:datafield[@tag='780']/marc:subfield[@code='o']">
			<dcterms:replaces xsi:type="dcterms:URI">
				<xsl:value-of select="." />
			</dcterms:replaces>
		</xsl:for-each>

		<!-- Coverage - Spatial - ISO 3166 -->
		<xsl:for-each
			select="marc:datafield[@tag='034']/marc:subfield[@code='c']|marc:datafield[@tag='044']/marc:subfield[@code='c']">
			<dcterms:spatial xsi:type="dcterms:ISO3166">
				<xsl:value-of select="." />
			</dcterms:spatial>
		</xsl:for-each>

		<!-- Coverage - Spatial - TGN -->
		<xsl:for-each
			select="marc:datafield[@tag='651' and @ind2='7' and marc:subfield[@code='2'] = 'tgn']">
			<xsl:for-each select="marc:subfield[contains($codes, @code)]">
				<dcterms:spatial xsi:type="dcterms:TGN">
					<xsl:value-of select="." />
				</dcterms:spatial>
			</xsl:for-each>
		</xsl:for-each>

	</xsl:template>
</xsl:stylesheet>
