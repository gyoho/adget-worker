package com.gyoho.adget

import java.net.URL
import java.util.UUID
import java.util.concurrent.{ExecutorService, Executors}

import com.gyoho.adget.utils.RandomGenericCreators
import org.scalatest.{Matchers, WordSpec}
import util.retry.blocking.RetryStrategy

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class AdvertiserTest extends WordSpec with Matchers {
  import TestData._

  "The advertiser" should {
    "return empty string after max number of retry" in {
      val res: String = fakeAdvertiser.sendRequest(timeoutInMillis = 10000)
      res should equal("")
    }
  }

  "The advertiser" should {
    "return empty string after timeout" in {
      val res: String = fakeAdvertiser.sendRequest(timeoutInMillis = 1)
      res should equal("")
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

class FakeAdvertiser(val id: String, val name: String, val url: String)(
    implicit val rs: RetryStrategy,
    val ec: ExecutionContext)
    extends Advertiser {

  val categories = Seq("game", "sport", "casino")
  val prices = Seq(2.5, 1.5, 3.0, 2.0, 6.0, 4.5, 1.0)

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
  val NUM_OF_THREAD = 5
  val ex: ExecutorService = Executors.newFixedThreadPool(NUM_OF_THREAD)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(ex)

  implicit val retryStrategy: RetryStrategy =
    RetryStrategy.fixedBackOff(retryDuration = 1.second, maxAttempts = 3)

  val fakeAdvertiser =
    new FakeAdvertiser(id = "123",
                       name = "abc",
                       url = "http://www.abc.com/non-exist-path")
}
