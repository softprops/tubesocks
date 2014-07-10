# tubesocks

[![Build Status](https://travis-ci.org/softprops/tubesocks.svg?branch=master)](https://travis-ci.org/softprops/tubesocks)

A comfortable _and_ fashionable way to have bi-directional conversations with modern web servers.

Tubesocks is a snug little interface that wraps [async http client][ahc] which supports an emerging standard protocol for pushing
messages to clients and responding over an open connection.

Here is an `echo` client.

```scala
import tubesocks._
Sock.uri("ws://host") {
  case Message(m, s) => s.send(m)
}
```

## install

### sbt

By hand (cut & paste)

```scala
libraryDependencies += "me.lessis" %% "tubesocks" % "0.1.0"
```

The [civilized way](https://github.com/softprops/ls#readme)

    ls-install tubesocks

## usage

```scala
import tubesocks._
Sock.uri("ws://host.com") {
  case Open(s) => s.send("I'm here")
  case Message(t, s) => println("server says %s" format t)
  case Close(s) => println("we're done")
}
```

Do you prefer configuring your own transmission?

```scala
import tubesocks._
Sock.configure({ b =>
  b.setWebSocketIdleTimeoutInMs(2 * 60 * 1000)
})(new URI("ws://host.com")) {
  case Message(t, s) => s.send("thanks for the message")
}
```

Doug Tangren (softprops) 2012-2013

[ahc]: https://github.com/sonatype/async-http-client
