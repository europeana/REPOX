<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0" xmlns="http://www.digmap.eu/schemas/resource/"
xmlns:bn="info:lc/xmlns/marcxchange-v1" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
xsi:schemaLocation="http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2003/04/02/dcterms.xsd"
>
	

<xsl:template name="unimarc2dc_qualified_identifier" xmlns:dcterms="http://purl.org/dc/terms/">

	<!-- dc:Identifier -->
	<xsl:for-each select="//bn:controlfield[@tag='001']">
		<dc:identifier xsi:type='dcterms:URI'><xsl:text>http://opac.porbase.org/ipac20/ipac.jsp?profile=porbase&amp;uri=full=3100024@!</xsl:text><xsl:value-of select="."/><xsl:text>@!0&amp;ri=1&amp;aspect=basic_search&amp;menu=search&amp;source=192.168.0.17@!porbase&amp;ipp=20&amp;staffonly=&amp;term=&amp;index=&amp;uindex=&amp;aspect=basic_search&amp;menu=search&amp;ri=1</xsl:text></dc:identifier>
	</xsl:for-each>
	<xsl:for-each select="//bn:controlfield[@tag='001']">
		<dc:identifier><xsl:text>Id. do registo: </xsl:text><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>						
	<xsl:for-each select="//bn:datafield[@tag='010']/bn:subfield[@code='a']">
		<dc:identifier><xsl:text>URN:ISBN:</xsl:text><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>						
	<xsl:for-each select="//bn:datafield[@tag='011' ]/bn:subfield[@code='a']">
		<dc:identifier><xsl:text>URN:ISSN:</xsl:text><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>						
	<xsl:for-each select="//bn:datafield[@tag='013' ]/bn:subfield[@code='a']">
		<dc:identifier><xsl:text>URN:ISMN:</xsl:text><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>						
	<xsl:for-each select="//bn:datafield[@tag='020']/bn:subfield[@code='a']">
		<dc:identifier><xsl:text>URN:NBN:</xsl:text><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>						
	<xsl:for-each select="//bn:datafield[@tag='071']/bn:subfield[@code='a']">
		<dc:identifier><xsl:text>NÂº do Editor:</xsl:text><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>						
	<xsl:for-each select="//bn:datafield[@tag='255']/bn:subfield[@code='x']">
		<dc:identifier><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='966']">
		<dc:identifier>
			<xsl:text>Cota: </xsl:text>
			<xsl:if test=".//bn:subfield[@code='d']">
				<xsl:value-of select=".//bn:subfield[@code='d']"/>
				<xsl:text> </xsl:text>
			</xsl:if>
			<xsl:value-of select=".//bn:subfield[@code='l']"/><xsl:text>-</xsl:text><xsl:value-of select=".//bn:subfield[@code='s']"/>
		</dc:identifier>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='021']">
		<dc:identifier><xsl:text>URN:LDN:</xsl:text><xsl:value-of select=".//bn:subfield[@code='a']"/><xsl:text>:</xsl:text><xsl:value-of select=".//bn:subfield[@code='b']"/></dc:identifier>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='856']/bn:subfield[@code='u' or @code='g']">
		<dc:identifier xsi:type='dcterms:URI'><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>						

</xsl:template>


<xsl:template name="unimarc2dc_qualified" xmlns:dcterms="http://purl.org/dc/terms/">

	<!-- dc:Title -->
	<xsl:for-each select="//bn:datafield[@tag='200']">
		<dc:title>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:for-each select=".//bn:subfield[@code='c']">
				<xsl:text>. </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='d']">
				<xsl:text> = </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='e']">
				<xsl:text>: </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='i']">
				<xsl:text>. </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
		</dc:title>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='500']/bn:subfield[@code='a'] | //bn:datafield[@tag='530']/bn:subfield[@code='a'] | //bn:datafield[@tag='518']/bn:subfield[@code='a'] | //bn:datafield[@tag='532']/bn:subfield[@code='a']">
		<dcterms:alternative><xsl:value-of select="."/></dcterms:alternative>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='517']">
		<dcterms:alternative>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:if test=".//bn:subfield[@code='e']">
				<xsl:text>: </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='e']"/>  
			</xsl:if>
		</dcterms:alternative>
	</xsl:for-each>


	<!-- dc:Creator -->
	<xsl:for-each select="//bn:datafield[@tag='710' or @tag='711' or @tag='720' or @tag='721']"> <!-- @tag='700' or @tag='701' or     :: done in Java-->
		<dc:creator>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:if test=".//bn:subfield[@code='b']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='b']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='f']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='f']"/>  
			</xsl:if>
		</dc:creator>
	</xsl:for-each>



	<!-- dc:Subject -->
	<xsl:for-each select="//bn:datafield[@tag='600' or @tag='601' or @tag='605' or @tag='606' or @tag='610' or @tag='686']">
		<dc:subject>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>  
			<xsl:if test=".//bn:subfield[@code='b']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='b']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='c']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='c']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='y']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='y']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='z']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='z']"/>  
			</xsl:if>
		</dc:subject>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='675']">
		<dc:subject xsi:type="dcterms:UDC">
			<xsl:value-of select=".//bn:subfield[@code='a']"/>  
			<xsl:if test=".//bn:subfield[@code='b']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='b']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='c']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='c']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='y']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='y']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='z']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='z']"/>  
			</xsl:if>
		</dc:subject>
	</xsl:for-each>

	


	<!-- dc:Description -->
	<xsl:choose>
		<xsl:when test="substring( //bn:leader , 8,1)='c'"><!-- miscelanea -->
			<dc:description>MiscelÃ¢nea</dc:description>
			<xsl:for-each select="//bn:datafield[@tag='481']">
				<dc:description>
					<xsl:if test=".//bn:subfield[@code='a']">
						<xsl:value-of select=".//bn:subfield[@code='a']"/> 
						<xsl:text>, </xsl:text>
					</xsl:if>
					<xsl:value-of select=".//bn:subfield[@code='t']"/> 
					<xsl:if test=".//bn:subfield[@code='c']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='c']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='d']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='d']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='5']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='5']"/> 
					</xsl:if>
				</dc:description>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="substring( //bn:leader , 8,1)='a'"><!-- analitico periodico ou monografico-->
			<dc:description>AnalÃ­tico</dc:description>
			<xsl:for-each select="//bn:datafield[@tag='461']">
				<dc:description>
					<xsl:value-of select=".//bn:subfield[@code='t']"/> 
					<xsl:if test=".//bn:subfield[@code='h']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='h']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='i']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='i']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='c']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='c']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='x']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='x']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='v']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='v']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='p']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='p']"/> 
					</xsl:if>
				</dc:description>
			</xsl:for-each>
			<xsl:for-each select="//bn:datafield[@tag='463']">
				<dc:description>
					<xsl:if test=".//bn:subfield[@code='a']">
						<xsl:value-of select=".//bn:subfield[@code='a']"/> 
						<xsl:text>, </xsl:text>
					</xsl:if>
					<xsl:value-of select=".//bn:subfield[@code='t']"/> 
					<xsl:if test=".//bn:subfield[@code='h']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='h']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='e']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='e']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='c']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='c']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='d']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='d']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='i']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='i']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='v']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='v']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='x']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='x']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='y']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='y']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='p']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='p']"/> 
					</xsl:if>
				</dc:description>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="substring( //bn:leader , 8,1)='m'"><!-- monografia -->
			<xsl:choose>
			  <xsl:when test="//bn:datafield[@tag='481']">
					<dc:description>MiscelÃ¢nea</dc:description>
			  </xsl:when>
			  <xsl:otherwise>
					<dc:description>Monografia</dc:description>
			  </xsl:otherwise>
			</xsl:choose>
			<xsl:for-each select="//bn:datafield[@tag='481']">
				<dc:description>
					<xsl:if test=".//bn:subfield[@code='a']">
						<xsl:value-of select=".//bn:subfield[@code='a']"/> 
						<xsl:text>, </xsl:text>
					</xsl:if>
					<xsl:value-of select=".//bn:subfield[@code='t']"/> 
					<xsl:if test=".//bn:subfield[@code='c']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='c']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='d']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='d']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='5']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='5']"/> 
					</xsl:if>
				</dc:description>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="substring( //bn:leader , 8,1)='s'"><!-- serie -->
			<dc:description>SÃ©rie</dc:description>
		</xsl:when>
	</xsl:choose>



	<xsl:for-each select="//bn:datafield[@tag='205' or @tag='207' or @tag='300' or @tag='303' or @tag='304'  or @tag='307' or @tag='305' or @tag='308' or @tag='317' or @tag='320' or @tag='326' or @tag='330' or @tag='327' or @tag='328']/bn:subfield[@code='a']">
		<dc:description><xsl:value-of select="."/></dc:description>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='321']">
		<dc:description><xsl:value-of select=".//bn:subfield[@code='a']"/> <xsl:value-of select=".//bn:subfield[@code='b']"/></dc:description>
	</xsl:for-each>


	<xsl:for-each select="//bn:datafield[@tag='206']/bn:subfield[@code='a']">
		<cartographic-math-statement><xsl:value-of select="."/></cartographic-math-statement>
	</xsl:for-each>


	<!-- dc:Publisher -->
	<xsl:for-each select="//bn:datafield[@tag='210']/bn:subfield[@code='c']">
		<dc:publisher><xsl:value-of select="."/></dc:publisher>
	</xsl:for-each>			
	<xsl:for-each select="//bn:datafield[@tag='210']/bn:subfield[@code='a']">
		<dc:publisher><xsl:value-of select="."/></dc:publisher>
	</xsl:for-each>

	<!-- dc:Contributor -->
	<!-- xsl:for-each select="//bn:datafield[@tag='702']">
		<dc:contributor>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:if test=".//bn:subfield[@code='b']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='b']"/> 
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='f']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='f']"/>  
			</xsl:if>
		</dc:contributor>
	</xsl:for-each   ::Done in Java-->
	<xsl:for-each select="//bn:datafield[@tag='712' or @tag='722' ]">
		<dc:contributor><xsl:value-of select=".//bn:subfield[@code='a']"/></dc:contributor>
	</xsl:for-each>
				
	<!-- dc:Date -->
	<xsl:for-each select="//bn:datafield[@tag='210']/bn:subfield[@code='d']">
		<dc:date><xsl:value-of select="."/></dc:date>
	</xsl:for-each>						

	<!-- dc:Type -->
	<!--xsl:for-each select="//bn:datafield[@tag='336']/bn:subfield[@code='a']">
		<dc:type><xsl:value-of select="."/></dc:type>
	</xsl:for-each-->
	<xsl:choose>
		<xsl:when test="substring( //bn:leader , 7,1)='a'">
			<dc:type>material textual, impresso</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='b'">
			<dc:type>material textual, manuscrito</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='c'">
			<dc:type>partituras musicais, impressas</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='d'">
			<dc:type>partituras musicais, manuscritas</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='e'">
			<dc:type>material cartogrÃ¡fico, impresso</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='f'">
			<dc:type>material cartogrÃ¡fico, manuscrito</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='g'">
			<dc:type>material de projecÃ§Ã£o e vÃ­deo</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='i'">
			<dc:type>registos sonoros, nÃ£o musicais</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='j'">
			<dc:type>registos sonoros, musicais</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='k'">
			<dc:type>material grÃ¡fico a duas dimensÃµes</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='l'">
			<dc:type>produtos de computador</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='m'">
			<dc:type>multimÃ©dia</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='r'">
			<dc:type>artefactos a trÃªs dimensÃµes e realia</dc:type>
		</xsl:when> 
	</xsl:choose>
	<xsl:for-each select="//bn:datafield[@tag='135']/bn:subfield[@code='a']">
		<dc:type><xsl:value-of select="substring( . , 2,1)"/></dc:type>
	</xsl:for-each>	
	<xsl:for-each select="//bn:datafield[@tag='200']/bn:subfield[@code='b']">
		<dc:type><xsl:value-of select="."/></dc:type>
	</xsl:for-each>	

	<!-- dc:Format -->
	<xsl:for-each select="//bn:datafield[@tag='336']/bn:subfield[@code='a']">
		<dc:format><xsl:value-of select="."/></dc:format>
	</xsl:for-each>			
	<xsl:for-each select="//bn:datafield[@tag='215']">
		<dc:format>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:if test=".//bn:subfield[@code='c']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='c']"/>
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='d']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='d']"/>
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='e']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='e']"/>
			</xsl:if>
		</dc:format>
	</xsl:for-each>	
	<xsl:for-each select="//bn:datafield[@tag='230']/bn:subfield[@code='a']">
		<dc:format><xsl:value-of select="."/></dc:format>
	</xsl:for-each>						
	











	<!-- dc:source -->
	<xsl:for-each select="//bn:datafield[@tag='324']/bn:subfield[@code='a']">
		<dc:source><xsl:value-of select="."/></dc:source>
	</xsl:for-each>						

	<!-- dc:Relation -->
	<xsl:for-each select="//bn:datafield[@tag='225']">
		<dc:relation>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:for-each select=".//bn:subfield[@code='e']">
				<xsl:text>: </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='h']">
				<xsl:text>; </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='i']">
				<xsl:text>. </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='v']">
				<xsl:text>; </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
		</dc:relation>
	</xsl:for-each>


	<!-- dc:Language -->
	<xsl:for-each select="//bn:datafield[@tag='101']/bn:subfield[@code='a']">
		<dc:language xsi:type='dcterms:ISO639-2'><xsl:value-of select="."/></dc:language>
	</xsl:for-each>						

	<!-- dc:Rights -->
	<!--xsl:for-each select="//bn:datafield[@tag='310']/bn:subfield[@code='a']">
		<dc:rights><xsl:value-of select="."/></dc:rights>
	</xsl:for-each-->


	<!-- dc:coverage -->
	<xsl:for-each select="//bn:datafield[@tag='600' or @tag='601' or @tag='605' or @tag='606']">
		<xsl:if test=".//bn:subfield[@code='z' or code='y']">
			<dc:coverage>
				<xsl:value-of select=".//bn:subfield[@code='a']"/>  
				<xsl:if test=".//bn:subfield[@code='b']">
					<xsl:text>, </xsl:text>
					<xsl:value-of select=".//bn:subfield[@code='b']"/>  
				</xsl:if>
				<xsl:if test=".//bn:subfield[@code='c']">
					<xsl:text>, </xsl:text>
					<xsl:value-of select=".//bn:subfield[@code='c']"/>  
				</xsl:if>
				<xsl:if test=".//bn:subfield[@code='y']">
					<xsl:text>, </xsl:text>
					<xsl:value-of select=".//bn:subfield[@code='y']"/>  
				</xsl:if>
				<xsl:if test=".//bn:subfield[@code='z']">
					<xsl:text>, </xsl:text>
					<xsl:value-of select=".//bn:subfield[@code='z']"/>  
				</xsl:if>
			</dc:coverage>
		</xsl:if>
	</xsl:for-each>

</xsl:template>    






<xsl:template name="unimarc2dc_simple">

	<!-- dc:Title -->
	<xsl:for-each select="//bn:datafield[@tag='200']">
		<dc:title>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:for-each select=".//bn:subfield[@code='c']">
				<xsl:text>. </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='d']">
				<xsl:text> = </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='e']">
				<xsl:text>: </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='i']">
				<xsl:text>. </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
		</dc:title>
	</xsl:for-each>

	<xsl:for-each select="//bn:datafield[@tag='517']/bn:subfield[@code='a'] | //bn:datafield[@tag='500']/bn:subfield[@code='a'] | //bn:datafield[@tag='530']/bn:subfield[@code='a'] | //bn:datafield[@tag='518']/bn:subfield[@code='a'] | //bn:datafield[@tag='532']/bn:subfield[@code='a']">
		<dc:title><xsl:value-of select="."/></dc:title>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='517']">
		<dc:title>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:if test=".//bn:subfield[@code='e']">
				<xsl:text>: </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='e']"/>  
			</xsl:if>
		</dc:title>
	</xsl:for-each>

	<!-- dc:Creator -->
	<xsl:for-each select="//bn:datafield[@tag='700' or @tag='701' or @tag='710' or @tag='711' or @tag='720' or @tag='721']">
		<dc:creator>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:if test=".//bn:subfield[@code='b']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='b']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='f']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='f']"/>  
			</xsl:if>
		</dc:creator>
	</xsl:for-each>
<!--  I'm not shure about mapping 200$f to creator -->
<!--				<xsl:for-each select="//bn:datafield[@tag='200']/bn:subfield[@code='f']">
		<creator><xsl:value-of select="."/></creator>
	</xsl:for-each>  -->

	<!-- dc:Subject -->
	<xsl:for-each select="//bn:datafield[@tag='600' or @tag='601' or @tag='605' or @tag='606' or @tag='610' or @tag='675' or @tag='686']">
		<dc:subject>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>  
			<xsl:if test=".//bn:subfield[@code='b']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='b']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='c']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='c']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='y']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='y']"/>  
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='z']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='z']"/>  
			</xsl:if>
		</dc:subject>
	</xsl:for-each>



	<!-- dc:Description -->
	<xsl:choose>
		<xsl:when test="substring( //bn:leader , 8,1)='c'"><!-- miscelanea -->
			<dc:description>MiscelÃ¢nea</dc:description>
			<xsl:for-each select="//bn:datafield[@tag='481']">
				<dc:description>
					<xsl:if test=".//bn:subfield[@code='a']">
						<xsl:value-of select=".//bn:subfield[@code='a']"/> 
						<xsl:text>, </xsl:text>
					</xsl:if>
					<xsl:value-of select=".//bn:subfield[@code='t']"/> 
					<xsl:if test=".//bn:subfield[@code='c']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='c']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='d']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='d']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='5']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='5']"/> 
					</xsl:if>
				</dc:description>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="substring( //bn:leader , 8,1)='a'"><!-- analitico periodico ou monografico-->
			<dc:description>AnalÃ­tico</dc:description>
			<xsl:for-each select="//bn:datafield[@tag='461']">
				<dc:description>
					<xsl:value-of select=".//bn:subfield[@code='t']"/> 
					<xsl:if test=".//bn:subfield[@code='h']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='h']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='i']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='i']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='c']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='c']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='x']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='x']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='v']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='v']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='p']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='p']"/> 
					</xsl:if>
				</dc:description>
			</xsl:for-each>
			<xsl:for-each select="//bn:datafield[@tag='463']">
				<dc:description>
					<xsl:if test=".//bn:subfield[@code='a']">
						<xsl:value-of select=".//bn:subfield[@code='a']"/> 
						<xsl:text>, </xsl:text>
					</xsl:if>
					<xsl:value-of select=".//bn:subfield[@code='t']"/> 
					<xsl:if test=".//bn:subfield[@code='h']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='h']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='e']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='e']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='c']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='c']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='d']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='d']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='i']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='i']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='v']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='v']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='x']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='x']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='y']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='y']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='p']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='p']"/> 
					</xsl:if>
				</dc:description>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="substring( //bn:leader , 8,1)='m'"><!-- monografia -->
			<xsl:choose>
			  <xsl:when test="//bn:datafield[@tag='481']">
					<dc:description>MiscelÃ¢nea</dc:description>
			  </xsl:when>
			  <xsl:otherwise>
					<dc:description>Monografia</dc:description>
			  </xsl:otherwise>
			</xsl:choose>
			<xsl:for-each select="//bn:datafield[@tag='481']">
				<dc:description>
					<xsl:if test=".//bn:subfield[@code='a']">
						<xsl:value-of select=".//bn:subfield[@code='a']"/> 
						<xsl:text>, </xsl:text>
					</xsl:if>
					<xsl:value-of select=".//bn:subfield[@code='t']"/> 
					<xsl:if test=".//bn:subfield[@code='c']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='c']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='d']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='d']"/> 
					</xsl:if>
					<xsl:if test=".//bn:subfield[@code='5']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select=".//bn:subfield[@code='5']"/> 
					</xsl:if>
				</dc:description>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="substring( //bn:leader , 8,1)='s'"><!-- serie -->
			<dc:description>SÃ©rie</dc:description>
		</xsl:when>
	</xsl:choose>


	<xsl:for-each select="//bn:datafield[@tag='205' or @tag='207' or @tag='300' or @tag='303' or @tag='304' or @tag='305' or @tag='308' or @tag='320' or @tag='326' or @tag='330' or @tag='327' or @tag='328']/bn:subfield[@code='a']">
		<dc:description><xsl:value-of select="."/></dc:description>
	</xsl:for-each>




	<!-- dc:Publisher -->
	<xsl:for-each select="//bn:datafield[@tag='210']/bn:subfield[@code='c']">
		<dc:publisher><xsl:value-of select="."/></dc:publisher>
	</xsl:for-each>			
	<xsl:for-each select="//bn:datafield[@tag='210']/bn:subfield[@code='a']">
		<dc:publisher><xsl:value-of select="."/></dc:publisher>
	</xsl:for-each>

	<!-- dc:Contributor -->
	<xsl:for-each select="//bn:datafield[@tag='702']">
		<dc:contributor>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:if test=".//bn:subfield[@code='b']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='b']"/> 
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='f']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='f']"/>  
			</xsl:if>
		</dc:contributor>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='712' or @tag='722' ]">
		<dc:contributor><xsl:value-of select=".//bn:subfield[@code='a']"/></dc:contributor>
	</xsl:for-each>
				
	<!-- dc:Date -->
	<xsl:for-each select="//bn:datafield[@tag='210']/bn:subfield[@code='d']">
		<dc:date><xsl:value-of select="."/></dc:date>
	</xsl:for-each>						

	<!-- dc:Type -->
	<!--xsl:for-each select="//bn:datafield[@tag='336']/bn:subfield[@code='a']">
		<dc:type><xsl:value-of select="."/></dc:type>
	</xsl:for-each-->
	<xsl:choose>
		<xsl:when test="substring( //bn:leader , 7,1)='a'">
			<dc:type>material textual, impresso</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='b'">
			<dc:type>material textual, manuscrito</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='c'">
			<dc:type>partituras musicais, impressas</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='d'">
			<dc:type>partituras musicais, manuscritas</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='e'">
			<dc:type>material cartogrÃ¡fico, impresso</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='f'">
			<dc:type>material cartogrÃ¡fico, manuscrito</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='g'">
			<dc:type>material de projecÃ§Ã£o e vÃ­deo</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='i'">
			<dc:type>registos sonoros, nÃ£o musicais</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='j'">
			<dc:type>registos sonoros, musicais</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='k'">
			<dc:type>material grÃ¡fico a duas dimensÃµes</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='l'">
			<dc:type>produtos de computador</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='m'">
			<dc:type>multimÃ©dia</dc:type>
		</xsl:when> 
		<xsl:when test="substring( //bn:leader , 7,1)='r'">
			<dc:type>artefactos a trÃªs dimensÃµes e realia</dc:type>
		</xsl:when> 
	</xsl:choose>
	<xsl:for-each select="//bn:datafield[@tag='135']/bn:subfield[@code='a']">
		<dc:type><xsl:value-of select="substring( . , 2,1)"/></dc:type>
	</xsl:for-each>	
	<xsl:for-each select="//bn:datafield[@tag='200']/bn:subfield[@code='b']">
		<dc:type><xsl:value-of select="."/></dc:type>
	</xsl:for-each>	

	<!-- dc:Format -->
	<xsl:for-each select="//bn:datafield[@tag='336']/bn:subfield[@code='a']">
		<dc:format><xsl:value-of select="."/></dc:format>
	</xsl:for-each>			
	<xsl:for-each select="//bn:datafield[@tag='215']">
		<dc:format>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:if test=".//bn:subfield[@code='c']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='c']"/>
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='d']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='d']"/>
			</xsl:if>
			<xsl:if test=".//bn:subfield[@code='e']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select=".//bn:subfield[@code='e']"/>
			</xsl:if>
		</dc:format>
	</xsl:for-each>	
	<xsl:for-each select="//bn:datafield[@tag='230']/bn:subfield[@code='a']">
		<dc:format><xsl:value-of select="."/></dc:format>
	</xsl:for-each>						

	<!-- dc:Identifier -->
	<xsl:for-each select="//bn:controlfield[@tag='001']">
		<dc:identifier><xsl:text>Id. do registo: </xsl:text><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>						
	<xsl:for-each select="//bn:datafield[@tag='010']/bn:subfield[@code='a']">
		<dc:identifier><xsl:text>URN:ISBN:</xsl:text><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>						
	<xsl:for-each select="//bn:datafield[@tag='011' ]/bn:subfield[@code='a']">
		<dc:identifier><xsl:text>URN:ISSN:</xsl:text><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>						
	<xsl:for-each select="//bn:datafield[@tag='013' ]/bn:subfield[@code='a']">
		<dc:identifier><xsl:text>URN:ISMN:</xsl:text><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>						
	<xsl:for-each select="//bn:datafield[@tag='020']/bn:subfield[@code='a']">
		<dc:identifier><xsl:text>URN:NBN:</xsl:text><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>						
	<xsl:for-each select="//bn:datafield[@tag='071']/bn:subfield[@code='a']">
		<dc:identifier><xsl:text>NÂº do Editor:</xsl:text><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>						

	<xsl:for-each select="//bn:datafield[@tag='856']/bn:subfield[@code='u' or @code='g']">
		<dc:identifier><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>					
	<xsl:for-each select="//bn:datafield[@tag='255']/bn:subfield[@code='x']">
		<dc:identifier><xsl:value-of select="."/></dc:identifier>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='966']">
		<dc:identifier>
			<xsl:text>Cota: </xsl:text>
			<xsl:if test=".//bn:subfield[@code='d']">
				<xsl:value-of select=".//bn:subfield[@code='d']"/>
				<xsl:text> </xsl:text>
			</xsl:if>
			<xsl:value-of select=".//bn:subfield[@code='l']"/><xsl:text>-</xsl:text><xsl:value-of select=".//bn:subfield[@code='s']"/>
		</dc:identifier>
	</xsl:for-each>
	<xsl:for-each select="//bn:datafield[@tag='021']">
		<dc:identifier><xsl:text>URN:LDN:</xsl:text><xsl:value-of select=".//bn:subfield[@code='a']"/><xsl:text>:</xsl:text><xsl:value-of select=".//bn:subfield[@code='b']"/></dc:identifier>
	</xsl:for-each>

	<!-- dc:source -->
	<xsl:for-each select="//bn:datafield[@tag='324']/bn:subfield[@code='a']">
		<dc:source><xsl:value-of select="."/></dc:source>
	</xsl:for-each>						

	<!-- dc:Relation -->
	<xsl:for-each select="//bn:datafield[@tag='225']">
		<dc:relation>
			<xsl:value-of select=".//bn:subfield[@code='a']"/>
			<xsl:for-each select=".//bn:subfield[@code='e']">
				<xsl:text>: </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='h']">
				<xsl:text>; </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='i']">
				<xsl:text>. </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select=".//bn:subfield[@code='v']">
				<xsl:text>; </xsl:text>
				<xsl:value-of select="."/>
			</xsl:for-each>
		</dc:relation>
	</xsl:for-each>


	<!-- dc:Language -->
<!-- todo: change the language codes to language names -->
	<xsl:for-each select="//bn:datafield[@tag='101']/bn:subfield[@code='a']">
		<dc:language><xsl:value-of select="."/></dc:language>
	</xsl:for-each>						

	<!-- dc:Rights -->
	<!--xsl:for-each select="//bn:datafield[@tag='310']/bn:subfield[@code='a']">
		<dc:rights><xsl:value-of select="."/></dc:rights>
	</xsl:for-each-->


	<!-- dc:coverage -->
	<xsl:for-each select="//bn:datafield[@tag='600' or @tag='601' or @tag='605' or @tag='606']">
		<xsl:if test=".//bn:subfield[@code='z' or code='y']">
			<dc:coverage>
				<xsl:value-of select=".//bn:subfield[@code='a']"/>  
				<xsl:if test=".//bn:subfield[@code='b']">
					<xsl:text>, </xsl:text>
					<xsl:value-of select=".//bn:subfield[@code='b']"/>  
				</xsl:if>
				<xsl:if test=".//bn:subfield[@code='c']">
					<xsl:text>, </xsl:text>
					<xsl:value-of select=".//bn:subfield[@code='c']"/>  
				</xsl:if>
				<xsl:if test=".//bn:subfield[@code='y']">
					<xsl:text>, </xsl:text>
					<xsl:value-of select=".//bn:subfield[@code='y']"/>  
				</xsl:if>
				<xsl:if test=".//bn:subfield[@code='z']">
					<xsl:text>, </xsl:text>
					<xsl:value-of select=".//bn:subfield[@code='z']"/>  
				</xsl:if>
			</dc:coverage>
		</xsl:if>
	</xsl:for-each>

</xsl:template>    





	<xsl:template name="unimarc_aut2dc_simple">
				<!-- dc:Title -->
				<xsl:if test="//bn:datafield[@tag='200' or @tag='210' or @tag='215' or @tag='220' or @tag='230' or @tag='235' or @tag='240' or @tag='245' or @tag='250']">
					<dc:title><xsl:for-each select="//bn:datafield[@tag='200' or @tag='210' or @tag='215' or @tag='220' or @tag='230' or @tag='235' or @tag='240' or @tag='245' or @tag='250']/bn:subfield[@code='a' or @code='b' or @code='f' or @code='k']"><xsl:value-of select="."/> </xsl:for-each></dc:title>
				</xsl:if>

				<!-- dc:Description -->
				<xsl:for-each select="//bn:datafield[@tag='830']/bn:subfield[@code='a']">
					<dc:description><xsl:value-of select="."/></dc:description>
				</xsl:for-each>

				<!-- dc:Date -->
				<xsl:for-each select="//bn:datafield[@tag='200' or @tag='210' or @tag='215' or @tag='220' or @tag='230' or @tag='235' or @tag='240' or @tag='245' or @tag='250']/bn:subfield[@code='f' or @code='k']">
					<dc:date><xsl:value-of select="."/></dc:date>
				</xsl:for-each>						

				<!-- dc:Identifier -->
				<xsl:for-each select="//controlfield[@tag='001']">
					<dc:identifier><xsl:value-of select="."/></dc:identifier>
				</xsl:for-each>						
	</xsl:template>    



</xsl:stylesheet>

