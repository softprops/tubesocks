organization := "me.lessis"

name := "tubesocks"

version := "0.1.1-SNAPSHOT"

description := "A comfortable and fashionable way to have bi-directional conversations with modern web servers"

libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.8.12",
  "net.databinder" %% "unfiltered-netty-websockets" % "0.8.0" % "test",
  "net.databinder" %% "unfiltered-specs2" % "0.8.0" % "test",
  "org.slf4j" % "slf4j-jdk14" % "1.6.2")

LsKeys.tags in LsKeys.lsync := Seq("websockets", "http")

seq(lsSettings :_*)

seq(buildInfoSettings:_*)

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](version)

buildInfoPackage := "tubesocks"

scalaVersion := "2.11.1"

crossScalaVersions ++= Seq("2.10.4", "2.11.1")

publishTo := Some(Opts.resolver.sonatypeStaging)

publishMavenStyle := true

publishArtifact in Test := false

licenses <<= (version)(v =>
      Seq("MIT" ->
          url("https://github.com/softprops/tubesocks/blob/%s/LICENSE" format v)))

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

