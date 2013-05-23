package era7.template.preprocessing

import org.rogach.scallop._

case class AppConf(arguments: Seq[String]) extends ScallopConf(arguments) {

  version("preprocessing-report 0.1.0")

  val prefix = opt[String](
                            required = true, short = 'p',
                            descr = "this is the prefix you used when running prinseq"
                          )

  val output = opt[String](
                            required = false, short = 'o',
                            descr = "output file name"
                            // this is just the wrapped Option inside ScallopOption
                            // default = prefix.get
                          )



  val template = opt[String](
                              required = true, noshort = true,
                              descr = "template to be applied",
                              // pandoc will look for this inside your templates folder
                              default = Some("projects.preprocessing.template")
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

  val total_number_reads = opt[String](
                                        required = true, noshort = true,
                                        descr = "the total number of reads, counting from all inputs"
                                      )

  val total_number_bases = opt[String](
                                        required = true, noshort = true,
                                        descr = "the total number of bases, counting from all inputs"
                                      )

  val mean_qual_threshold =  opt[String](
                                          required = true, noshort = true,
                                          descr = "the mean quality score used to filter reads"
                                        )

  val trim_qual_threshold =  opt[String](
                                          required = true, noshort = true,
                                          descr = "the mean quality score used to trim reads on both ends"
                                        )

  // maybe add here raw report?

  val templateOpts =  sequencing_technology :: 
                      sequencing_provider ::
                      reads_type ::
                      data_reception_date :: 
                      total_number_reads ::
                      total_number_bases ::
                      mean_qual_threshold ::
                      trim_qual_threshold :: Nil

  // template list vals
  val input_files = opt[List[String]](
                                        required = true, noshort = true,
                                        descr = "a list of input files for this data set"
                                      )

  val input_files_links = opt[List[String]](
                                             required = true, noshort = true,
                                             descr = "links for each of the input files"
                                           )

  validate(input_files, input_files_links) { (a,b) =>
    if (a.length == b.length) Right(Unit)
    else Left("number of file links does not match number of files")
  }

  val templateListOpts =  input_files :: 
                          input_files_links :: Nil


}

object Main extends App {

  val conf = AppConf(args)

  conf.printHelp()
  println("prefix: "+ conf.prefix())
  println("list of input files: "+ conf.input_files( ))
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
            input: String,
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