//> using scala 3.3
//> using dep se.lth.cs::introprog::1.3.1

package photo

import introprog.{PixelWindow, Image, IO}
import introprog.PixelWindow.Event
import scala.util.{Try, Success, Failure}

val DefaultStartDir = "images/"

lazy val mainButtons: Seq[Button] = Button.column(
  "Open" -> (() => doOpen()), 
  "Exit" -> (() => doExit(isWindowClosed = false)),
  )
  
val WinSize = (Button.Size._1 + Button.Pad * 2 , (Button.Size._2 + Button.Pad * 2) * mainButtons.length)
private var mainWindow: PixelWindow = _ 

private def initMainWindow(): Unit = 
  mainWindow = new PixelWindow(WinSize._1, WinSize._2, "Photo")
  mainWindow.clear()
  mainButtons.foreach(_.draw(mainWindow))
end initMainWindow

def doExit(isWindowClosed: Boolean): Unit = 
  println("TODO Exit; if exit is canceled after check and window was closed then re-init main window")

def doOpen(): Unit = 
  println(s"TODO Open selected image i ImageEditor in /$DefaultStartDir if exists else in current")

@main def photoMain() = 
  println("Welcome to Photo app!\n\nClick Open to edit image")
  initMainWindow()
  while mainWindow.lastEventType != Event.WindowClosed do
    mainWindow.awaitEvent(10)
    mainWindow.lastEventType match
      case Event.MousePressed  => 
        println(s"Event.MousePressed => lastMousePos == ${mainWindow.lastMousePos}")
        for b <- mainButtons if b.isClickInside(mainWindow.lastMousePos) do 
          println(s"Inside $b")
          b.drawOnClick(mainWindow)

      case Event.MouseReleased => 
        println(s"Event.MouseReleased => lastMousePos == ${mainWindow.lastMousePos}")
        for b <- mainButtons do 
          if b.isClickInside(mainWindow.lastMousePos) then
            println(s"Inside $b")
            b.onClick()
          end if
          b.draw(mainWindow)  // always draw normal even if released outside

      case Event.WindowClosed  => doExit(isWindowClosed = true)
      case _ =>

    Thread.sleep(10)
  end while
end photoMain
