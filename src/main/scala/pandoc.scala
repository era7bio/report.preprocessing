package era7.pandoc

import org.rogach.scallop._

sealed trait PandocFormat
case object markdown extends PandocFormat
case object pdf extends PandocFormat

trait PandocTemplateAux {

  type From <: PandocFormat
  type To <: PandocFormat
  type TemplateOption <: TemplateVarAux
  val name: String
  val from: From
  val to: To
}

trait PandocTemplate[
  F <: PandocFormat,
  T <: PandocFormat
] extends PandocTemplateAux {
  type From  = F
  type To  = T
}

abstract class Template[
  F <: PandocFormat, 
  T <: PandocFormat
](val name: String, val from: F, val to: T) extends PandocTemplate[F,T] {}

// TODO: move this to records?
trait TemplateVarAux { 

  type Template <: PandocTemplateAux
  val name: String
  val value: String
  val cmd: Seq[String]
}
abstract class TemplateVar[T <: PandocTemplateAux](val name: String, val value: String) extends TemplateVarAux { 
  type Template = T 
  val cmd = Seq("--variable", name + "=" + value)
}


// stupid wrapper for pandoc stuff
object pandoc {

  def applyTemplate[T <: PandocTemplateAux](template: T)(options: List[template.TemplateOption], out: String): Seq[String] = {

    val buh: Seq[String] = options flatMap {op => op.cmd}

    Seq("pandoc", "--out", out,
      "--from", template.from.toString,
      "--to", template.to.toString,
      "--template", template.name
    ) ++ (buh: Seq[String])
  }
    
}