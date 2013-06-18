package era7.report.preprocessing

import era7.pandoc._

object PreprocessingTemplateOptions { 

  type me = PreprocessingTemplate.type

  case class sequencingProvider(override val value: String)    extends TemplateVar[me](name = "sequencing_provider", value)
  case class sequencingTechnology(override val value: String)  extends TemplateVar[me](name = "sequencing_technology", value)
  case class trimQualThreshold(override val value: String)     extends TemplateVar[me](name = "trim_qual_threshold", value)
  case class meanQualThreshold(override val value: String)     extends TemplateVar[me](name = "mean_qual_threshold", value)
  case class readsType(override val value: String)             extends TemplateVar[me](name = "reads_type", value)
  case class filename1(override val value: String)             extends TemplateVar[me](name = "filename_1", value)
  case class filename2(override val value: String)             extends TemplateVar[me]("filename_2", value)

  case class totalNumberReads1(override val value: String)     extends TemplateVar[me]("total_number_reads_1", value)
  case class totalNumberBases1(override val value: String)     extends TemplateVar[me]("total_number_bases_1", value)
  case class totalNumberReads2(override val value: String)     extends TemplateVar[me]("total_number_reads_2", value)
  case class totalNumberBases2(override val value: String)     extends TemplateVar[me]("total_number_bases_2", value)
  case class dataReceptionDate(override val value: String)     extends TemplateVar[me]("data_reception_date", value)

  case class prefix(override val value: String)                extends TemplateVar[me]("prefix", value)

  // should be boolean
  case class pairedEnd(override val value: String)             extends TemplateVar[me]("paired_end", value)
  case class preprocessed(override val value: String)          extends TemplateVar[me]("preprocessed", value)
}

object PreprocessingTemplate extends era7.pandoc.Template(
  name = "preprocessing.md.template",
  from = markdown,
  to = markdown
) {

  type TemplateOption = TemplateVar[PreprocessingTemplate.type]
} 