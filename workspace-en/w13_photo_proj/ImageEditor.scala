package photo

import introprog.Image

class ImageEditor(im: Image, initPath: String):
  var history: Vector[Image] = Vector(im)

  // TODO; use PixelWindow to implement gui