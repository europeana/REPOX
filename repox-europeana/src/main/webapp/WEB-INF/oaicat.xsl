<?xml version="1.0" encoding="UTF-8"?>
<!-- Edited with emacs -->
<!-- New Namespace -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:oai="http://www.openarchives.org/OAI/2.0/"
                              xmlns:oai_id="http://www.openarchives.org/OAI/2.0/oai-identifier"
                              xmlns:oai_branding="http://www.openarchives.org/OAI/2.0/branding/"
                              xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
                              xmlns:oai_etdms="http://www.ndltd.org/standards/metadata/etdms/1.0/"
                              xmlns:dc="http://purl.org/dc/elements/1.1/"
                              xmlns:toolkit="http://oai.dlib.vt.edu/OAI/metadata/toolkit">
  <xsl:output method="html" version="4.0"/>

  <xsl:template match="/oai:OAI-PMH">
    <html>
      <head>
        <title><xsl:value-of select="oai:request/@verb"/> Response [OAICat]</title>
        <style type="text/css">
          <xsl:comment>
            ADDRESS {
              FONT-SIZE: x-small
            }
            BODY {
              FONT-SIZE: x-small; COLOR: #000000
            }
            BUTTON {
              CURSOR: auto
            }
            H2 {
              FONT: bold 18px helvetica, arial, sans-serif; COLOR: #00527c
            }
            HR {
              COLOR: #cccccc; HEIGHT: 1px
            }
            INPUT {
              CURSOR: auto; COLOR: #000000
            }
            SELECT {
              FONT-SIZE: x-small; COLOR: #000000
            }
            TD {
              FONT-SIZE: x-small; MARGIN-LEFT: 0px
            }
            TEXTAREA {
              CURSOR: text; COLOR: #000000; BACKGROUND-COLOR: #ffffff
            }
            TH {
              FONT-WEIGHT: bold; FONT-SIZE: x-small; MARGIN-LEFT: 0px; TEXT-ALIGN: center
            }
            A.footer:link {
              COLOR: #336600; TEXT-DECORATION: none
            }
            A.footer:link {
              COLOR: #336600; TEXT-DECORATION: none
            }
            A.footer:visited {
              COLOR: #669933; TEXT-DECORATION: none
            }
            A.footer:active {
              COLOR: #99cc33; TEXT-DECORATION: none
            }
            A.footer:hover {
              COLOR: #ff0000; TEXT-DECORATION: underline
            }
            .divider {
              COLOR: #999999
            }
            .nospace {
            MARGIN-TOP: 0px; MARGIN-BOTTOM: 0px
            }
          </xsl:comment>
        </style>
      </head>
      <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
        <table width="100%" border="0" cellpadding="0" cellspacing="20">
          <tr valign="top">
            <td bgcolor="#669933">
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr valign="top">
                  <td width="50%"><a href="/oaicat/index.html"><img src="/oaicat/banner01.gif" alt="OAICat - OAI Repository Framework" width="388" height="120" border="0"/></a></td>
                  <td width="50%" align="right"><a href="http://www.oclc.org/research/"><img src="/oaicat/banner02.gif" alt="A Project of OCLC Research" width="202" height="120" border="0"/></a></td>
                </tr>
              </table>
            </td>
          </tr>
          <tr valign="top">
            <td><a href="/oaicat/identify.html">Identify</a><xsl:text disable-output-escaping="yes">&#160;</xsl:text><span class="divider">|</span><xsl:text disable-output-escaping="yes">&#160;</xsl:text><a href="/oaicat/getRecord.html">GetRecord</a><xsl:text disable-output-escaping="yes">&#160;</xsl:text><span class="divider">|</span><xsl:text disable-output-escaping="yes">&#160;</xsl:text><a href="/oaicat/listIdentifiers.html">ListIdentifiers</a><xsl:text disable-output-escaping="yes">&#160;</xsl:text>(<a href="/oaicat/listIdentifiersResumption.html">Resumption</a>)<xsl:text disable-output-escaping="yes">&#160;</xsl:text><span class="divider">|</span><xsl:text disable-output-escaping="yes">&#160;</xsl:text><a href="/oaicat/listMetadataFormats.html">ListMetadataFormats</a><xsl:text disable-output-escaping="yes">&#160;</xsl:text><span class="divider">|</span><xsl:text disable-output-escaping="yes">&#160;</xsl:text><a href="/oaicat/listRecords.html">ListRecords</a><xsl:text disable-output-escaping="yes">&#160;</xsl:text>(<a href="/oaicat/listRecordsResumption.html">Resumption</a>)<xsl:text disable-output-escaping="yes">&#160;</xsl:text><span class="divider">|</span><xsl:text disable-output-escaping="yes">&#160;</xsl:text><a href="/oaicat/listSets.html">ListSets</a></td>
          </tr>
          <tr valign="top">
            <td bgcolor="#cccccc">
              <table width="100%" border="0" cellpadding="4" cellspacing="0">
                <xsl:apply-templates select="oai:responseDate|oai:request"/>
              </table>
            </td>
          </tr>
          <tr valign="top">
            <td><xsl:apply-templates select="oai:Identify|oai:GetRecord|oai:ListIdentifiers|oai:ListMetadataFormats|oai:ListRecords|oai:ListSets|oai:error"/></td>
          </tr>
          <tr valign="top">
            <td bgcolor="#F9DD50" height="10"></td>
          </tr>
          <tr valign="top">
            <td><a href="http://www.oclc.org/research/software/oai/cat.shtm"><img border="0" src="http://alcme.oclc.org/oaicat/oaicat_icon.gif" alt="OAICat - An OAI-PMH v2 Repository Framework" width="120" height="60"/></a>&#160;&#160;&#160;&#160;<a href="http://www.openarchives.org"><img border="0" src="http://www.openarchives.org/images/OA100.gif"/></a></td>
          </tr>
          <tr valign="top">
            <td><address><a href="mailto:jyoung@oclc.org">Jeff Young</a></address></td>
          </tr>
        </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="oai:Identify|oai:GetRecord">
    <h2><xsl:value-of select="name()"/></h2>
    <table width="100%" border="0" cellspacing="2" cellpadding="0">
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="oai:ListMetadataFormats">
    <h2><xsl:value-of select="name()"/></h2>
      <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="oai:ListSets">
    <h2><xsl:value-of select="name()"/></h2>
    <table width="100%" border="0" cellspacing="0" cellpadding="4">
      <tr valign="top">
        <td width="150"><strong>setSpec</strong></td>
        <td><strong>setName</strong></td>
      </tr>
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="oai:ListRecords">
    <h2><xsl:value-of select="name()"/> (<xsl:value-of select="/oai:OAI-PMH/oai:request/@metadataPrefix"/>)</h2>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="oai:ListIdentifiers">
    <h2><xsl:value-of select="name()"/></h2>
    <table width="100%" border="0" cellspacing="2" cellpadding="0">
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="oai:error">
    <h2><font color="red"><xsl:value-of select="name()"/></font></h2>
    <table width="100%" border="0" cellspacing="2" cellpadding="0">
      <tr valign="top">
        <td width="200"><strong><xsl:value-of select="@code"/></strong></td>
        <td><xsl:value-of select="."/></td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="oai:record">
    <tr valign="top">
      <td>
        <table width="100%" border="0" cellspacing="2" cellpadding="0">
          <xsl:apply-templates/>
        </table>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="oai:header">
    <tr valign="top">
      <td bgcolor="#eeeeee">
        <table width="100%" border="0" cellspacing="4" cellpadding="0">
          <xsl:apply-templates/>
        </table>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="oai:metadata">
    <tr valign="top">
      <td>
        <xsl:apply-templates/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="oai:set">
    <tr valign="top">
      <xsl:apply-templates/>
    </tr>
  </xsl:template>

  <xsl:template match="oai:setSpec">
    <td width="150"><strong><a><xsl:attribute name="href">/oaicat/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=<xsl:value-of select="."/></xsl:attribute><xsl:value-of select="."/></a></strong></td>
  </xsl:template>

  <xsl:template match="oai:setName">
    <td><xsl:value-of select="."/></td>
  </xsl:template>

  <xsl:template match="oai:responseDate">
    <tr valign="top">
      <td width="150"><strong><xsl:value-of select="name()"/></strong></td>
      <td><xsl:value-of select="."/></td>
    </tr>
  </xsl:template>

  <xsl:template match="oai:request">
    <tr valign="top">
      <td width="150"><strong><xsl:value-of select="name()"/></strong></td>
      <td><xsl:value-of select="."/>?verb=<xsl:value-of select="@verb"/></td>
    </tr>
  </xsl:template>

  <xsl:template match="oai:*">
    <tr valign="top">
      <td width="150"><strong><xsl:value-of select="name()"/></strong></td>
      <td><xsl:value-of select="."/></td>
    </tr>
  </xsl:template>

  <xsl:template match="oai:adminEmail">
    <tr valign="top">
      <td width="150"><strong><xsl:value-of select="name()"/></strong></td>
      <td><cite><a><xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute><xsl:value-of select="."/></a></cite></td>
    </tr>
  </xsl:template>

  <xsl:template match="oai:resumptionToken">
    <tr valign="top">
      <td>ResumptionToken: <a><xsl:attribute name="href">/oaicat/OAIHandler?verb=<xsl:value-of select="//oai:OAI-PMH/oai:request/@verb"/>&amp;resumptionToken=<xsl:value-of select="."/></xsl:attribute><xsl:value-of select="."/></a></td>
    </tr>
  </xsl:template>

  <xsl:template match="oai:identifier">
    <tr valign="top">
      <td><strong><xsl:value-of select="name()"/></strong></td>
      <td><a><xsl:attribute name="href">/oaicat/OAIHandler?verb=GetRecord&amp;metadataPrefix=<xsl:choose><xsl:when test="/oai:OAI-PMH/oai:request/@metadataPrefix"><xsl:value-of select="/oai:OAI-PMH/oai:request/@metadataPrefix"/></xsl:when><xsl:otherwise>oai_dc</xsl:otherwise></xsl:choose>&amp;identifier=<xsl:value-of select="."/></xsl:attribute><xsl:value-of select="."/></a></td>
    </tr>
  </xsl:template>

<!--
  <xsl:template name="apply-templates-copy-all">
    <xsl:copy>
      <xsl:call-template name="apply-templates-copy-all"/>
    </xsl:copy>
  </xsl:template>
-->

  <xsl:template match="oai:description">
    <tr valign="top">
      <td><strong><xsl:value-of select="name()"/></strong></td>
      <td><xsl:apply-templates/></td>
    </tr>
  </xsl:template>

<!--
  <xsl:template match="oai_id:oai-identifier">
    <table border="0">
      <tr valign="top"><td>OAI Identifier</td></tr>
      <xsl:apply-templates/>
    </table>
  </xsl:template>
-->

  <xsl:template match="oai_id:oai-identifier">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top">
      <td><strong><xsl:value-of select="name()"/>:</strong></td>
    </tr>
    <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="oai_id:*">
    <tr valign="top">
      <td><strong><xsl:value-of select="name()"/></strong></td>
      <td><xsl:apply-templates/></td>
    </tr>
  </xsl:template>

  <xsl:template match="oai_branding:branding">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="oai_branding:metadataRendering">
    <tr valign="top">
      <td><strong><xsl:value-of select="name()"/></strong></td>
      <td>
        <a>
          <xsl:attribute name="href">
            <xsl:value-of select="."/>
          </xsl:attribute>
          <xsl:attribute name="type">
            <xsl:value-of select="@mimeType"/>
          </xsl:attribute>
          <xsl:value-of select="@metadataNamespace"/>
        </a>
      </td>
    </tr>
  </xsl:template>

<!--
  <xsl:template match="oai_branding:metadataRendering>
    <tr valign="top">
      <td><strong><xsl:value-of select="name()"/></strong></td>
    </tr>
  </xsl:template>
-->

  <xsl:template match="oai_branding:collectionIcon">
    <tr valign="top">
      <td><strong><xsl:value-of select="name()"/></strong></td>
      <td>
        <!--
        <a href="/oaicat/index.html"><img src="/oaicat/oaicat_icon.gif" alt="Testing"/></a>
        -->
        <a>
          <xsl:attribute name="href">
            <xsl:value-of select="oai_branding:link"/>
          </xsl:attribute>
          <img>
            <xsl:attribute name="width">
              <xsl:value-of select="oai_branding:width"/>
            </xsl:attribute>
            <xsl:attribute name="height">
              <xsl:value-of select="oai_branding:height"/>
            </xsl:attribute>
            <xsl:attribute name="src">
              <xsl:value-of select="oai_branding:url"/>
            </xsl:attribute>
            <xsl:attribute name="alt">
              <xsl:value-of select="oai_branding:title"/>
            </xsl:attribute>
          </img>
        </a>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="oai_branding:*">
    <tr valign="top">
      <td><strong><xsl:value-of select="name()"/></strong></td>
      <td><xsl:apply-templates/></td>
    </tr>
  </xsl:template>

  <xsl:template match="oai:metadataFormat">
    <table width="100%" border="0" cellspacing="0" cellpadding="4">
      <tr valign="top"><td width="150"><strong>metadataPrefix</strong></td><td><a><xsl:attribute name="href">/oaicat/OAIHandler?verb=ListRecords&amp;metadataPrefix=<xsl:value-of select="oai:metadataPrefix"/></xsl:attribute><xsl:value-of select="oai:metadataPrefix"/></a></td></tr>
        <tr valign="top"><td width="150"><strong>schema</strong></td><td><a><xsl:attribute name="href"><xsl:value-of select="oai:schema"/></xsl:attribute><xsl:value-of select="oai:schema"/></a></td></tr>
        <tr valign="top"><td width="150"><strong>metadataNamespace</strong></td><td><a><xsl:attribute name="href"><xsl:value-of select="oai:metadataNamespace"/></xsl:attribute><xsl:value-of select="oai:metadataNamespace"/></a></td></tr>
    </table>
    <hr/>
  </xsl:template>

  <xsl:template match="oai_dc:dc">
        <table width="100%" border="0" cellspacing="4" cellpadding="0">
          <xsl:apply-templates/>
        </table>
  </xsl:template>

  <xsl:template match="oai_etdms:thesis">
        <table width="100%" border="0" cellspacing="4" cellpadding="0">
          <xsl:apply-templates/>
        </table>
  </xsl:template>

  <xsl:template match="dc:identifier">
    <tr valign="top">
      <td><strong><xsl:value-of select="name()"/></strong></td>
      <td><a><xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute><xsl:value-of select="."/></a></td>
    </tr>
  </xsl:template>

  <xsl:template match="dc:*">
    <tr valign="top">
      <td width="150"><strong><xsl:value-of select="name()"/></strong></td>
      <td><xsl:value-of select="."/></td>
    </tr>
  </xsl:template>

  <xsl:template match="oai_etdms:*">
    <tr valign="top">
      <td width="150"><strong><xsl:value-of select="name()"/></strong></td>
      <xsl:if test="@resource">
        <td><a><xsl:attribute name="href"><xsl:value-of select="@resource"/></xsl:attribute><xsl:value-of select="."/></a></td>
      </xsl:if>
      <xsl:if test="not(@resource)">
        <td><xsl:value-of select="."/></td>
      </xsl:if>
    </tr>
  </xsl:template>

  <xsl:template match="toolkit:toolkit">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr valign="top"><td width="150"><strong><xsl:value-of select="name()"/></strong></td>
        <td>
    <a>
      <xsl:attribute name="href">
        <xsl:value-of select="toolkit:URL"/>
      </xsl:attribute>
      <img border="0" cellspacing="0" cellpadding="0">
        <xsl:attribute name="alt">
          <xsl:value-of select="toolkit:title"/>
        </xsl:attribute>
        <xsl:attribute name="src">
          <xsl:value-of select="toolkit:toolkitIcon"/>
        </xsl:attribute>
      </img>
    </a>
    (version <xsl:value-of select="toolkit:version"/>)
        </td>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>