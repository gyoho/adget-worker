package com.gyoho.adget

import org.scalatest.{Matchers, WordSpec}

class AdgetServiceTest extends WordSpec with Matchers {

  import TestData._
  val advtz = Seq(fakeAdvertiser1, fakeAdvertiser2)

  "The adget servie" should {
    "return map of category to max price of ad" in {
      val res: Map[String, Ad] = AdgetService.getNewAdMap(advtz, 2000000)
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

      val updatedMap = AdgetService.updateAdMap(advtz, 1)
      println(AdgetService.getAds(category))
      AdgetService.getAds(category) should not be None
    }
  }
}
