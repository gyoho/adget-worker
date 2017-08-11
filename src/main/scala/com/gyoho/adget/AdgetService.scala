package com.gyoho.adget

import com.gyoho.adget.utils.ScalaHttpClient

object AdgetService {
  @volatile private var ads: Map[String, Ad] = Map.empty

  def updateAdMap(advertisers: Seq[Advertiser], timeoutInMillis: Long): Unit = {
    ads = getNewAdMap(advertisers, timeoutInMillis)
  }

  private[adget] def getNewAdMap(advertisers: Seq[Advertiser], timeoutInMillis: Long): Map[String, Ad] = {
    advertisers
      .flatMap(_.getAds(ScalaHttpClient, timeoutInMillis = timeoutInMillis))
      .groupBy(_.category)
      .mapValues { ads: Seq[Ad] =>
        ads.sortWith { (left: Ad, right: Ad) =>
          left.price > right.price
        }.head
      }
//      .map { elem: (String, Seq[Ad]) =>
//        elem._1 -> elem._2.sortWith { (left: Ad, right: Ad) =>
//          left.price > right.price
//        }.head
//      }
  }

  def getAds(category: String): Option[Ad] = ads.get(category)
}
