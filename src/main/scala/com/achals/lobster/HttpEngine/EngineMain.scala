package com.achals.Lobster.HttpEngine

object EngineMain {
  def main(args: Array[String]) = {
    val engine = Engine()
        
    //engine.simplyRead("http://www.yahoo.com/")
    
    val contents = engine.GET("http://www.yahoo.com/")
    contents match {
      case Some(ans) => println((ans.Body.toString));
      case None      => println("Error in performing action.")
    }
  }
}