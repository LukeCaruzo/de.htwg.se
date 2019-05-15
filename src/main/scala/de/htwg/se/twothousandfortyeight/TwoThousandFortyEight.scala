package de.htwg.se.twothousandfortyeight

import de.htwg.se.twothousandfortyeight.controller.turnBaseImpl.Turn
import de.htwg.se.twothousandfortyeight.view.microservice.{CounterService, CounterServiceServer, HighScoreService, HighScoreServiceServer}
import de.htwg.se.twothousandfortyeight.view.tui.{Rest, Tui}

object TwoThousandFortyEight {
  val FIELD_SIZE = 4 // Tests are configured for 4

  def main(args: Array[String]): Unit = {
    val turn = new Turn

    new Rest(turn)
    new CounterServiceServer(new CounterService)
    new HighScoreServiceServer(new HighScoreService)
    new Tui(turn)
  }
}
