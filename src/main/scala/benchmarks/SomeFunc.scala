package benchmarks

import scala.concurrent.duration.*
import cats.effect.unsafe.implicits.global

class SomeFunc(threadCount: Int) {
  //val threadCounts = List(2, 4) // Разные значения количества потоков

    val futuresBenchmark = new PiBenchmark(threadCount, 50_000_000)
    val threadsBenchmark = new PiThreadBenchmark(threadCount, 50_000_000)
    val parIOBenchmark = new PiParIOBenchmark(threadCount, 50_000_000)
    val fiberBenchmark = new PiFiberBenchmark(threadCount, 5_000_000)

    val (futuresResult, futuresDuration) = time { futuresBenchmark.run() }
    val (threadsResult, threadsDuration) = time { threadsBenchmark.run() }
    val (parIOResult, parIODuration) = time { parIOBenchmark.run() }
    val (fiberResult, fiberDuration) = time { fiberBenchmark.run() }

    println(s"Thread count: $threadCount")
    println(s"  Futures: Estimated value of Pi: $futuresResult, Time taken: ${futuresDuration.toMillis} ms")
    println(s"  Threads: Estimated value of Pi: $threadsResult, Time taken: ${threadsDuration.toMillis} ms")
    println(s"  ParIO: Estimated value of Pi: $parIOResult, Time taken: ${parIODuration.toMillis} ms")
    println(s"  Fiber: Estimated value of Pi: $fiberResult, Time taken: ${fiberDuration.toMillis} ms")

  def time[A](block: => A): (A, FiniteDuration) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, (end - start).nanos)
  }
  def getResults: String = {
    s"Thread count: $threadCount \n"+
    s"  Futures: Estimated value of Pi: $futuresResult, Time taken: ${futuresDuration.toMillis} ms\n"+
    s"  Threads: Estimated value of Pi: $threadsResult, Time taken: ${threadsDuration.toMillis} ms\n"+
    s"  ParIO: Estimated value of Pi: $parIOResult, Time taken: ${parIODuration.toMillis} ms\n"+
    s"  Fiber: Estimated value of Pi: $fiberResult, Time taken: ${fiberDuration.toMillis} ms\n"
  }
  def getResultsDouble: List[Double] = {
    List(futuresDuration.toMillis, threadsDuration.toMillis, parIODuration.toMillis, fiberDuration.toMillis)
  }
}