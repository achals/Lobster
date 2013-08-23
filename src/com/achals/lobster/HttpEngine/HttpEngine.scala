package com.achals.lobster.HttpEngine

import java.net.{URL, Socket}
import java.io.{BufferedReader, InputStreamReader, InputStream, OutputStream};
import java.lang.Integer;
import scala.annotation.tailrec

class HttpEngine {

  def HEAD (url: String):Option[Contents] = {
    val urlObj = new URL(url)
    val sock = new Socket(urlObj.getHost(), urlObj.getPort())
    
    val outStream = sock.getOutputStream()
    val inStream  = sock.getInputStream()
    val inBufReader = new BufferedReader(new InputStreamReader(inStream));
    makeRequest(outStream, "HEAD", urlObj)
    parseHeaders(url, inBufReader)
  }
  
  def GET(url:String) : Option[Contents] = {
    val urlObj = new URL(url)
    val sock = new Socket(urlObj.getHost(), urlObj.getPort())
    
    val outStream = sock.getOutputStream()
    val inStream  = sock.getInputStream()
    val inBufReader = new BufferedReader(new InputStreamReader(inStream));
  
    makeRequest(outStream, "GET", urlObj)
    val headers = parseHeaders(url, inBufReader)
    println(headers.get.Headers)
    println(headers.get.Body)
    val output = headers match {
      case Some(content) => parseBody(inBufReader, content)
      case None 		 => None
    }
    output
  }
  
  private def makeRequest(outStream: OutputStream, command: String, urlObj:URL):Unit = {
    val file = urlObj.getFile()
    val host = urlObj.getHost()

    outStream.write((command+" /"+file+" HTTP/1.1\r\n").getBytes());
    outStream.write(("User-Agent: Lobstercrawler\r\n").getBytes());
    outStream.write(("Connection: close\r\n").getBytes());
    outStream.write(("Host: "+host+"\r\n\r\n").getBytes());
    outStream.flush();
  }
  
  private def parseHeaders(url:String, inStream: BufferedReader): Option[Contents] = {
    var line = ""
    var content = ""
    val StatusLine = inStream.readLine()
    if (StatusLine.split(" ")(1)=="200")
      Some(parseLine(inStream, Contents(url, Map[String, String](), StatusLine)))
    else
      None
        
  }
  
  private def parseBody(inBufReader: BufferedReader, tempContents: Contents): Option[Contents] = {
		  tempContents.Headers.get("Transfer-Encoding") match {
		    case Some("Chunked") => Some(getChunkedResponse(inBufReader, tempContents))
		    case None 				 => None
		  }
		  None
  }
  
  def getChunkedResponse(inBufReader:BufferedReader, tempContents:Contents): Contents = {
    val line = inBufReader.readLine()
    if (line==null || line == "0")
      tempContents
    else {
      val size = Integer.parseInt(line, 16)
      var bodyLine = ""
      for (i <- 0 to size-1) {
        bodyLine = line+inBufReader.read()
      }
      inBufReader.read();
      inBufReader.read();
      
      getChunkedResponse(inBufReader, Contents(tempContents.URL,
    		  								   tempContents.Headers,
    		  								   tempContents.Body + bodyLine))
    }
  }
  
  @tailrec
  private def parseLine(inStream: BufferedReader, tempContent:Contents):Contents ={
    val line = inStream.readLine()
    if (line==null || line=="")
      tempContent
    else     
      parseLine(inStream, Contents(tempContent.URL, 
    		  					   tempContent.Headers+(line.split(":", 2)(0) -> line.split(":", 2)(1)),
    		  					   tempContent.Body+"\r\n"+line))
  }
}