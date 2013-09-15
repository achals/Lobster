package com.achals.lobster.HttpEngine

import scala.collection.mutable;
class Contents(val URL:String, val Headers:Map[String, String], val Body:mutable.StringBuilder) {
}

object Contents {
  def apply(URL:String, Headers:Map[String, String], Body:mutable.StringBuilder):Contents = {
    new Contents(URL, Headers, Body)
  }
}