libraryDependencies += "com.ning" % "async-http-client" % "1.7.5"


seq(lsSettings :_*)

libraryDependencies += "net.databinder" %% "unfiltered-netty-websockets" % "0.6.3" % "test"

libraryDependencies += "net.databinder" %% "unfiltered-spec" % "0.6.3" % "test"

resolvers += "unfiltered-netty-websockets-resolver-0" at "https://oss.sonatype.org/content/repositories/releases"


