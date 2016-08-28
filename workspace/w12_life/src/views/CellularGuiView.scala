package views

import java.io._
import javafx.animation.{Animation, KeyFrame, Timeline}
import javafx.application.Application
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control._
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.stage.{FileChooser, Stage}
import javafx.util.Duration

import models.{ArrayMatrix2D, Matrix2D}
import rules.{Rule, LifeRule}


object CellularGuiView extends CellularView2D {

  private var modelMatrix: Matrix2D = null
  private var modelChanged: Boolean = true

  protected var rule: Rule = LifeRule
  var lifeGuiApp: CellularGuiView = null
  var started = false
  var drawEdges = true
  var quit = false
  var colors: Array[Color] = Array(Color.WHITE, Color.BLACK)
  var renderCellText: Boolean = false

  private def start() {
    if(started) {
      throw new Exception("GUI has already been opened once, cannot be opened twice.")
    }

    if(modelMatrix == null) {
      modelMatrix = ArrayMatrix2D(20, 20)
    }

    new Thread(new Runnable() {
      override def run() {
        started = true
        Application.launch(classOf[CellularGuiView])
        quit = true
      }
    }).start()
  }

  def useRule(rule: Rule): Unit = {
    this.rule = rule
  }

  def setMatrixChanged(): Unit = {
    modelChanged = true
  }

  // Will return true if display() has been called since last call to this function
  // A kind of event + getter + resetter in one
  protected def changedSinceLastCall(): Boolean = {
    if(modelChanged) {
      modelChanged = false
      true
    } else {
      false
    }
  }

  def currentMatrix(): Matrix2D = {
    modelMatrix
  }

  /*
  def useColors(colorArray: Array[String]): Unit = {
    colors = colorArray
  }
  */

  def useColors(colorArray: Array[Color]): Unit = {
    def colorToCSSColorString(color: Color): String = {
      s"#${color.toString.substring(2, 8)}"
    }
    colors = colorArray.array
  }

  def useGreyscale(n: Int = 2) = {
    val colorSeq = (1.0 to 0.0 by -1.0/(n-1)).map(Color.gray)
    useColors(colorSeq.toArray)
  }

  override def display(m: Matrix2D): Unit = {
    if(quit) {
      throw new Exception("GUI has quit and can no longer display a matrix. Please restart application to use GUI again.")
    }
    modelMatrix = m
    modelChanged = true
    if(!started)
      this.start()
  }
}

// TODO: Switch to using Canvas instead of StackPanes
protected class CellularGuiView extends Application {
  var root: VBox = new VBox
  var canvas: Canvas = null
  var primaryStage: Stage = null

  var playing = false

  // TODO: Move to Matrix
  private var gen = 1

  override def start(primaryStage: Stage) {
    this.primaryStage = primaryStage
    primaryStage.setTitle("Game of Life")

    createGui()

    val rendertimer: Timeline = new Timeline(new KeyFrame(
      Duration.millis(1000/60), new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          if(CellularGuiView.changedSinceLastCall()) {
            renderBoard()
          }
        }
      }))
    rendertimer.setCycleCount(Animation.INDEFINITE)
    rendertimer.play()

    val playtimer: Timeline = new Timeline(new KeyFrame(
      Duration.millis(1000/10), new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          if(playing) {
            nextGen()
          }
        }
      }))
    playtimer.setCycleCount(Animation.INDEFINITE)
    playtimer.play()

    CellularGuiView.lifeGuiApp = this
  }

  val generationLabel = new Label("Generation: " + this.gen)
  def setGeneration(gen: Int) = {
    this.gen = gen
    generationLabel.setText("Generation: " + this.gen)
  }

  def nextGen() {
    CellularGuiView.display(CellularGuiView.modelMatrix.applyRule(CellularGuiView.rule))
    setGeneration(gen + 1)
  }

  def clear() {
    CellularGuiView.currentMatrix().clear()
    CellularGuiView.display(CellularGuiView.currentMatrix())
    setGeneration(0)
  }

  def randomize() {
    CellularGuiView.currentMatrix().randomize()
    CellularGuiView.setMatrixChanged()
    setGeneration(0)
  }

  def saveToFile(): Unit = {
    val fileChooser = new FileChooser()
    fileChooser.setTitle("Save Board")
    val file = fileChooser.showSaveDialog(primaryStage)
    if (file != null) {
      try {
        val bw = new BufferedWriter(new FileWriter(file))
        bw.write(CellularGuiView.currentMatrix().toFileFormat)
        bw.close()
      } catch {
        case ioe: IOException =>
          println(ioe.getMessage())
      }
    }
  }

  def loadFromFile(): Unit = {
    val fileChooser = new FileChooser()
    fileChooser.setTitle("Open Board")
    val file = fileChooser.showOpenDialog(primaryStage)
    val br = new BufferedReader(new FileReader(file))
    val lines = br.lines().toArray.mkString("\n")
    val matrix = ArrayMatrix2D.fromFileFormat(lines)
    CellularGuiView.display(matrix)
  }

  def createCanvasBoard(): Canvas = {
    val height = 600
    val width = 600

    canvas = new Canvas(width, height)
    canvas.setOnMousePressed(new EventHandler[MouseEvent] {
        override def handle(e: MouseEvent) {
          val matrix: Matrix2D = CellularGuiView.currentMatrix()

          val cellWidth = canvas.getWidth/matrix.cols
          val cellHeight = canvas.getHeight/matrix.rows

          val col = (e.getX/cellWidth).toInt
          val row = (e.getY/cellHeight).toInt

          matrix.set(row, col, (matrix(row)(col) + 1) % matrix.states)
          CellularGuiView.display(matrix)
        }
      })

    return canvas
  }

  def renderBoard(): Unit = {
    val matrix = CellularGuiView.currentMatrix()

    val rows = matrix.rows
    val cols = matrix.cols

    val edgeOffset = 0.2

    val ctx = canvas.getGraphicsContext2D
    ctx.setFill(if(CellularGuiView.drawEdges) Color.gray(0.5) else Color.WHITE)
    ctx.fillRect(0, 0, canvas.getWidth, canvas.getHeight)
    val cellWidth = canvas.getWidth/cols
    val cellHeight = canvas.getHeight/rows
    for(row <- 0 until rows; col <- 0 until cols) {
      ctx.setFill(CellularGuiView.colors(matrix(row)(col)))

      if(CellularGuiView.drawEdges) {
        ctx.fillRect(edgeOffset+col*cellWidth, edgeOffset+row*cellHeight, cellWidth-(2*edgeOffset), cellHeight-(2*edgeOffset))
      } else {
        ctx.fillRect(col*cellWidth, row*cellHeight, cellWidth, cellHeight)
      }

      if(CellularGuiView.renderCellText) {
        //ctx.setFill(LifeGuiView.colors((matrix(row)(col)+1) % LifeGuiView.colors.length))
        ctx.setFill(Color.BLACK)
        ctx.fillText(matrix(row)(col).toString, col * cellWidth, (row + 1) * cellHeight)
      }
    }
    ctx.save()
  }


  def createToolbar(): VBox = {
    val toolbar = new VBox
    toolbar.setSpacing(10)
    toolbar.setPadding(new Insets(10, 10, 10, 10))

    val buttonbox = new HBox(10)
    val playBtn = new Button("Play (P)")
    playBtn.setOnAction(new EventHandler[ActionEvent] {
      override def handle(e: ActionEvent) {
        playing = !playing
        playBtn.setText(if(playing) "Pause (P)" else "Play (P)")
      }
    })
    val nextBtn = new Button("Next (N)")
    nextBtn.setOnAction(new EventHandler[ActionEvent] {
      override def handle(e: ActionEvent) {
        nextGen()
      }
    })
    val clearBtn = new Button("Clear (C)")
    clearBtn.setOnAction(new EventHandler[ActionEvent] {
      override def handle(e: ActionEvent) {
        clear()
      }
    })
    val randomizeBtn = new Button("Randomize (R)")
    randomizeBtn.setOnAction(new EventHandler[ActionEvent] {
      override def handle(e: ActionEvent) {
        randomize()
      }
    })
    buttonbox.getChildren.addAll(playBtn, nextBtn, clearBtn, randomizeBtn)

    val infobox = new HBox(70)
    infobox.getChildren.addAll(generationLabel)
    toolbar.getChildren.addAll(buttonbox, infobox)

    return toolbar
  }

  def createMenuBar(): MenuBar = {
    val menuFile = new Menu("File")

    val save = new MenuItem("Save...")
    save.setOnAction(new EventHandler[ActionEvent] {
      override def handle(e: ActionEvent): Unit = {
        saveToFile()
      }
    })
    val load = new MenuItem("Load...")
    load.setOnAction(new EventHandler[ActionEvent] {
      override def handle(e: ActionEvent): Unit = {
        loadFromFile()
      }
    })
    val exit = new MenuItem("Exit")
    exit.setOnAction(new EventHandler[ActionEvent] {
      override def handle(e: ActionEvent): Unit = {
          System.exit(0)
      }
    })

    menuFile.getItems.addAll(save, load, new SeparatorMenuItem(), exit)
    return new MenuBar(menuFile)
  }

  def createGui(): Unit = {
    val scene = new Scene(root, 600, 700)
    // Maps 'C' key to clear button and 'N' key to next button
    scene.setOnKeyPressed(new EventHandler[KeyEvent] {
      override def handle(e: KeyEvent) {
        if (e.getCode == KeyCode.P) {
          playing = !playing
        } else if (e.getCode == KeyCode.N) {
          nextGen()
        } else if (e.getCode == KeyCode.R) {
          randomize()
        } else if (e.getCode == KeyCode.C) {
          clear()
        }
      }
    })
    primaryStage.setScene(scene)
    primaryStage.show

    val menuBar = createMenuBar()
    val canvasBoard = createCanvasBoard()
    val toolbar = createToolbar()

    root.getChildren.addAll(menuBar, canvasBoard, toolbar)
  }
}
