package com.gyoho.adget

import java.net.URL

import com.gyoho.adget.utils.ScalaHttpClient
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
  val TIMEOUT_IN_MILLIS = 5000

  def sendRequest(timeoutInMillis: Long): Try[String] = {
    val responseFuture: Future[Retry[String]] = Future {
      Retry(ScalaHttpClient.httpGet(new URL(url)))
    }

    Try(Await.result(responseFuture, timeoutInMillis.millisecond)) match {
      case Success(res) => res match {
        case RetrySuccess(data) => Success(data)
        case RetryFailure(ex) => Failure(ex)
      }
      case Failure(ex) => Failure(ex)
    }
  }

  protected def parse(body: String): Seq[Ad]

  def getAds(timeoutInMillis: Long = TIMEOUT_IN_MILLIS): Seq[Ad] = {
    logger.info(s"timeout = $timeoutInMillis milliseconds")
    val currentTime = System.currentTimeMillis()

    val response = sendRequest(timeoutInMillis)

    logger.info(s"took ${System.currentTimeMillis() - currentTime} milliseconds")

    val resToparse = response match {
      case Success(res) => res
      case Failure(ex) =>
        logger.info(s"Failed to get ads from advertiser=$name. Request timeout. ex=${ex.getMessage}")
        ""
    }

    parse(resToparse)
  }
}

class IronSource(val id: String, val name: String, val url: String)(
    implicit val rs: RetryStrategy,
    val ec: ExecutionContext)
    extends Advertiser {
  override protected def parse(body: String): Seq[Ad] = ???
}
