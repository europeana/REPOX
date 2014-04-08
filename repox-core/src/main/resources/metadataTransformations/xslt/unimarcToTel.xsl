<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://krait.kb.nl/coop/tel/handbook/telterms.html" xmlns:rpx="info:lc/xmlns/marcxchange-v1" xmlns:dc="http://purl.org/dc/elements/1.1/" version="1.0">  
  <xsl:output method="xml" indent="yes"/>  
  <xsl:template match="/"> 
    <record xmlns:tel="http://krait.kb.nl/coop/tel/handbook/telterms.html">  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='712' or @tag='722' ]/rpx:subfield[@code='a']"> 
        <dc:contributor> 
          <xsl:value-of select="."/> 
        </dc:contributor> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='702']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='b'] or rpx:subfield[@code='f']"> 
          <dc:contributor> 
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
          </dc:contributor> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='600' or @tag='601' or @tag='605' or @tag='606']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='b'] or rpx:subfield[@code='c'] or rpx:subfield[@code='y'] or rpx:subfield[@code='z']"> 
          <dc:coverage> 
            <xsl:if test="rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='b']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='b']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='c']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='c']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='y']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='y']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='z']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='z']"/> 
            </xsl:if> 
          </dc:coverage> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='700' or @tag='701' or @tag='710' or @tag='711' or @tag='720' or @tag='721']"> 
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
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='210']/rpx:subfield[@code='d']"> 
        <dc:date> 
          <xsl:value-of select="."/> 
        </dc:date> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='205' or @tag='207' or @tag='300' or @tag='303' or @tag='304'  or @tag='307' or @tag='305' or @tag='308' or @tag='317' or @tag='320' or @tag='326' or @tag='330' or @tag='327' or @tag='328']/rpx:subfield[@code='a']"> 
        <dc:description> 
          <xsl:value-of select="."/> 
        </dc:description> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='321']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='b']"> 
          <dc:description> 
            <xsl:if test="rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='b']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='b']"/> 
            </xsl:if> 
          </dc:description> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='230']/rpx:subfield[@code='a']"> 
        <dc:format> 
          <xsl:value-of select="."/> 
        </dc:format> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='336']/rpx:subfield[@code='a']"> 
        <dc:format> 
          <xsl:value-of select="."/> 
        </dc:format> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='215']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='c'] or rpx:subfield[@code='d'] or rpx:subfield[@code='e']"> 
          <dc:format> 
            <xsl:if test="rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='c']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='c']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='d']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='d']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='e']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='e']"/> 
            </xsl:if> 
          </dc:format> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:if test="/rpx:record/rpx:datafield[@tag='013']/rpx:subfield[@code='a']"> 
        <dc:identifier> 
          <xsl:if test="/rpx:record/rpx:datafield[@tag='013']/rpx:subfield[@code='a']">URN:ISMN:</xsl:if>  
          <xsl:if test="/rpx:record/rpx:datafield[@tag='013']/rpx:subfield[@code='a']"> 
            <xsl:text xml:space="preserve"> </xsl:text>  
            <xsl:value-of select="/rpx:record/rpx:datafield[@tag='013']/rpx:subfield[@code='a']"/> 
          </xsl:if> 
        </dc:identifier> 
      </xsl:if>  
      <xsl:if test="/rpx:record/rpx:datafield[@tag='020']/rpx:subfield[@code='a']"> 
        <dc:identifier> 
          <xsl:if test="/rpx:record/rpx:datafield[@tag='020']/rpx:subfield[@code='a']">URN:NBN:</xsl:if>  
          <xsl:if test="/rpx:record/rpx:datafield[@tag='020']/rpx:subfield[@code='a']"> 
            <xsl:text xml:space="preserve"> </xsl:text>  
            <xsl:value-of select="/rpx:record/rpx:datafield[@tag='020']/rpx:subfield[@code='a']"/> 
          </xsl:if> 
        </dc:identifier> 
      </xsl:if>  
      <xsl:if test="/rpx:record/rpx:datafield[@tag='071']/rpx:subfield[@code='a']"> 
        <dc:identifier> 
          <xsl:if test="/rpx:record/rpx:datafield[@tag='071']/rpx:subfield[@code='a']">Publishers' Numbers for Music:</xsl:if>  
          <xsl:if test="/rpx:record/rpx:datafield[@tag='071']/rpx:subfield[@code='a']"> 
            <xsl:text xml:space="preserve"> </xsl:text>  
            <xsl:value-of select="/rpx:record/rpx:datafield[@tag='071']/rpx:subfield[@code='a']"/> 
          </xsl:if> 
        </dc:identifier> 
      </xsl:if>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='255']/rpx:subfield[@code='x']"> 
        <dc:identifier> 
          <xsl:value-of select="."/> 
        </dc:identifier> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='021']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='b']"> 
          <dc:identifier> 
            <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='b']">URN:LDN:</xsl:if>  
            <xsl:if test="rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='b']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='b']"/> 
            </xsl:if> 
          </dc:identifier> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='856']"> 
        <xsl:if test="rpx:subfield[@code='u'] or rpx:subfield[@code='g']"> 
          <dc:identifier xsi:type="dcterms:URI"> 
            <xsl:if test="rpx:subfield[@code='u']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='u']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='g']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='g']"/> 
            </xsl:if> 
          </dc:identifier> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='010']/rpx:subfield[@code='a']"> 
        <dc:identifier xsi:type="lib:ISBN"> 
          <xsl:value-of select="."/> 
        </dc:identifier> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='011']/rpx:subfield[@code='a']"> 
        <dc:identifier xsi:type="lib:ISSN"> 
          <xsl:value-of select="."/> 
        </dc:identifier> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='101']/rpx:subfield[@code='a']"> 
        <dc:language xsi:type="dcterms:ISO639-2"> 
          <xsl:value-of select="."/> 
        </dc:language> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='210']/rpx:subfield[@code='a']"> 
        <dc:publisher> 
          <xsl:value-of select="."/> 
        </dc:publisher> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='210']/rpx:subfield[@code='c']"> 
        <dc:publisher> 
          <xsl:value-of select="."/> 
        </dc:publisher> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='225']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='e'] or rpx:subfield[@code='h'] or rpx:subfield[@code='i'] or rpx:subfield[@code='v']"> 
          <dc:relation> 
            <xsl:if test="rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='e']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='e']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='h']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='h']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='i']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='i']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='v']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='v']"/> 
            </xsl:if> 
          </dc:relation> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='324']/rpx:subfield[@code='a']"> 
        <dc:source> 
          <xsl:value-of select="."/> 
        </dc:source> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='600' or @tag='601' or @tag='605' or @tag='606' or @tag='610' or @tag='686']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='b'] or rpx:subfield[@code='c'] or rpx:subfield[@code='y'] or rpx:subfield[@code='z']"> 
          <dc:subject> 
            <xsl:if test="rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='b']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='b']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='c']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='c']"/> 
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
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='675']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='b'] or rpx:subfield[@code='c'] or rpx:subfield[@code='y'] or rpx:subfield[@code='z']"> 
          <dc:subject xsi:type="dcterms:UDC"> 
            <xsl:if test="rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='b']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='b']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='c']">,
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='c']"/> 
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
      <xsl:for-each select="/rpx:record"> 
        <xsl:if test="rpx:datafield[@tag='500']/rpx:subfield[@code='a'] or rpx:datafield[@tag='530']/rpx:subfield[@code='a'] or rpx:datafield[@tag='518']/rpx:subfield[@code='a'] or rpx:datafield[@tag='532']/rpx:subfield[@code='a']"> 
          <dc:terms> 
            <xsl:if test="rpx:datafield[@tag='500']/rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:datafield[@tag='500']/rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:datafield[@tag='530']/rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:datafield[@tag='530']/rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:datafield[@tag='518']/rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:datafield[@tag='518']/rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:datafield[@tag='532']/rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:datafield[@tag='532']/rpx:subfield[@code='a']"/> 
            </xsl:if> 
          </dc:terms> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='517']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='e']"> 
          <dc:terms> 
            <xsl:if test="rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='e']">:
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='e']"/> 
            </xsl:if> 
          </dc:terms> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='200']"> 
        <xsl:if test="rpx:subfield[@code='a'] or rpx:subfield[@code='c'] or rpx:subfield[@code='d'] or rpx:subfield[@code='e'] or rpx:subfield[@code='i']"> 
          <dc:title> 
            <xsl:if test="rpx:subfield[@code='a']"> 
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='a']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='c']">.
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='c']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='d']">=
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='d']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='e']">:
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='e']"/> 
            </xsl:if>  
            <xsl:if test="rpx:subfield[@code='i']">.
              <xsl:text xml:space="preserve"> </xsl:text>  
              <xsl:value-of select="rpx:subfield[@code='i']"/> 
            </xsl:if> 
          </dc:title> 
        </xsl:if> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='135']/rpx:subfield[@code='a']"> 
        <dc:type> 
          <xsl:value-of select="."/> 
        </dc:type> 
      </xsl:for-each>  
      <xsl:for-each select="/rpx:record/rpx:datafield[@tag='200']/rpx:subfield[@code='b']"> 
        <dc:type> 
          <xsl:value-of select="."/> 
        </dc:type> 
      </xsl:for-each> 
    </record> 
  </xsl:template> 
</xsl:stylesheet>
