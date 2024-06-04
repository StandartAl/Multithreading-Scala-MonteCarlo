package benchmarks

import java.util.concurrent.ThreadLocalRandom
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.*


class PiBenchmark(val threadCount: Int, val points: Int) {
  
  implicit val ec: ExecutionContext = ExecutionContext.global
  
  def run(): Double = {
    println("Futures started")
    //Создание последовательности future
    val futures: Seq[Future[Int]] = (1 to points).grouped(points / threadCount).map { chunk =>
      Future {
        val count = chunk.count { _ =>
          val random = ThreadLocalRandom.current()
          val x = random.nextDouble()
          val y = random.nextDouble()
          x * x + y * y < 1
        }
        count
      }
    }.toSeq
    //Выполнение последовательности future
    val countFuture: Future[Int] = Future.sequence(futures).map(_.sum)
    val count = Await.result(countFuture, Duration.Inf)
    4.0 * count / points
  }
}