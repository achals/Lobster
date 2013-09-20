package com.achals.Lobster.HttpEngine

import scala.collection.mutable;
class Contents(val URL:String, 
			   val Headers:Map[String, String], 
			   val Body:mutable.StringBuilder,
			   val HTML:mutable.StringBuilder) {
}

object Contents {
  
  def apply(URL:String, Headers:Map[String, String], Body:mutable.StringBuilder) : Contents = {
    new Contents(URL, Headers, Body, new mutable.StringBuilder)
  }
  
  def apply(URL:String, Headers:Map[String, String], Body:mutable.StringBuilder, WholeResponse:mutable.StringBuilder):Contents = {
    new Contents(URL, Headers, Body, WholeResponse)
  }
}