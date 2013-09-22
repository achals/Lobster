package com.achals.Lobster.api

import java.net.URL

trait Parser {
	def getHREFs(currentPage:URL, html:String) : List[URL] 
}