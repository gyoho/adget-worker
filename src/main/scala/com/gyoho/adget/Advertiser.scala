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

  def sendRequest(timeoutInMillis: Long): String = {
    logger.info(s"timeout = $timeoutInMillis milliseconds")
    val currentTime = System.currentTimeMillis()

    val responseFuture = Future {
      Retry(ScalaHttpClient.httpGet(new URL(url))) match {
        case RetrySuccess(res) => res
        case RetryFailure(ex) =>
          logger.info(
            s"Failed to get ads from advertiser=$name. Max retry exceeds. ex=${ex.getMessage}")
          ""
      }
    }

    Try(Await.result(responseFuture, timeoutInMillis.millisecond)) match {
      case Success(res) => res
      case Failure(ex) =>
        logger.info(s"Failed to get ads from advertiser=$name. Request timeout. ex=${ex.getMessage}")
        logger.info(s"took ${System.currentTimeMillis() - currentTime} milliseconds")
        ""
    }
  }

  protected def parse(body: String): Seq[Ad]

  def getAds(timeoutInMillis: Long = TIMEOUT_IN_MILLIS): Seq[Ad] = {
    val response = sendRequest(timeoutInMillis)
    parse(response)
  }
}

class IronSource(val id: String, val name: String, val url: String)(
    implicit val rs: RetryStrategy,
    val ec: ExecutionContext)
    extends Advertiser {
  override protected def parse(body: String): Seq[Ad] = ???
}
