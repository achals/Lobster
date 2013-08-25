package com.achals.lobster.HttpEngine

object EngineMain {
  def main(args: Array[String]) = {
    val engine = new HttpEngine()
    val contents = engine.GET("http://gawker.com/")
    contents match {
      case Some(ans) => println(new String(ans.Body.toArray, "UTF-8"))
      case None      => println("Error in performing action.")
    }
  }
}