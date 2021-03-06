package de.htwg.se.twothousandfortyeight.model.gameModel.gameBaseImpl

import de.htwg.se.twothousandfortyeight.TwoThousandFortyEight
import de.htwg.se.twothousandfortyeight.model.gameModel.gameBaseImpl.Operations.{addTile, compareLines, getPositionOfTile}

import java.util

case class Game(grid: Array[Tile] =
                Operations.addTile(Operations.addTile(
                  new Array[Tile](TwoThousandFortyEight.FIELD_SIZE * TwoThousandFortyEight.FIELD_SIZE).map(_ => Tile()))),
                score: Score = new Score) {

  def reset: Game = Game()

  def rotate: Game = {
    val newTiles = new Array[Tile](TwoThousandFortyEight.FIELD_SIZE * TwoThousandFortyEight.FIELD_SIZE)
    val oX = TwoThousandFortyEight.FIELD_SIZE - 1
    val oY = 0
    val angle = 90

    val radians = Math.toRadians(angle)
    val cos = Math.cos(radians).toInt
    val sin = Math.sin(radians).toInt

    for (x <- 0 until TwoThousandFortyEight.FIELD_SIZE) {
      for (y <- 0 until TwoThousandFortyEight.FIELD_SIZE) {
        val nX = (x * cos) - (y * sin) + oX
        val nY = (x * sin) + (y * cos) + oY
        newTiles(nX + nY * TwoThousandFortyEight.FIELD_SIZE) = Operations.getPositionOfTile(this.grid, x, y)
      }
    }

    Game(newTiles, this.score)
  }

  def left: Game = {
    var needsATile = false
    var gameNew = new Game
    val scoreOld = this.score.value

    var score = 0
    for (i <- 0 until TwoThousandFortyEight.FIELD_SIZE) {
      val singleLine = this.getSingleLine(i)
      val mergedLine = Game(singleLine.grid, new Score).moveSingleLine.mergeSingleLine

      System.arraycopy(mergedLine.grid, 0, this.grid, i * TwoThousandFortyEight.FIELD_SIZE, TwoThousandFortyEight.FIELD_SIZE)

      score = score + mergedLine.score.value

      if (!needsATile && !compareLines(singleLine.grid, mergedLine.grid)) {
        needsATile = true
      }
    }

    gameNew = Game(this.grid, Score(score + scoreOld))

    if (needsATile) {
      gameNew = Game(addTile(gameNew.grid), gameNew.score)
    }

    gameNew
  }

  def right: Game = this.rotate.rotate.left.rotate.rotate

  def up: Game = this.rotate.rotate.rotate.left.rotate

  def down: Game = this.rotate.left.rotate.rotate.rotate

  def moveSingleLine: Game = {
    val oldLine = this.grid
    val helperList = new util.LinkedList[Tile]
    for (i <- 0 until TwoThousandFortyEight.FIELD_SIZE) {
      if (!oldLine(i).isEmpty) {
        helperList.addLast(oldLine(i))
      }
    }

    if (helperList.size() == 0) {
      Game(oldLine, this.score)
    } else {
      val newLine = new Array[Tile](TwoThousandFortyEight.FIELD_SIZE)

      while (helperList.size != TwoThousandFortyEight.FIELD_SIZE) {
        helperList.add(Tile())
      }

      for (i <- 0 until TwoThousandFortyEight.FIELD_SIZE) {
        newLine(i) = helperList.removeFirst()
      }

      Game(newLine, this.score)
    }
  }

  def mergeSingleLine: Game = {
    val oldLine = this.grid
    val helperList = new util.LinkedList[Tile]
    var scoreNew = this.score

    var i = 0
    while (i < TwoThousandFortyEight.FIELD_SIZE && !oldLine(i).isEmpty) {
      var oldValue = oldLine(i).value
      if (i < (TwoThousandFortyEight.FIELD_SIZE - 1) && oldLine(i).value == oldLine(i + 1).value) {
        oldValue *= 2
        scoreNew = Score(this.score.value + oldValue)

        i = i + 1
      }

      helperList.add(Tile(oldValue))
      i = i + 1
    }

    if (helperList.size() == 0) {
      Game(oldLine, scoreNew)
    } else {
      while (helperList.size != TwoThousandFortyEight.FIELD_SIZE) {
        helperList.add(Tile())
      }

      Game(helperList.toArray(new Array[Tile](TwoThousandFortyEight.FIELD_SIZE)), scoreNew)
    }
  }

  def getSingleLine(index: Int): Game = {
    val singleLine = new Array[Tile](TwoThousandFortyEight.FIELD_SIZE)

    for (i <- 0 until TwoThousandFortyEight.FIELD_SIZE) {
      singleLine(i) = getPositionOfTile(this.grid, i, index)
    }

    Game(singleLine, this.score)
  }

  override def toString: String = {
    val sb = new StringBuilder

    for (i <- 0 until TwoThousandFortyEight.FIELD_SIZE) {
      val tiles = this.getSingleLine(i).grid
      for (j <- 0 until TwoThousandFortyEight.FIELD_SIZE) {
        sb.append("(" + tiles(j) + ")")
      }

      if (i != (TwoThousandFortyEight.FIELD_SIZE - 1)) {
        sb.append("\n")
      }
    }

    sb.toString
  }
}
