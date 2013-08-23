package com.achals.lobster.HttpEngine

object EngineMain {
  def main(args: Array[String]) = {
    val engine = new HttpEngine()
    val contents = engine.GET("http://www.google.com:80")
    contents match {
      case Some(ans) => println(ans.Headers)
      case None      => println("Error in performing action.")
    }
  }
}