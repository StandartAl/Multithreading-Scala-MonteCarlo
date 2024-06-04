import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ComboBox, Label}
import scalafx.scene.paint.Color
import scalafx.event.ActionEvent
import scalafx.Includes.*
import benchmarks.SomeFunc
import scalafx.scene.text.{Font, TextAlignment}

import scala.collection.mutable.ListBuffer


object Main extends JFXApp3 {
  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Multithreading"
      scene = new Scene(500, 500) {
        fill = Color.rgb(255, 255, 255)
        val button = new Button("Start test")
        button.layoutX = 20
        button.layoutY = 20
        val labelOption1 = new Label()
        labelOption1.layoutX = 20
        labelOption1.layoutY = 60
        labelOption1.setFont(new Font(14))
        labelOption1.text = "Потоки"
        val labelOption2 = new Label()
        labelOption2.layoutX = 120
        labelOption2.layoutY = 60
        labelOption2.setFont(new Font(14))
        labelOption2.text = "Итерации"
        val comboBox = new ComboBox(1.to(32))
        comboBox.layoutX = 20
        comboBox.layoutY = 80
        val comboBox1 = new ComboBox(1.to(30))
        comboBox1.layoutX = 120
        comboBox1.layoutY = 80
        val label = new Label()
        label.layoutX = 20
        label.layoutY = 120
        val label1 = new Label()
        label1.layoutX = 150
        label1.layoutY = 290
        label1.textAlignment = TextAlignment.Justify
        label1.setFont(new Font(16))
        val labelName = new Label()
        labelName.layoutX = 150
        labelName.layoutY = 250
        labelName.text = "Min\t\t\tMax\t\t\tAvg"
        labelName.setFont(new Font(16))
        val labelBench = new Label()
        labelBench.layoutX = 20
        labelBench.layoutY = 290
        labelBench.text = "Futures\nJava Threads\nParIO\nFibers"
        labelBench.setFont(new Font(16))

        content = List(button, comboBox, comboBox1, label, label1, labelName, labelBench, labelOption1, labelOption2)

        button.onAction = (e:ActionEvent) => {
          val tableResults: ListBuffer[List[Double]] = ListBuffer[List[Double]]()
          val FcaseResult: ListBuffer[Double] = ListBuffer()
          val ScaseResult: ListBuffer[Double] = ListBuffer()
          val TcaseResult: ListBuffer[Double] = ListBuffer()
          val FourthcaseResult: ListBuffer[Double] = ListBuffer()

          for (iteration <- 1 to comboBox1.getValue) {
            val res = new SomeFunc(comboBox.getValue)
            label.text = res.getResults
            FcaseResult += res.getResultsDouble.head
            ScaseResult += res.getResultsDouble(1)
            TcaseResult += res.getResultsDouble(2)
            FourthcaseResult += res.getResultsDouble(3)
          }
          label1.text = ""
          tableResults += FcaseResult.toList += ScaseResult.toList += TcaseResult.toList += FourthcaseResult.toList
          tableResults.foreach(i =>
            label1.text = f"${label1.text.getValue} ${i.min}%6.2f\t\t ${i.max}%6.2f\t\t ${i.sum/i.length}%6.2f\n"
          )
        }
      }
    }
  }
}