# tubesocks

A comfortable _and_ fashionable way to have bi-directional conversations with modern web servers.

## install



## usage

    import tubesocks._
    val s = Channel.uri("ws://host.com") {
      case Open(s) => s.send("I'm here")
      case Message(t, s) => println("server says %s" format t)
      case Close(s) => println("we're done")
    }
    
Doug Tangren (softprops) 2012
