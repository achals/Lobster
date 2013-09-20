package com.achals.Lobster.api

import java.net.URL

trait Parser {
	def getHREFs(html:String) : List[URL] 
}