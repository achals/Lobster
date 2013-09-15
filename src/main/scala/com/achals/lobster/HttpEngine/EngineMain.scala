package com.achals.lobster.HttpEngine

object EngineMain {
  def main(args: Array[String]) = {
    val engine = new HttpEngine()
        
    engine.simplyRead("http://www.yahoo.com/")
    
   /* val contents = engine.GET("http://www.google.com/")
    contents match {
      case Some(ans) => println((ans.Body.toString));
      case None      => println("Error in performing action.")
    }*/
  }
}