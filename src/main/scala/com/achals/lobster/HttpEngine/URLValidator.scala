package com.achals.lobster.HttpEngine

import java.net.URL

import org.apache.commons.validator.routines.UrlValidator


/**
 * Created by achalshah on 9/14/14.
 */
object URLValidator {
  val validator:UrlValidator = new UrlValidator()

  def validate( url:String ) :Option[URL] =  {
    if (this.validator.isValid(url)) Option.apply(new URL(url))
    else Option.empty
  }
}
