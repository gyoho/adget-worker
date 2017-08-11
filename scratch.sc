import scala.concurrent.duration._
import util.retry.blocking.{Failure => RFailure, Retry, RetryStrategy, Success => RSuccess}

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

// define the retry strategy
implicit val retryStrategy = RetryStrategy.fixedBackOff(retryDuration = 1.seconds, maxAttempts = 3)


println("start")

def calc(): Future[Long] = Future {
  println("retrying...")
  Thread.sleep(500)
  System.currentTimeMillis()
  5/0
}

val currentTime = System.currentTimeMillis()

// pattern match the result
val r = {
  val res = Retry(calc())

  res match {
    case RSuccess(x) =>
      println("retry succeed")
      x
    case RFailure(t) =>
      println("retry failed")
      println(t)
      5
  }
}

Thread.sleep(5000)

r

val now = (System.currentTimeMillis() - currentTime) / 1000

// recover in case of a failure
//val recover = Retry(12 / 4) recover {
//  case ex: Exception =>
//    println(ex)
//    5
//}