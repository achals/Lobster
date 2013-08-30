package com.achals.lobster.HttpEngine

object EngineMain {
  def main(args: Array[String]) = {
    val engine = new HttpEngine()
        
    val contents = engine.GET("http://en.wikipedia.org/wiki/Main_Page/")
    contents match {
      case Some(ans) => println((ans.Body.toString));
      case None      => println("Error in performing action.")
    }
  }
}