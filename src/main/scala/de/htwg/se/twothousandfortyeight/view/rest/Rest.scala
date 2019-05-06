package de.htwg.se.twothousandfortyeight.view.tui

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import de.htwg.se.twothousandfortyeight.controller.TurnTrait
import de.htwg.se.twothousandfortyeight.controller.actorBaseImpl.CommandActor
import de.htwg.se.twothousandfortyeight.util.Utils

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success

class Rest(turn: TurnTrait) {
  val cmdActor = system.actorOf(Props(classOf[CommandActor], turn), "commandactor")

  implicit val timeout = Timeout(5 seconds)
  implicit val system = ActorSystem("system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route: Route = get {
    pathSingleSlash {
      complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "<h1>2048</h1>"))
    }
    path("2048") {
      printTui
    } ~
      path("2048" / Segment) {
        command => {
          val future = (cmdActor ? command).mapTo[Int]
          /*val cmd = future.onComplete({
            case Success(cmd) => cmd
            case Failure(e) => e.printStackTrace
          })*/

          val cmd = Await.result(future, 5 seconds)

          cmd match {
            case 0 => printTui
            case 1 => printWin
            case 2 => printLose
            case 3 => printHelp
          }
        }
      }
  }

  def printTui: StandardRoute = {
    println("Turn made with REST!")
    println

    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
      "<h1>2048</h1>" + turn.game.toString + "\n" + "Your Score: " + turn.game.score.toString + "\n"))
  }

  def printWin: StandardRoute = {
    println("Game won with REST!")
    println

    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "You won!" + "\n"))
  }

  def printLose: StandardRoute = {
    println("Game lost with REST!")
    println

    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "You lost!" + "\n"))
  }

  def printHelp: StandardRoute = {
    println("Called help with REST!")
    println

    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Utils.help))
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  def unbind = {
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
