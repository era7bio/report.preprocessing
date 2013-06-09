package era7.report.preprocessing

import scala.language.reflectiveCalls

import org.rogach.scallop._
import scalax.io._
import scalax.file.Path
import scalax.file.ImplicitConverters._
import scala.sys.process._


object IOConf {

  implicit val codec = scalax.io.Codec.UTF8
}

import IOConf._

case class AppConf(arguments: Seq[String]) extends ScallopConf(arguments) {

  version("preprocessing-report 0.1.0")

  val prefix = opt[String](
    required = true, short = 'p',
    descr = "the prefix you used for running prinseq"
  )

  val input = opt[String](
    required = true, short = 'i',
    descr = "the folder containing prinseq results (pngs and html report)"
  )

  val gd = opt[String](
    required = true, short = 'g',
    descr = "the .gd file produced by prinseq"
  )

  val output = opt[String](
    required = true, short = 'o',
    descr = "output folder"
    // this is just the wrapped Option inside ScallopOption
    // default = prefix.get
  )

  // template vals
  val sequencing_provider =  opt[String](
                                          required = true,
                                          descr = "the sequencing provider from which the data comes from",
                                          noshort = true
                                        )

  val sequencing_technology =  opt[String](
                                            required = true,
                                            descr = "the sequencing technology used for generating data",
                                            noshort = true
                                          )

  val reads_type = opt[String](
                                required = true, noshort = true,
                                descr = "type of reads (paired-end, single, whatever)"
                              )

  val data_reception_date =  opt[String](
                                          required = true, noshort = true,
                                          descr = "the date on which data was received from the provider"
                                        )

  val mean_qual_threshold =  opt[String](
                                          required = true, noshort = true,
                                          descr = "the mean quality score used to filter reads"
                                        )

  val trim_qual_threshold =  opt[String](
                                          required = true, noshort = true,
                                          descr = "the mean quality score used to trim reads on both ends"
                                        )





  val templateOpts =  sequencing_technology :: 
                      sequencing_provider ::
                      reads_type ::
                      data_reception_date :: 
                      // total_number_reads ::
                      // total_number_bases ::
                      mean_qual_threshold ::
                      trim_qual_threshold :: Nil

  // get rid of this, is no longer needed
  val templateListOpts = Nil

}

class Main extends xsbti.AppMain {
  def run(config: xsbti.AppConfiguration) =
    new Exit(Main.exec(config.arguments))
}

case class Exit(val code: Int) extends xsbti.Exit

object Main {

  def exec(args: Array[String]): Int = {

    val conf = AppConf(args)

    // TODO: implement retrieving stuff from gd file
    //
    // 1. read values from gd
    // 2. set corresponding ops
    // 3. check path to images etc
    // 4. copy images to out folder
    // 5. apply template

    println("creating dir: "+ conf.prefix())

    // copy template to prefix/
    val working_path = Path(conf.prefix())
    working_path.createDirectory(failIfExists = false)
    val template_path = working_path / template.name

    println("copying template to "+ conf.prefix())
    template.copyTo(template_path)

    val pandoc_cmd = pandoc.cmd(
                                 output = conf.output(),
                                 template = template.name,
                                 template_vars = conf.templateOpts,
                                 template_listVars = conf.templateListOpts
                               )

    println("pandoc command to be executed: "+ pandoc_cmd.toString)

    // exec pandoc
    (Seq("echo", "") #| Process(pandoc_cmd, working_path.asFile, "" -> "")).!
  }
}



object template {

  val name = "preprocessing.md.template"

  def copyTo(dest: Path) {

    val in_java: java.io.InputStream = getClass.getResourceAsStream("/"+ name)
    val in = Resource.fromInputStream(in_java)

    dest.write(in.bytes)
  }
}


// stupid wrapper for pandoc stuff
object pandoc {
 
  val optToPandocVar: ScallopOption[String] => Seq[String] = opt => opt.get match {

    case Some(value) => Seq("--variable", opt.name + "=" + value)
    case None => Seq.empty
  }
  
  val listOptToPandocVar: ScallopOption[List[String]] => Seq[String] = opt => opt.get match {

    case Some(values) => values flatMap {value => Seq("--variable", opt.name + "=" + value)}
    case None => Seq.empty
  }

  def cmd(
            output: String,
            template: String,
            template_vars: List[ScallopOption[String]], 
            template_listVars: List[ScallopOption[List[String]]]
          ): Seq[String] =  Seq(
                                  "pandoc",
                                  "--from", "markdown",
                                  "--to", "markdown",
                                  "--template", template,
                                  "--out", output
                                ) ++: 
                                (template_vars flatMap optToPandocVar) ++:
                                (template_listVars flatMap listOptToPandocVar)


}