package com.achals.Lobster

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
    JSoupParser.getHREFs(contents.Body.toString).map(println)
  }

}
