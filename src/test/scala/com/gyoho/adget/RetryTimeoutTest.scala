package com.gyoho.adget

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

/**
  * - Evaluation (= Execution in context of Future) happens when assigning a result to a value
  * - Delay evaluation/execution by marking it "lazy"
  */

object RetryTimeoutTest {
  def main(args: Array[String]): Unit = {

    def producer() = {
      println("producer - start")

      // by marking lazy, we can postponed these two future execution
      // until the list is referenced first time
      // which is in: Future.sequence(list)
      val list = Seq(
        Future {
          println("startFirst")
          Thread.sleep(3000)
          println("stopFirst")
        },
        Future {
          println("startSecond")
          Thread.sleep(1000)
          println("stopSecond")
        }
      )

      println("inverting the list")

      Future.sequence(list)
    }

    Await.result(producer(), Duration.Inf)

    println("\n----------------------------------\n")

    println("test - start")

    Thread.sleep(1000)

    val x1 = Future {
      println("x1 started")
      Thread.sleep(1000)
      println("x1 is completed")
      "Hello x1"
    }

    val x2 = Future {
      println("x2 started")
      Thread.sleep(3000)
      println("x2 is completed")
//      throw new Exception("Exception occured with x2")
      "Hello x2"
    }

    // make it lazy to avoid start the task in background immediately
    lazy val x3 = Future {
      println("x3 started")
      Thread.sleep(2000)
      println("x3 is completed")
      "Hello x3"
    }

    // what if we want to get the individual result in the order the tasks finish execution?
    println("assigning futures to a list")
    val seq = Seq(x1, x2, x3)

    println("inverting the list")
    val x = Future.sequence(seq)

    x.onComplete {
      case Success(res) => println("Success: " + res)
      case Failure(ex)  => println("Ohhh Exception: " + ex.getMessage)
    }

    // wait for the result for 5 sec
    // execution hang here
    Await.ready(x, 5.seconds)

    Thread.sleep(2000)

    println(s"Future val is: ${x.value}")
    println("test - end")
  }
}
