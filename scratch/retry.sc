import scala.concurrent.duration._
import util.retry.blocking.{RetryStrategy, Failure, Retry, Success}

// define the retry strategy
implicit val retryStrategy = RetryStrategy.noBackOff(maxAttempts = 10)

// pattern match the result
val r = Retry(5 / 5) match {
  case Success(x) => x
  case Failure(t) =>
    println(t)
    5
}

// recover in case of a failure
val recover = Retry(12 / 4) recover {
  case ex: Exception =>
    println(ex)
    5
}