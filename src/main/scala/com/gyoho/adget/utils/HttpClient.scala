package com.gyoho.adget.utils

import java.net.URL

// use URL instead of String b/c I don't want to deal with edge cases handling
trait HttpClient[T] {
  def httpGet(url: URL): T
  def httpPost(url: URL, payload: String): T
}

// use object or class?
// should each service have its own http client?
object ScalaHttpClient extends HttpClient[String] {
  @throws(classOf[java.io.IOException])
  override def httpGet(url: URL): String = scala.io.Source.fromURL(url).mkString
  override def httpPost(url: URL, payload: String): String = ???
}
