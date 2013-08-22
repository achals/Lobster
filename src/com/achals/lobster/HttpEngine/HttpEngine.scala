package com.achals.lobster.HttpEngine

import java.net.{URL, Socket}
import java.io.{BufferedReader, InputStreamReader, InputStream};
import scala.annotation.tailrec

class HttpEngine {

  def HEAD (url: String):Option[Contents] = {
    val urlObj = new URL(url)
    val sock = new Socket(urlObj.getHost(), urlObj.getPort())
    
    val outStream = sock.getOutputStream()
    val inStream  = sock.getInputStream()
    val inBufReader = new BufferedReader(new InputStreamReader(inStream));
    
    val file = urlObj.getFile()
    val host = urlObj.getHost()
    
    outStream.write(("HEAD /"+file+" HTTP/1.1\r\n").getBytes());
    outStream.write(("User-Agent: Lobstercrawler\r\n").getBytes());
    outStream.write(("Connection: close\r\n").getBytes());
    outStream.write(("Host: "+host+"\r\n\r\n").getBytes());
    outStream.flush();         

    parseHeaders(url, inBufReader)
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