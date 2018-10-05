package com.gyoho.adget

import java.net.URL
import java.util.UUID
import java.util.concurrent.{ExecutorService, Executors, TimeoutException}

import com.gyoho.adget.utils.{RandomGenericCreators, ScalaHttpClient}
import org.scalatest.{Matchers, WordSpec}
import util.retry.blocking.RetryStrategy

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class AdvertiserTest extends WordSpec with Matchers {
  import TestData._

  "The advertiser" should {
    "return empty string after max number of retry" in {
      val res: Try[String] =
        fakeAdvertiser1.sendRequest(ScalaHttpClient, timeoutInMillis = 10000)

      res match {
        case Failure(_: TimeoutException) => fail("request failed with different reason")
        case Failure(_) => // expected to be this case
        case Success(_) => fail("this request is expected to fail")
      }
    }
  }

  "The advertiser" should {
    "return empty string after timeout" in {
      val res: Try[String] = fakeAdvertiser1.sendRequest(ScalaHttpClient, timeoutInMillis = 10)

      res match {
        case Failure(_: TimeoutException) => // expected to be this case
        case Failure(_) => fail("request failed with different reason")
        case Success(_) => fail("this request is expected to fail")
      }
    }
  }

  val resBody: String =
    """
    |{
    | "results": [
    |   {"adId": "aaa", "url": "www.rrr.com", "price": 2.5, "category": "casino"},
    |   {"adId": "bbb", "url": "www.sss.com", "price": 1.5, "category": "casino"},
    |   {"adId": "ccc", "url": "www.ttt.com", "price": 3.0, "category": "game"},
    |   {"adId": "ddd", "url": "www.uuu.com", "price": 2.0, "category": "game"},
    |   {"adId": "eee", "url": "www.vvv.com", "price": 3.0, "category": "game"},
    |   {"adId": "fff", "url": "www.www.com", "price": 2.0, "category": "sport"},
    |   {"adId": "ggg", "url": "www.xxx.com", "price": 1.0, "category": "sport"},
    |   {"adId": "hhh", "url": "www.yyy.com", "price": 1.0, "category": "game"},
    |   {"adId": "iii", "url": "www.zzz.com", "price": 6.0, "category": "sport"}
    | ],
    | status": "RESULTS RETURNED"
    |}
  """.stripMargin
}

class FakeAdvertiser(val id: String,
                     val name: String,
                     val url: String,
                     val categories: Seq[String],
                     val prices: Seq[Double])(implicit val rs: RetryStrategy,
                                              val ec: ExecutionContext)
    extends Advertiser {

  override protected def parse(body: String): Seq[Ad] = {
    for (i <- 1 to 10) yield {
      Ad(
        adId = UUID.randomUUID().toString,
        url = new URL("http://www." + name + ".com/" + i),
        price = RandomGenericCreators.randomElem(prices),
        category = RandomGenericCreators.randomElem(categories)
      )
    }
  }
}

object TestData {
  val NUM_OF_THREAD = 30
  val MAX_RETRY_ATTEMPTS = 2
  val RETRY_DURATION_IN_MILLIS = 20

  val ex: ExecutorService = Executors.newFixedThreadPool(NUM_OF_THREAD)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(ex)

  implicit val retryStrategy: RetryStrategy =
    RetryStrategy.fixedBackOff(retryDuration = RETRY_DURATION_IN_MILLIS.millisecond, maxAttempts = MAX_RETRY_ATTEMPTS)

  val fakeAdvertiser1 = new FakeAdvertiser(
    id = "123",
    name = "abc",
    url = "http://www.abc123.com/non-exist-path",
    categories = Seq("game", "sport", "casino"),
    prices = Seq(2.5, 1.5, 3.0, 2.0, 6.0, 4.5, 1.0)
  )

  val fakeAdvertiser2 = new FakeAdvertiser(
    id = "8910",
    name = "xyz",
    url = "http://www.xyz8910.com/non-exist-path",
    categories = Seq("game", "sport", "casino", "food"),
    prices = Seq(6.2, 1.2, 5.2, 9.2, 3.2)
  )
}
