package era7.report.preprocessing

import scala.language.reflectiveCalls

import org.rogach.scallop._

import scalax.io._
import scalax.file._
import scalax.file.ImplicitConverters._
import scalax.file.ImplicitConversions._
import scalax.file.PathSet

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

  val paired_end = opt[Boolean](
    required = true, noshort = true,
    descr = "paired-end reads?"
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

object MainTest extends App {

  Main.exec(args)
}

object Main {

  def exec(args: Array[String]): Int = {

    val reportTemplate: pandoc.Template = pandoc.Template(name = "preprocessing.md.template", from = "markdown", to = "markdown")
    val conf = AppConf(args)

    // create prinseqops from opts

    // TODO: implement retrieving stuff from gd file
    //
    // 1. read values from gd
    // 2. set corresponding ops
    // 3. check path to images etc
    // 4. copy images to out folder
    // 5. apply template

    println("setting up output folder")
    val out = setOutputFolder(conf, reportTemplate)
    println("copying images to output folder")
    copyImgs(conf, out)

    val pandoc_cmd = pandoc.cmd(
                                 output = out.name,
                                 template = reportTemplate,
                                 templateVars = conf.templateOpts map pandoc.fromOpt,
                                 templateListVars = conf.templateListOpts map pandoc.fromListOpt
                               )



    println("pandoc command to be executed: "+ pandoc_cmd.toString)

    // exec pandoc
    (Seq("echo", "") #| Process(pandoc_cmd, FileSystem.default(out.name), "" -> "")).!
  }


  def setOutputFolder(conf: AppConf, template: pandoc.Template): Path = {

    // create output folder
    val outputF = Path(conf.output())
    outputF.createDirectory(failIfExists = false)
    // copy template to output folder
    ResourceOps.copyTo(template.name, outputF / template.name)
    outputF
  }

  def copyImgs(conf: AppConf, path: Path) = {

    val src: Path = Path(conf.input())
    val imgs = src * "*.png"
    val cp_path = path.createDirectory(failIfExists = false)
    imgs.foreach(img => img.copyTo(cp_path / img.name))
  }
}


