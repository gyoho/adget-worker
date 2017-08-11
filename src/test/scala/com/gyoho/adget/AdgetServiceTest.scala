package com.gyoho.adget

import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class AdgetServiceTest extends WordSpec with Matchers {

  import TestData._
  val advtz = Seq(fakeAdvertiser1, fakeAdvertiser2)

  "The adget servie" should {
    "return map of category to max price of ad" in {
      val res: Map[String, Ad] = AdgetService.getNewAdMap(advtz, 1000)
      val uniqueCategory = advtz.flatMap(_.categories).toSet
      val uniquePrice = advtz.flatMap(_.prices).toSet

      res.size should equal(uniqueCategory.size)
      res.values.foreach(_.price should be <= uniquePrice.max)
    }
  }

  "The adget servce" should {
    "return updated map" in {
      val category = "sport"
      AdgetService.getAds(category) should be(None)

      AdgetService.updateAdMap(advtz, 1000)
      AdgetService.getAds(category) should not be None
    }
  }

  "The adget service" should {
    "return consistent on-stale ad for all sdk request" in {
      // create 10 threads and access it with 1 millis gap
      val category = "sport"

      def getTenAds(category: String): Seq[Future[Option[Ad]]] =
        (1 to 10).map(idx =>
          Future {
            Thread.sleep(idx * 10)
            AdgetService.getAds(category)
        })

      val adsBeforeUpdate = getTenAds(category)
      Future.sequence(adsBeforeUpdate) onComplete {
        case Success(res)  =>
          val s = res.toSet
          s.size should be(1)
          s.head should be(None)

        case Failure(expt) => fail(s"failed to get ads: $expt")
      }


      Future {
        AdgetService.updateAdMap(advtz, 150)
      }

      Thread.sleep(200)

      val adsAfterUpdate = getTenAds(category)
      Future.sequence(adsAfterUpdate) onComplete {
        case Success(res)  =>
          val s = res.toSet
          s.size should be(1)
          s.head should not be None

        case Failure(expt) => fail(s"failed to get ads: $expt")
      }

      Thread.sleep(3000)
    }
  }
}
