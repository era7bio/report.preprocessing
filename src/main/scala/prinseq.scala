package era7.report.preprocessing

import prinseq._
import scalax.file.Path


object prinseq {
  
  // json output .gd file
  // rep of what we want from there

  case class PairedEndSummary(
    numseqs: Int,
    numbases: Int,
    numseqs2: Int,
    numbases2: Int,
    maxlength: Int,
    filename1: String,
    filename2: String
  )

  case class GCStats(
    min: Int,
    max: Int,
    mean: String,
    std: String
  )

  case class LengthStats(
    min: Int,
    max: Int,
    mean: String,
    std: String
  )

  case class NsStats(
    min: Int,
    max: Int,
    mean: String,
    std: String
  )
}

case class PairedEndPrinseqOps(gd: Path) {

  import scalax.io._
  import scalax.file.Path
  import scalax.file.ImplicitConverters._
  import IOConf._
  import org.json4s._
  import org.json4s.jackson.JsonMethods._

  implicit val formats = DefaultFormats

  val jsonStr = gd.lines(includeTerminator = true).drop(2).fold("")(_ + _)
  val json = parse(jsonStr)

  def summary = {

    val extr = json.extract[PairedEndSummary]
    extr.copy(filename1 = toAscii(extr.filename1), filename2 = toAscii(extr.filename2))
  }

  def stats1 = stats(is2 = false)
  def stats2 = stats(is2 = true)

  private def stats(is2: Boolean): (GCStats, LengthStats, NsStats) = {

    val stats = if (is2) { json \\ "stats2" } else { json \\ "stats" }

    (
      (stats \\ "gc").extract[GCStats],
      (stats \\ "length").extract[LengthStats],
      (stats \\ "ns").extract[NsStats]
    )
  }

  private def toAscii(hex: String) = {
    require(hex.size % 2 == 0, 
      "Hex must have an even number of characters. You had " + hex.size)
    val sb = new StringBuilder
    for (i <- 0 until hex.size by 2) {
      val str = hex.substring(i, i + 2)
      sb.append(Integer.parseInt(str, 16).toChar)
    }
    sb.toString
  }
}

object testPrinseq extends App {

  // args(0) = path to gd file
  val ops = PairedEndPrinseqOps(Path(args(0)))
  
  println(ops.summary)
}