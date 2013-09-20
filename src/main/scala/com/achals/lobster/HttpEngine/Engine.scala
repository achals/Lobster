package com.achals.Lobster.HttpEngine

import java.net.{URL, Socket}
import java.io.{BufferedReader, InputStreamReader, InputStream, OutputStream}
import java.nio.{ByteBuffer}
import java.lang.Integer
import scala.annotation.tailrec
import scala.collection.mutable

object Engine {
  def apply() : Engine ={
    new Engine()
  }
}

class Engine {

  def HEAD (url: String):Option[Contents] = {
    val urlObj = new URL(url)
    val port = if (urlObj.getPort() == -1) urlObj.getDefaultPort() else urlObj.getPort()
    val sock = new Socket(urlObj.getHost(), port)
    
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
//    println((headers.get.Body.toArray))
    
    val output = headers match {
      case Some(content) => parseBody(inBufReader, content)
      case None 		 => None
    }
    output
  }
  
  private def makeRequest(outStream: OutputStream, command: String, urlObj:URL):Unit = {
    val file = urlObj.getFile()
    val host = urlObj.getHost()

    outStream.write((command+" "+file+" HTTP/1.1\r\n").getBytes());
    outStream.write(("User-Agent: Lobstercrawler\r\n").getBytes());
    outStream.write(("Accept-Encoding: identity\r\n").getBytes());
    outStream.write(("Connection: close\r\n").getBytes());
    outStream.write(("Host: "+host+"\r\n\r\n").getBytes());
    outStream.flush();
  }
  
  private def parseHeaders(url:String, inStream: BufferedReader): Option[Contents] = {
    var line = ""
    var content = ""
    val StatusLine = inStream.readLine()
    if (StatusLine.split(" ")(1)=="200") {
		  val initList:mutable.StringBuilder = new mutable.StringBuilder(StatusLine)
		  //initList.map((x:Char) => println (x + " " + x.toInt) )
		  Some(parseLines(inStream, Contents(url, Map[String, String](), initList)))
		}
    else
      None
        
  }
  
  private def readNBytes(inBufReader: BufferedReader, n:Int) = {
    val contents:mutable.StringBuilder = new mutable.StringBuilder()
        
    @tailrec
    def readInner(inBufReader: BufferedReader, n: Int, buffer: mutable.StringBuilder):mutable.StringBuilder = {
      if (n < 1)
        contents
      else {
        //if (n%500 == 0) println(n + " " + contents.length) else ()
        val readInt = inBufReader.read()
        contents+=readInt.toChar
        readInner(inBufReader, n-1, contents)
      }
    }
    
    readInner(inBufReader, n, contents).toString
  }
  
  private def parseBody(inBufReader: BufferedReader, tempContents: Contents): Option[Contents] = {
	  val contents = Contents(tempContents.URL, tempContents.Headers, tempContents.Body ++= "\r\n", new mutable.StringBuilder)
		  if (tempContents.Headers.get("Transfer-Encoding") == Some("Chunked"))
		    Some(getChunkedResponse(inBufReader, contents))
		  else if (tempContents.Headers.contains("Content-Length"))
			Some(getWholeResponse(inBufReader, contents))
		  else if (tempContents.Headers.contains("Connection") && tempContents.Headers.get("Connection")==(Some("close")))
			Some(getTillEnd(inBufReader, contents))
	      else {
	        println("Not parseable.")
	        None
	      }
  }
  
  private def getWholeResponse(inBufReader: BufferedReader, tempContents:Contents) : Contents = {
    val length = Integer.parseInt(tempContents.Headers.getOrElse("Content-Length", "0").trim)
    val NBytes = readNBytes(inBufReader, length).toString()
    Contents(tempContents.URL,
             tempContents.Headers,
             tempContents.Body ++= NBytes,
             tempContents.HTML ++= NBytes)
  }
  
  @tailrec
  private def getTillEnd(inBufReader: BufferedReader, tempContents:Contents) : Contents  = {
	val line = inBufReader.readLine()

    if (line == null) {
      tempContents
    }
    else getTillEnd(inBufReader, Contents(tempContents.URL,
    									  tempContents.Headers,
    									  tempContents.Body ++= line,
    									  tempContents.HTML ++= line)) 
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
    		  								   tempContents.Body ++= bodyLine.toString(),
    		  								   tempContents.HTML ++= bodyLine.toString()))
    }
  }
  
  @tailrec
  private def parseLines(inStream: BufferedReader, tempContent:Contents):Contents ={
    val line = inStream.readLine()
    if (line==null || line=="")
      tempContent
    else     
      parseLines(inStream, Contents(tempContent.URL, 
    		  					   tempContent.Headers+(line.split(":", 2)(0) -> line.split(":", 2)(1).trim()),
    		  					   tempContent.Body ++= "\r\n" ++= line,
    		  					   tempContent.HTML ++= "\r\n" ++= line))
  }
  
  def simplyRead(url: String) ={
    val urlObj = new URL(url)
    val port = if (urlObj.getPort() == -1) urlObj.getDefaultPort() else urlObj.getPort()
    val sock = new Socket(urlObj.getHost(), port)
    
    val outStream = sock.getOutputStream()
    val inStream  = sock.getInputStream()
    val inBufReader = new BufferedReader(new InputStreamReader(inStream));
  
    makeRequest(outStream, "GET", urlObj)

    var line = inBufReader.readLine()
    
    while(line!=null) {
      println(line)
      line = inBufReader.readLine()
    }
  }
}