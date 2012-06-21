# tubesocks

A comfortable _and_ fashionable way to have bi-directional conversations with modern web servers.

## install

## usage

    import tubesocks._
    Channel("ws://host.com") {
      case Open(s) => s.send("I'm here")
      case Message(t) => println("server says %s" format t)
      case Close(s) => println("we're done")
    }

Doug Tangren (softprops) 2012
