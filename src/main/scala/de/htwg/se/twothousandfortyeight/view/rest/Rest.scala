package de.htwg.se.twothousandfortyeight.view.tui

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.stream.ActorMaterializer
import de.htwg.se.twothousandfortyeight.controller.turnBaseImpl.Turn
import de.htwg.se.twothousandfortyeight.model.fileIoModel.fileIoJsonImpl.FileIo
import play.api.libs.json.Json


class Rest {
  implicit val system = ActorSystem("system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val turn = new Turn
  val fileIo = new FileIo

  val route: Route = get {
    pathSingleSlash {
      complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "<h1>2048</h1>"))
    }
    path("2048" / Segment) {
      command => {
        publishKey(command.charAt(0)) match {
          case 0 => printTui
          case 1 => printWin
          case 2 => printLose
        }
      }
    }
  }

  def printTui: StandardRoute = {
    println("Turn made!")
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
      "<h1>2048</h1>" + turn.game.toString + "\n" + "Your Score: " + turn.game.score.toString + "\n"))
    //complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIo.serialize(turn.game)))
  }

  def printWin: StandardRoute = {
    println("Game won!")
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "You won!" + "\n"))
  }

  def printLose: StandardRoute = {
    println("Game lost!")
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "You lost!" + "\n"))
  }

  def publishKey(key: Char): Int = {
    key match {
      case 'a' =>
        turn.turnLeft
      case 'd' =>
        turn.turnRight
      case 's' =>
        turn.turnDown
      case 'w' =>
        turn.turnUp
      case 'q' =>
        turn.turnUndo
      case 'r' =>
        turn.turnReset
      case 'z' =>
        turn.turnSave
      case 'u' =>
        turn.turnLoad
      case 't' =>
        turn.turnExit
      case _ =>
        turn.evaluate
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  def unbind = {
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}