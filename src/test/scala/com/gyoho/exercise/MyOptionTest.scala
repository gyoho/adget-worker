package com.gyoho.exercise

import org.scalatest.{Matchers, WordSpec}

class MyOptionTest extends WordSpec with Matchers {
  "The Option implementation" should {
    "possesses all the properties/functions of the Option" in {
      MyOption(null) should be(MyNone)
      MyOption("yo") should be(MySome("yo"))
      MyOption(2) should be(MySome(2))

      MySome(3).get should equal(3)
    }
  }
}
