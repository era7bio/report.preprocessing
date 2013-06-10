package era7.report.preprocessing

import org.rogach.scallop._

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

  case class TemplateVar(name: String, value: String) {

    def cmd = Seq("--variable", name + "=" + value)
  }
  case class TemplateListVar(name: String, values: List[String]) {

    def cmd: Seq[String] = values flatMap { value => Seq("--variable", name + "=" + value) }
  }

  case class Template(name: String, from: String, to: String) {

    def cmd = Seq(
      "--from", from,
      "--to", to,
      "--template", name
    )
  }

  def cmd(
    output: String,
    template: Template,
    templateVars: List[TemplateVar], 
    templateListVars: List[TemplateListVar]
  ): Seq[String] =  {

    val vars: Seq[String] = templateVars flatMap { x: TemplateVar => x.cmd }
    val lvars: Seq[String] = templateListVars flatMap { x: TemplateListVar => x.cmd }
    
    Seq(
      "pandoc",
      "--out", output
    ) ++:
    template.cmd ++: 
    vars ++:
    lvars
  }

                            


}