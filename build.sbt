
import sbtrelease._
import ReleaseStateTransformations._

import preprocessingBuild._

name := "template-preprocessing"

organization := "era7"

scalaVersion := "2.10.1"

// crossScalaVersions := Seq("2.10.0.RC1", "2.10.0.RC2")

publishMavenStyle := false

publishTo <<= version { (v: String) =>
  if (v.trim.endsWith("-SNAPSHOT")) {
        Some(Resolver.file("local-snapshots", file("artifacts/snapshots.era7.com"))(Resolver.ivyStylePatterns))
  } else {
    Some(Resolver.file("local-releases", file("artifacts/releases.era7.com"))(Resolver.ivyStylePatterns))
  }
}

resolvers ++= Seq (
                    "Typesafe Releases"   at "http://repo.typesafe.com/typesafe/releases",
                    "Sonatype Releases"   at "https://oss.sonatype.org/content/repositories/releases",
                    "Sonatype Snapshots"  at "https://oss.sonatype.org/content/repositories/snapshots",
                    "Era7 Releases"       at "http://releases.era7.com.s3.amazonaws.com",
                    "Era7 Snapshots"      at "http://snapshots.era7.com.s3.amazonaws.com"
                  )

libraryDependencies ++= Seq (
                              "com.chuusai" %% "shapeless" % "1.2.3",
                              "org.rogach" %% "scallop" % "0.9.1",
                              "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.2",
                              "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2"
                            )

scalacOptions ++= Seq(
                      "-feature",
                      "-language:higherKinds",
                      "-language:implicitConversions",
                      "-deprecation",
                      "-unchecked"
                    )
