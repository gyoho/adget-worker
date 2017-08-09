package com.gyoho.adget

import org.scalatest.{Matchers, WordSpec}

class AdgetServiceTest extends WordSpec with Matchers {

  "The adget servie" should {
    "return map of category to max price of ad" in {
      import TestData.fakeAdvertiser

      val advtz: Seq[Advertiser] = Seq(fakeAdvertiser)
      val res: Map[String, Ad] = AdgetService.getNewAdMap(advtz, 1000)

      res foreach { println }

      res.size should equal(fakeAdvertiser.categories.size)
      res.values.foreach(_.price should be <= fakeAdvertiser.prices.max)
    }
  }
}
