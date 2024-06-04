package benchmarks

import java.util.Random


class PiThreadBenchmark(val threadCount: Int, val points: Int) {
  
  def run(): Double = {
    println("Threads started")
    val samplesPerThread = points / threadCount
    val spare = points % threadCount
    //Создание потоков и разделение между ними точек для обработки
    val threads = (1 to threadCount).map { id =>
      val toRun = samplesPerThread + (if (id <= spare) 1 else 0)
      new MonteCarloThread(toRun)
    }

    threads.foreach(_.start())
    threads.foreach(_.join())

    val count = threads.map(_.count).sum
    4.0 * count / points
  }

  private class MonteCarloThread(iterations: Int) extends Thread {
    var count = 0

    override def run(): Unit = {
      println(Thread.currentThread().getName)
      val random = new Random()
      count = (1 to iterations).count { _ =>
        val x = random.nextDouble()
        val y = random.nextDouble()
        x * x + y * y < 1
      }
    }
  }
}