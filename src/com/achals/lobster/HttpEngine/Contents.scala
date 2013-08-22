package com.achals.lobster.HttpEngine

class Contents(val URL:String, val Headers:Map[String, String], val Body:String) {
}

object Contents {
  def apply(URL:String, Headers:Map[String, String], Body:String):Contents = {
    new Contents(URL, Headers, Body)
  }
}