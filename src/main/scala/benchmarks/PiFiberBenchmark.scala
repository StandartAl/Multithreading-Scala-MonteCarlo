/*package benchmarks

import cats.effect.{IO, IOApp}
import cats.effect.std.Random
import cats.implicits._
import cats.effect.syntax.concurrent.concurrentParSequenceOps

object PiFiberBenchmark extends IOApp.Simple {

  class MonteCarloPiCalculator(numThreads: Int) {

    // Function to compute a single point in the Monte Carlo simulation
    def computePoint(random: Random[IO]): IO[Int] = for {
      x <- random.nextDouble
      y <- random.nextDouble
      insideCircle = if (x * x + y * y <= 1) 1 else 0
    } yield insideCircle

    // Function to compute points in parallel using fibers
    def computePoints(random: Random[IO], points: Int): IO[Int] = {
      val tasks = List.fill(points)(computePoint(random))
      tasks.parSequenceN(numThreads).map(_.sum)
    }

    // Function to estimate Pi
    def estimatePi(points: Int): IO[Double] = {
      println("Fibers started");
      for {
        random <- Random.scalaUtilRandom[IO]
        insideCircle <- computePoints(random, points)
      } yield 4.0 * insideCircle / points
    }
  }

  def run: IO[Unit] = {
    val points = 1000000
    val numThreads = 4 // Specify the number of threads here
    val calculator = new MonteCarloPiCalculator(numThreads)
    calculator.estimatePi(points).flatMap { piValue =>
      IO(println(s"Estimated Pi: $piValue"))
    }
  }
}*/

package benchmarks

import cats.effect.IO
import cats.implicits._
import scala.util.Random
import cats.effect.unsafe.implicits.global

class PiFiberBenchmark(val threadCount: Int, val points: Int) {
  def randomPoint(): IO[(Double, Double)] = IO {
    val random = new Random()
    val x = random.nextDouble()
    val y = random.nextDouble()
    (x, y)
  }

  def isInsideCircle(x: Double, y: Double): Boolean = x * x + y * y < 1

  def calculatePi(numPoints: Int): IO[Double] = {
    val pointsPerThread = numPoints / threadCount
    //Создание листа задач с IO
    val tasks = List.fill(threadCount)(calculatePartialPi(pointsPerThread).start)

    tasks.sequence.flatMap { fibers =>                          //Используем fiber, join запускает fiber и через flatMap получаем Outcome
      fibers.traverse(_.join).flatMap { outcomes =>
        val results = outcomes.collect {                        //Анализируем Outcome, так как без этого будет ошибка, и возвращаем результат (IO[Int])
          case cats.effect.Outcome.Succeeded(result) => result
        }
        results.sequence.map { counts =>
          val totalPointsInsideCircle = counts.sum
          (totalPointsInsideCircle.toDouble / numPoints) * 4
        }
      }
    }
  }

  private def calculatePartialPi(numPoints: Int): IO[Int] = {
    val points = List.fill(numPoints)(randomPoint())
    points.sequence.map { generatedPoints =>
      generatedPoints.count { case (x, y) => isInsideCircle(x, y) }
    }
  }

  def run(): Double = {
    println("Fibers started")
    calculatePi(points).unsafeRunSync()
  }
}
