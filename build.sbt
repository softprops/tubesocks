organization := "me.lessis"

name := "tubesocks"

version := "0.1.0"

description := "A comfortable and fashionable way to have bi-directional conversations with modern web servers"

libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.7.5",
  "net.databinder" %% "unfiltered-netty-websockets" % "0.6.3" % "test",
  "net.databinder" %% "unfiltered-spec" % "0.6.3" % "test",
  "org.slf4j" % "slf4j-jdk14" % "1.6.2")

LsKeys.tags in LsKeys.lsync := Seq("websockets", "http")

seq(lsSettings :_*)

crossScalaVersions ++= Seq(
  "2.8.0","2.8.1","2.9.0", "2.9.0-1", "2.9.1.RC1", "2.9.1", "2.9.2")

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

