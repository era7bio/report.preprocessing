package era7.report.preprocessing

import pandoc.TemplateVar

object preprocessingTemplate extends pandoc.Template(name = "preprocessing.md.template", from = "markdown", to = "markdown") {

  def pairedEndSummaryOpts(s: prinseq.PairedEndSummary): List[TemplateVar] = {

    TemplateVar("numseqs", s.numseqs.toString) ::
    TemplateVar("numbases", s.numbases.toString) ::
    TemplateVar("numseqs2", s.numseqs2.toString) ::
    TemplateVar("numbases2", s.numbases2.toString) ::
    TemplateVar("maxlength", s.maxlength.toString) ::
    TemplateVar("filename1", s.filename1) ::
    TemplateVar("filename2", s.filename2) :: Nil
  }

  def GCStats1Opts(gc: prinseq.GCStats): List[TemplateVar] = {

    TemplateVar("gcmin", gc.min.toString) ::
    TemplateVar("gcmax", gc.max.toString) ::
    TemplateVar("gcmean", gc.mean) ::
    TemplateVar("gcstd", gc.std) :: Nil
  }
}