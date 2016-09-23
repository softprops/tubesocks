organization := "me.lessis"

name := "tubesocks"

version := "0.1.1-SNAPSHOT"

description := "A comfortable and fashionable way to have bi-directional conversations with modern web servers"

libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.8.12",
  "org.slf4j" % "slf4j-nop" % "1.7.7")

libraryDependencies ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)){
  case Some((2, v)) if v <= 11 =>
    // workaround for cyclic dependency unfiltered <=> tubesocks
    Seq(
      "net.databinder" %% "unfiltered-netty-websockets" % "0.8.0" % "test",
      "net.databinder" %% "unfiltered-specs2" % "0.8.0" % "test"
    )
}.toList.flatten

// workaround for cyclic dependency unfiltered <=> tubesocks
// https://github.com/unfiltered/unfiltered/blob/v0.8.2/netty-websockets/build.sbt#L8
// TODO enable test for Scala 2.12
sources in Test := PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)){
  case Some((2, v)) if v <= 11 =>
    (sources in Test).value
  case _ =>
    Nil
}.toList.flatten

LsKeys.tags in LsKeys.lsync := Seq("websockets", "http")

seq(lsSettings :_*)

seq(buildInfoSettings:_*)

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](version)

buildInfoPackage := "tubesocks"

crossScalaVersions ++= Seq("2.10.4", "2.11.1", "2.12.0-RC1")

scalaVersion := crossScalaVersions.value.last

publishTo := Some(Opts.resolver.sonatypeStaging)

publishMavenStyle := true

publishArtifact in Test := false

licenses += ("MIT" ->
             url(s"https://github.com/softprops/${name.value}/blob/${version.value}/LICENSE"))

homepage := some(url("https://github.com/softprops/tubesocks/#readme"))

pomExtra := (
  <scm>
    <url>git@github.com:softprops/tubesocks.git</url>
    <connection>scm:git:git@github.com:softprops/tubesocks.git</connection>
  </scm>
  <developers>
    <developer>
      <id>softprops</id>
      <name>Doug Tangren</name>
      <url>https://github.com/softprops</url>
    </developer>
  </developers>)

