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

  // length stats
  case class meanLength1(override val value: String)     extends TemplateVar[me]("mean_length_1", value)
  case class meanLength2(override val value: String)     extends TemplateVar[me]("mean_length_2", value)
  case class lengthStd1(override val value: String)     extends TemplateVar[me]("length_std_1", value)
  case class lengthStd2(override val value: String)     extends TemplateVar[me]("length_std_2", value)
  case class lengthMode1(override val value: String)     extends TemplateVar[me]("length_mode_1", value)
  case class lengthMode2(override val value: String)     extends TemplateVar[me]("length_mode_2", value)
  case class lengthModeValue1(override val value: String)     extends TemplateVar[me]("length_mode_value_1", value)
  case class lengthModeValue2(override val value: String)     extends TemplateVar[me]("length_mode_value_2", value)

  // Ns stats
  case class meanNs1(override val value: String)     extends TemplateVar[me]("mean_ns_1", value)
  case class meanNs2(override val value: String)     extends TemplateVar[me]("mean_ns_2", value)
  case class nsStd1(override val value: String)     extends TemplateVar[me]("ns_std_1", value)
  case class nsStd2(override val value: String)     extends TemplateVar[me]("ns_std_2", value)
  case class nsMode1(override val value: String)     extends TemplateVar[me]("ns_mode_1", value)
  case class nsMode2(override val value: String)     extends TemplateVar[me]("ns_mode_2", value)
  case class nsModeValue1(override val value: String)     extends TemplateVar[me]("ns_mode_value_1", value)
  case class nsModeValue2(override val value: String)     extends TemplateVar[me]("ns_mode_value_2", value)

  // GC stats
  case class meanGC1(override val value: String)     extends TemplateVar[me]("mean_gc_1", value)
  case class meanGC2(override val value: String)     extends TemplateVar[me]("mean_gc_2", value)
  case class gcStd1(override val value: String)     extends TemplateVar[me]("gc_std_1", value)
  case class gcStd2(override val value: String)     extends TemplateVar[me]("gc_std_2", value)
  case class gcMode1(override val value: String)     extends TemplateVar[me]("gc_mode_1", value)
  case class gcMode2(override val value: String)     extends TemplateVar[me]("gc_mode_2", value)
  case class gcModeValue1(override val value: String)     extends TemplateVar[me]("gc_mode_value_1", value)
  case class gcModeValue2(override val value: String)     extends TemplateVar[me]("gc_mode_value_2", value)
  
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