package era7.report.preprocessing

import scala.language.reflectiveCalls
import org.rogach.scallop._
import scalax.io._
import scalax.file._
import scalax.file.ImplicitConverters._
import scalax.file.ImplicitConversions._
import scala.sys.process._

import era7.pandoc._
import PreprocessingTemplateOptions._


object IOConf { implicit val codec = scalax.io.Codec.UTF8 }
import IOConf._

case class AppConf(arguments: Seq[String]) extends ScallopConf(arguments) {

  version("preprocessing-report 0.1.0")

  val prefix = opt[String](
    required = true, short = 'p',
    descr = "the prefix you used for running prinseq"
  )
  val input  = opt[String](
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

    // read conf
    val conf = AppConf(args)

    println("setting up output folder")
    val outFolder = setOutputFolder(conf, PreprocessingTemplate)

    println("copying images to output folder")
    copyImgs(conf, outFolder)
    println("copying .gd file")
    val gdpath = copyGD(conf, outFolder)
    println("parsing .gd file")
    val gdops = PairedEndPrinseqOps(gdpath)

    // read off template options
    val conf_opts = Aux.optionsFromConf(conf)
    // this should be something
    val default_opts = pairedEnd("true") :: preprocessed("true") :: Nil
    val gd_opts = Aux.optionsFromPrinseqData(gdops)

    val pandoc_cmd = pandoc.applyTemplate(PreprocessingTemplate)(
      options = conf_opts ::: default_opts ::: gd_opts,
      out = outFolder.name+".md"
    )

    println("pandoc command to be executed: "+ pandoc_cmd.toString)

    // exec pandoc
    (Seq("echo", "") #| Process(pandoc_cmd, FileSystem.default(outFolder.name), "" -> "")).!
  }


  def setOutputFolder(conf: AppConf, template: PandocTemplateAux): Path = {

    // create output folder
    val outputF = Path(conf.output())
    outputF.createDirectory(failIfExists = false)
    // copy template to output folder
    ResourceOps.copyTo(template.name, outputF / template.name)
    outputF
  }

  def copyImgs(conf: AppConf, path: Path): Unit = {

    val src: Path = Path(conf.input())
    val imgs = src * "*.png"
    val cp_path = path.createDirectory(failIfExists = false)
    imgs.foreach(img => img.copyTo(cp_path / img.name, replaceExisting=true))
  }

  def copyGD(conf: AppConf, path: Path): Path = {

    val src: Path = Path.fromString(conf.gd())
    val cp_path = path.createDirectory(failIfExists = false)
    src.copyTo(cp_path / src.name, replaceExisting=true)
  }
}

object Aux {

  def optionsFromConf(conf: AppConf): List[TemplateVar[PreprocessingTemplate.type]] = {

    sequencingProvider(conf.sequencing_provider())      ::
    prefix(conf.prefix())                               ::
    sequencingTechnology(conf.sequencing_technology()) ::
    trimQualThreshold(conf.trim_qual_threshold())     ::
    meanQualThreshold(conf.mean_qual_threshold())       ::
    readsType(conf.reads_type())                        ::
    dataReceptionDate(conf.data_reception_date())       :: Nil
  }

  def optionsFromPrinseqData(prinseq: PairedEndPrinseqOps): List[TemplateVar[PreprocessingTemplate.type]] = {

    val summary = prinseq.summary

    totalNumberReads1(summary.numseqs.toString)     ::
    totalNumberReads2(summary.numseqs2.toString)    ::
    totalNumberBases1(summary.numbases.toString)    ::
    totalNumberBases2(summary.numbases2.toString)   ::
    filename1(summary.filename1)                    ::
    filename2(summary.filename2)                    :: Nil
  }


}


