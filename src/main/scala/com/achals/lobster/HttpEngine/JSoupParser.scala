package com.achals.Lobster.HttpEngine

import org.jsoup.Jsoup
import java.net.URL
import com.achals.Lobster.api.Parser

object JSoupParser extends Parser {
  def getHREFs(html:String) : List[URL] = {
    val document = Jsoup.parse(html)
    val elements = document.getElementsByTag("a")
    println(elements.first())
    List() : List[URL]
  }
}