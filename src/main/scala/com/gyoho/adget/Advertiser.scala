package com.gyoho.adget

import java.net.URL

import com.gyoho.adget.utils.{HttpClient, ScalaHttpClient}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import util.retry.blocking.{Retry, RetryStrategy, Failure => RetryFailure, Success => RetrySuccess}

trait Advertiser extends LazyLogging {
  def id: String
  def url: String
  def name: String
  implicit def rs: RetryStrategy
  implicit def ec: ExecutionContext

  val TIMEOUT_IN_MILLIS = 200

  def sendRequest[T](client: HttpClient[T], timeoutInMillis: Long): Try[String] = {
    val responseFuture = Future {
      Retry(client.httpGet(new URL(url), timeoutInMillis / 2))
    }

    Try(Await.result(responseFuture, timeoutInMillis.millisecond)) match {
      case Success(res) => res match {
        case RetrySuccess(data) => Success(data.toString)
        case RetryFailure(ex) => Failure(ex)
      }
      case Failure(ex) => Failure(ex)
    }
  }

  protected def parse(body: String): Seq[Ad]

  def getAds[T](client: HttpClient[T], timeoutInMillis: Long = TIMEOUT_IN_MILLIS): Seq[Ad] = {
    logger.info(s"timeout = $timeoutInMillis milliseconds")
    val currentTime = System.currentTimeMillis()

    val response = sendRequest(client, timeoutInMillis)

    logger.info(s"took ${System.currentTimeMillis() - currentTime} milliseconds")

    val resToParse = response match {
      case Success(res) => res
      case Failure(ex) =>
        logger.info(s"Failed to get ads from advertiser=$name. Request timeout. ex=${ex.getMessage}")
        ""
    }

    parse(resToParse)
  }
}

class IronSource(val id: String, val name: String, val url: String)(
    implicit val rs: RetryStrategy,
    val ec: ExecutionContext)
    extends Advertiser {
  override protected def parse(body: String): Seq[Ad] = ???
}
