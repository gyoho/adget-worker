package com.gyoho.adget.utils

import java.net.URL

import com.twitter.finagle.{Http, Service, http}
import com.twitter.util.{Future => TwitterFuture}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise, ExecutionContext}

// use URL instead of String b/c I don't want to deal with edge cases handling
trait HttpClient[T] {
  def httpGet(url: URL, timeoutInMillis: Long)(implicit ec: ExecutionContext): T
  def httpPost(url: URL, payload: String, timeoutInMillis: Long)(implicit ec: ExecutionContext): T
}

object ScalaHttpClient extends HttpClient[String] {
  @throws(classOf[java.io.IOException])
  override def httpGet(url: URL, timeoutInMillis: Long)(implicit ec: ExecutionContext) : String = {
    val responseFuture = Future(scala.io.Source.fromURL(url).mkString)
    Await.result(responseFuture, timeoutInMillis.millisecond)
  }
  override def httpPost(url: URL, payload: String, timeoutInMillis: Long)(implicit ec: ExecutionContext): String = ???
}

object FinagleHttpClient extends HttpClient[http.Response] {
  private def twitterFutureToScala[T](twitterFuture: TwitterFuture[T]): Future[T] = {
    val scalaFuture = Promise[T]()
    twitterFuture.onSuccess(v => scalaFuture.success(v))
    twitterFuture.onFailure(t => scalaFuture.failure(t))
    scalaFuture.future
  }

  override def httpGet(url: URL, timeoutInMillis: Long)(implicit ec: ExecutionContext): http.Response = {
    val client: Service[http.Request, http.Response] = Http.newService(url.toString)
    val request = http.Request(http.Method.Get, "/")
    val responseFuture = twitterFutureToScala(client(request))
    Await.result(responseFuture, timeoutInMillis.millisecond)
  }

  override def httpPost(url: URL, payload: String, timeoutInMillis: Long)(implicit ec: ExecutionContext): http.Response = ???
}
