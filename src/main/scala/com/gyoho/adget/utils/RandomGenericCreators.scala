package com.gyoho.adget.utils

import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

object RandomGenericCreators {
  def randomString(): String = UUID.randomUUID().toString
  def randomInt(floor: Int, ceiling: Int): Int =
    ThreadLocalRandom.current().nextInt(floor, ceiling)
  def randomElem[T](seq: Seq[T]): T = seq(randomInt(0, seq.length))
}