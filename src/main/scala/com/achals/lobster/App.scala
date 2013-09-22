package com.achals.Lobster

import java.net.URL
import api.Parser;
import HttpEngine.{Contents,JSoupParser, Engine}

/**
 * @author ${user.name}
 */
object App {
    
  def main(args : Array[String]) {
    println( "Hello World!" )
    val engine = Engine()
    val contents = engine.GET("http://www.yahoo.com/").getOrElse(Contents(null, null, null))
    JSoupParser.getHREFs(new URL(contents.URL), contents.Body.toString).map(println)
  }

}
