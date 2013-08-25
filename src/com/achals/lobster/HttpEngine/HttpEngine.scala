package com.achals.lobster.HttpEngine

import java.net.{URL, Socket}
import java.io.{BufferedReader, InputStreamReader, InputStream, OutputStream}
import java.lang.Integer
import scala.annotation.tailrec
import scala.collection.mutable

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
    val port = if (urlObj.getPort() == -1) urlObj.getDefaultPort() else urlObj.getPort()
    val sock = new Socket(urlObj.getHost(), port)
    
    val outStream = sock.getOutputStream()
    val inStream  = sock.getInputStream()
    val inBufReader = new BufferedReader(new InputStreamReader(inStream));
  
    makeRequest(outStream, "GET", urlObj)
    val headers = parseHeaders(url, inBufReader)
    val output = headers match {
      case Some(content) => parseBody(inBufReader, content)
      case None 		 => None
    }
    println(headers.get.Headers)
    output
  }
  
  private def makeRequest(outStream: OutputStream, command: String, urlObj:URL):Unit = {
    val file = urlObj.getFile()
    val host = urlObj.getHost()

    outStream.write((command+" "+file+" HTTP/1.1\r\n").getBytes());
    outStream.write(("User-Agent: Lobstercrawler\r\n").getBytes());
    outStream.write(("Connection: close\r\n").getBytes());
    outStream.write(("Host: "+host+"\r\n\r\n").getBytes());
    outStream.flush();
  }
  
  private def parseHeaders(url:String, inStream: BufferedReader): Option[Contents] = {
    var line = ""
    var content = ""
    val StatusLine = inStream.readLine()
    println(StatusLine)
    if (StatusLine.split(" ")(1)=="200") {
		  val initList:mutable.ListBuffer[Byte] = mutable.ListBuffer()
		  initList.insertAll(0, StatusLine.getBytes())
		  Some(parseLine(inStream, Contents(url, Map[String, String](), initList)))
		}
    else
      None
        
  }
  
  private def readNBytes(inBufReader: BufferedReader, n:Int) = {
    val contents:mutable.ListBuffer[Byte] = mutable.ListBuffer()
    
    @tailrec
    def readInner(inBufReader: BufferedReader, n: Int, contents: mutable.ListBuffer[Byte]):mutable.ListBuffer[Byte] = {
      if (n < 1)
        contents
      else {
        println(n + " " + contents.length)
        readInner(inBufReader, n-1, contents :+ inBufReader.read().toByte)
      }
    }
    
    readInner(inBufReader, n, contents)
  }
  
  private def parseBody(inBufReader: BufferedReader, tempContents: Contents): Option[Contents] = {
		  if (tempContents.Headers.get("Transfer-Encoding") == Some("Chunked"))
		    Some(getChunkedResponse(inBufReader, tempContents))
		  else if (tempContents.Headers.contains("Content-Length"))
			Some(getWholeResponse(inBufReader, tempContents))
	      else
	        None
  }
  
  private def getWholeResponse(inBufReader: BufferedReader, tempContents:Contents) : Contents = {
    val length = Integer.parseInt(tempContents.Headers.getOrElse("Content-Length", "0").trim)
    println(length)
    Contents(tempContents.URL,
             tempContents.Headers,
             readNBytes(inBufReader, length))
  }
  
  def getChunkedResponse(inBufReader:BufferedReader, tempContents:Contents): Contents = {
    val line = inBufReader.readLine()
    if (line==null || line == "0")
      tempContents
    else {
      val size = Integer.parseInt(line, 16)
      val bodyLine = readNBytes(inBufReader, size)
      inBufReader.read();
      inBufReader.read();
      
      getChunkedResponse(inBufReader, Contents(tempContents.URL,
    		  								   tempContents.Headers,
    		  								   tempContents.Body ++ bodyLine))
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
    		  					   tempContent.Body ++ line.getBytes()))
  }
}