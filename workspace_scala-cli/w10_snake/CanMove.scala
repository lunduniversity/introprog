package snake

trait CanMove extends Entity:
  def move(): Unit

  var movesPerSecond: Double = 20.0

  final def millisBetweenMoves: Int = 
    (1000 / movesPerSecond).round.toInt max 1

  private var _timestampLastMove: Long = System.currentTimeMillis

  final def timestampLastMove = _timestampLastMove

  override final def update(): Unit =
    // flytta om tiden har gått millisBetweenMoves
    if System.currentTimeMillis > 
         _timestampLastMove + millisBetweenMoves 
    then
      _timestampLastMove = System.currentTimeMillis
      move()
