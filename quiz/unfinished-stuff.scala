// Just some unfinished ideas dumped into this file; nothing of below is in use yet...
package justSomeIdeas

object AnyToHexString {
  import javax.xml.bind.DatatypeConverter
  import java.security.MessageDigest
  def apply(a: Any) = {
    val bytes = a.toString.getBytes("UTF-8")
    val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
    DatatypeConverter.printHexBinary(digest)
  }
}

case class Alt(alt: String, hint: String, isCorrect: Boolean)

case class Quest(module: String, topics: String, quest:  String, alts: Vector[Alt]) {
  lazy val id = AnyToHexString(this)
  lazy val topicSet = topics.split(',').map(_.trim).map(_.toLowerCase)
}

trait QuestVector {
  def toVector: Vector[Quest]
}

trait QuestOps {
  self: QuestVector =>

  lazy val moduleVector: Vector[String] = toVector.map(_.module).distinct
  lazy val numberOfModule: Map[String, Int] = moduleVector.zipWithIndex.toMap
  lazy val moduleSet: Set[String] = moduleVector.toSet
  lazy val topicSet:  Set[String] = toVector.map(_.topicSet).toSet.flatten
  lazy val byModule: Map[String, Vector[Quest]] = {
    def questsOf(module: String) = toVector.filter(_.module == module)
    moduleSet.map(module => (module, questsOf(module))).toMap
  }
  lazy val byTopic: Map[String, Vector[Quest]] = {
    def questsOf(topic: String) = toVector.filter(_.topics.contains(topic))
    topicSet.map(topic => (topic, questsOf(topic))).toMap
  }
}

object Questions extends QuestVector with QuestOps {
  lazy val toVector: Vector[Quest] = Vector(
    Quest(
      module = "expressions",
      topics = "double, float, accuracy",
      quest  = "Which of these expressions are problematic from an accuracy point of view?",
      alts   = Vector(
        Alt("1.0 + 3.0",
          hint = "similar order of magnitude is often no problem",
          isCorrect = false
        ),
        Alt("1.0e10 + 3.0e-10",
          hint = "large difference in order of magnitude leads to truncation",
          isCorrect = true
        )
      )
    )
  )
}
