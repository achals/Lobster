package com.achals.Lobster.HttpEngine

import org.jsoup.Jsoup
import java.net.URL
import com.achals.Lobster.api.Parser
import scala.collection.mutable

object JSoupParser extends Parser {
  def getHREFs(html:String) : List[URL] = {
    val document = Jsoup.parse(html)
    hrefsFromParse(document).map(elem => new URL(elem))

  }
  
  def hrefsFromParse(document: org.jsoup.nodes.Document) : List[String] = {
    val hrefs = mutable.ListBuffer.empty[String]
    val elements = document.getElementsByTag("a")
    var a=0;
    for (a <- 0 to elements.size-1) {
      if (isValid(elements.get(a).attr("href")))
    	  hrefs.append(elements.get(a).attr("href"))
      else ()
    }
    hrefs.toList
  }
  
  def isValid(href:String) = {
    false
  }
  
  def canonize(currentPage:URL, href:String) :String = {
    href
  } 
}