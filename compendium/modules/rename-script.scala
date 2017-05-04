//in repo root:
// $ sbt
// > project plan
// > console
// scala> :paste compendium/modules/rename-script.scala

val here = "compendium/modules"

def list(path: String, prefix: String = ""): Vector[String] =
  (new java.io.File(path)).list.toVector.filter(_.startsWith(prefix))

def rename(path: String, f1: String, f2: String): Unit =
  (new java.io.File(path+"/"+f1)).renameTo(new java.io.File(path+"/"+f2))

def renameFiles(
  path: String,
  files: Vector[String],
  initFrom: String,
  initTo: String,
  isPrintOnly: Boolean = true
): Unit =
  files.foreach{ fileName =>
    if (fileName.startsWith(initFrom)) {
      val newFileName = initTo + fileName.stripPrefix(initFrom)
      val action = if (isPrintOnly) "Would have renamed" else "Rename"
      println(s"$action from $fileName -> $newFileName")
      if (!isPrintOnly) rename(path, fileName, newFileName)
    } else {
      println(s"Not renamed: $fileName")
    }
  }

// renameFiles(here, list(here,"w42"), "w42-intro", "w24-intro", isPrintOnly=true)
