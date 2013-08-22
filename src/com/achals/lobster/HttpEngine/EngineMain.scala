package com.achals.lobster.HttpEngine

object EngineMain {
  def main(args: Array[String]) = {
    val engine = new HttpEngine()
    val contents = engine.HEAD("http://www.google.com:80")
    println(contents.get.Body)
    ()
  }
}