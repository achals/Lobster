package com.achals.Lobster.HttpEngine

import org.jsoup.Jsoup
import java.net.URL
import com.achals.Lobster.api.Parser
import scala.collection.mutable

object JSoupParser extends Parser {
  def getHREFs(currentPage:URL, html:String) : List[URL] = {
    val document = Jsoup.parse(html)
    hrefsFromParse(currentPage, document).map(elem => new URL(elem))

  }
  
  def hrefsFromParse(currentPage:URL, document: org.jsoup.nodes.Document) : List[String] = {
    val hrefs = mutable.ListBuffer.empty[String]
    val elements = document.getElementsByTag("a")
    var a=0;
    for (a <- 0 to elements.size-1) {
    	  hrefs.append(elements.get(a).attr("href"))
    }
    hrefs.toList.map(canonize(currentPage, _)) flatten
  }
    
  def canonize(currentPage:URL, href:String) :Option[String] = {
            
  		if (href.startsWith("http://") || href.startsWith("https://")){
            Some (href);
        } else if (href.startsWith("/") || href.startsWith(".")){
        	val builder:StringBuilder = new StringBuilder();
            val base = getBaseURL(currentPage.toExternalForm());
            builder.append(currentPage.getProtocol());
            builder.append("://");
            builder.append(currentPage.getHost());
            builder.append(href);
            Some (builder.toString)
        } else {
        	None
        }

  } 
  
  def getBaseURL(currentPage: String) ={
    
  }
}