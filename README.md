# tubesocks

A comfortable _and_ fashionable way to have bi-directional conversations with modern web servers.

Tubesocks is a snug little interface that wraps [async http client][ahc] which supports an emerging standard protocol for pushing 
messages to clients and respoding over an open connection.

Here is an `echo` client.

    import tubesocks._
    Sock.uri("ws://host") { case Message(m, s) => s.send(m) }

## install



## usage

    import tubesocks._
    Sock.uri("ws://host.com") {
      case Open(s) => s.send("I'm here")
      case Message(t, s) => println("server says %s" format t)
      case Close(s) => println("we're done")
    }
    
Do you prefer configuring your own transmission?

    import tubesockets._
    Sock.configure({ b =>
      b.setWebSocketIdleTimeoutInMs(2 * 60 * 1000)
    })(new URI("ws://host.com")) {
      case Message(t, s) =>
        s.send("thanks for the message")
    }

Doug Tangren (softprops) 2012

[ahc]: https://github.com/sonatype/async-http-client
