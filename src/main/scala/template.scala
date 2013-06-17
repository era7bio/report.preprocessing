package era7.report.preprocessing

import era7.pandoc._

object PreprocessingTemplateOptions { 

  type me = PreprocessingTemplate

  case class sequencingProvider(value: String) extends TemplateVar[me]("sequencing_provider", value)
  case class sequencingTechnology(value: String) extends TemplateVar[me]("sequencing_technology", value)
  case class trimQualThreshold(value: String) extends TemplateVar[me]("trim_qual_threshold", value)
  case class meanQualThreshold(value: String) extends TemplateVar[me]("mean_qual_threshold", value)
  case class readsType(value: String) extends TemplateVar[me]("reads_type", value)
  case class totalNumberReads(value: String) extends TemplateVar[me]("total_number_reads", value)
  case class totalNumbeBases(value: String) extends TemplateVar[me]("total_number_bases", value)
  case class dataReceptionDate(value: String) extends TemplateVar[me]("data_reception_date", value)

  case class prefix(value: String) extends TemplateVar[me]("prefix", value)

  // should be boolean
  case class pairedEnd(value: String) extends TemplateVar[me]("paired_end", value)
  case class preprocessed(value: String) extends TemplateVar[me]("preprocessed", value)
}

case class PreprocessingTemplate(options: List[TemplateVar[PreprocessingTemplate]]) 
extends era7.pandoc.Template(
  name = "preprocessing.md.template",
  from = markdown,
  to = markdown
) {} 