package era7.pandoc

import org.rogach.scallop._

sealed trait PandocFormat
case object markdown extends PandocFormat
case object pdf extends PandocFormat

trait PandocTemplateAux {

  type From <: formats.PandocFormat
  type To <: formats.PandocFormat
  type TemplateOption <: TemplateVar
  val name: String
  val from: From
  val to: To
  val options: List[TemplateOption]

  val cmd: Seq[String]
}

trait PandocTemplate[
  From <: formats.PandocFormat,
  To <: formats.PandocFormat
] extends PandocTemplateAux {
  type From  = F
  type To  = T
}

abstract class Template[
  F <: formats.PandocFormat, 
  T <: formats.PandocFormat
](val name: String, val from: F, val to: T) extends PandocTemplate[F,T] {

  val cmd = Seq(
    "--from", from,
    "--to", to,
    "--template", name,
    options flatMap {op => op.cmd}
  )
}

// TODO: move this to records?
trait TemplateVarAux { 

  type Template <: PandocTemplateAux
  val name: String
  val value: String
  val cmd: Seq[String]
}
abstract class TemplateVar[T <: PandocTemplateAux](name: String, value: String) { 
  type Template = T 
  val cmd = Seq("--variable", name + "=" + value)
}

object Ops {

  def applyTo[T <: PandocTemplateAux](template: T, out: String): Seq[String] = 
    Seq("pandoc", "--out", output) ++: template.cmd
}
  

// stupid wrapper for pandoc stuff
object pandoc {
 
  val fromOpt: ScallopOption[String] => TemplateVar = opt => opt.get match {

    case Some(value) => TemplateVar(opt.name, value)
    case None => TemplateVar("", "")
  }
  
  val fromListOpt: ScallopOption[List[String]] => TemplateListVar = opt => opt.get match {

    case Some(values) => TemplateListVar(opt.name, values)
    case None => TemplateListVar("", Nil)
  }                    
}