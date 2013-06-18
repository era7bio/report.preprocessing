package era7.report.preprocessing

import scalax.io._
import scalax.file._

object ResourceOps {
  
  def copyTo(resourceName: String, dest: Path) {

    val in_java: java.io.InputStream = getClass.getResourceAsStream("/"+ resourceName)
    val in = Resource.fromInputStream(in_java)

    dest.write(in.bytes)
  }
}