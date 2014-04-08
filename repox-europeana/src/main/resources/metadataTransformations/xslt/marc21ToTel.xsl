<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://krait.kb.nl/coop/tel/handbook/telterms.html" xmlns:rpx="info:lc/xmlns/marcxchange-v1" xmlns:dc="http://purl.org/dc/elements/1.1/" version="1.0">  
  <xsl:output method="xml" indent="yes"/>  
  <xsl:template match="/"> 
    <record xmlns:tel="http://krait.kb.nl/coop/tel/handbook/telterms.html">  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='710' or @tag='711' or @tag='720' or @tag='700' or    @tag='100' or @tag='110' or @tag='111']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='b'] or rpx:subfield[@code='f']"> 
          <dc:creator> 
            <xsl:if test="rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='b']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='b']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='f']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='f']"/> 
            </xsl:if> 
          </dc:creator> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='800' or @tag='801' or @tag='810' or @tag='811' or @tag='820' or @tag='821']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='b'] or rpx:subfield[@code='f']"> 
          <dc:creator> 
            <xsl:if test="rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='b']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='b']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='f']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='f']"/> 
            </xsl:if> 
          </dc:creator> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='245']/rpx:subfield[@code='c']"> 
        <dc:description> 
          <xsl:value-of select="."/> 
        </dc:description> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='245']/rpx:subfield[@code='h']"> 
        <dc:description> 
          <xsl:value-of select="."/> 
        </dc:description> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='500']/rpx:subfield[@code='a']"> 
        <dc:description> 
          <xsl:value-of select="."/> 
        </dc:description> 
      </xsl:for-each>  
      <xsl:if test="/rpx:record/rpx:datafield[@tag='015']/rpx:subfield[@code='a']"> 
        <dc:identifier> 
          <xsl:if test="/rpx:record/rpx:datafield[@tag='015']/rpx:subfield[@code='a']">URN:NBN:</xsl:if>  
          <xsl:if test="/rpx:record/rpx:datafield[@tag='015']/rpx:subfield[@code='a']"> 
            <xsl:text xml:space="preserve"> </xsl:text>  
            <xsl:value-of select="/rpx:record/rpx:datafield[@tag='015']/rpx:subfield[@code='a']"/> 
          </xsl:if> 
        </dc:identifier> 
      </xsl:if>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='856']/rpx:subfield[@code='u' or @code='g']"> 
        <dc:identifier xsi:type="dcterms:URI"> 
          <xsl:value-of select="."/> 
        </dc:identifier> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='020']/rpx:subfield[@code='a']"> 
        <dc:identifier xsi:type="lib:ISBN"> 
          <xsl:value-of select="."/> 
        </dc:identifier> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='022' ]/rpx:subfield[@code='a']"> 
        <dc:identifier xsi:type="lib:ISSN"> 
          <xsl:value-of select="."/> 
        </dc:identifier> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='041']/rpx:subfield[@code='a']"> 
        <dc:language xsi:type="dcterms:ISO639-2"> 
          <xsl:value-of select="."/> 
        </dc:language> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='260']/rpx:subfield[@code='a']"> 
        <dc:publisher> 
          <xsl:value-of select="."/> 
        </dc:publisher> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='260']/rpx:subfield[@code='b']"> 
        <dc:publisher> 
          <xsl:value-of select="."/> 
        </dc:publisher> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='243']/rpx:subfield[@code='a']"> 
        <dc:relation> 
          <xsl:value-of select="."/> 
        </dc:relation> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='561']/rpx:subfield[@code='a']"> 
        <dc:source> 
          <xsl:value-of select="."/> 
        </dc:source> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='650' or @tag='651']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='b'] or rpx:subfield[@code='x'] or rpx:subfield[@code='y'] or rpx:subfield[@code='z']"> 
          <dc:subject> 
            <xsl:if test="rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='b']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='b']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='x']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='x']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='y']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='y']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='z']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='z']"/> 
            </xsl:if> 
          </dc:subject> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='130' or @tag='210' or @tag='222' or @tag='240' or @tag='242' or @tag='246' or @tag='730' or @tag='740']/rpx:subfield[@code='a']"> 
        <dc:terms> 
          <xsl:value-of select="."/> 
        </dc:terms> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='245']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='b'] or rpx:subfield[@code='n'] or rpx:subfield[@code='p']"> 
          <dc:title> 
            <xsl:if test="rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='b']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='b']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='n']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='n']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='p']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='p']"/> 
            </xsl:if> 
          </dc:title> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='260']/rpx:subfield[@code='c']"> 
        <dc:type> 
          <xsl:value-of select="."/> 
        </dc:type> 
      </xsl:for-each> 
    </record> 
  </xsl:template> 
</xsl:stylesheet>
