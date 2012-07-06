organization := "me.lessis"

name := "tubesocks"

version := "0.1.0-SNAPSHOT"

libraryDependencies += "com.ning" % "async-http-client" % "1.7.5"

seq(lsSettings :_*)

libraryDependencies += "net.databinder" %% "unfiltered-netty-websockets" % "0.6.3" % "test"

libraryDependencies += "net.databinder" %% "unfiltered-spec" % "0.6.3" % "test"

libraryDependencies += "org.slf4j" % "slf4j-jdk14" % "1.6.2"

resolvers += "unfiltered-netty-websockets-resolver-0" at "https://oss.sonatype.org/content/repositories/releases"


