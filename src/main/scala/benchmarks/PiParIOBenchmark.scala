package benchmarks

import cats.effect.IO
import cats.syntax.all.*
import cats.effect.unsafe.implicits.global

import scala.util.Random

class PiParIOBenchmark(val threadCount: Int, val points: Int) {

  def run(): Double = {
    println("ParIO started")
    val samplesPerThread = points / threadCount
    val spare = points % threadCount
    //Создание листа с IO, которое будет выполнять задачу ioCalculation
    val ioList: List[IO[Int]] = (1 to threadCount).map { id =>
      val toRun = samplesPerThread + (if (id <= spare) 1 else 0)
      ioCalculation(toRun)
    }.toList
    //Выполнение IO
    val countIO: IO[Int] = ioList.parSequence.map(_.sum)

    countIO.map(count => 4.0 * count / points).unsafeRunSync()
  }
  

  private def ioCalculation(iterations: Int): IO[Int] = IO {
    var count = 0
    val random = new Random()
    println(Thread.currentThread().getName)
    for (_ <- 1 to iterations) {
      val x = random.nextDouble()
      val y = random.nextDouble()
      if (x * x + y * y < 1) count += 1
    }
    count
  }
}