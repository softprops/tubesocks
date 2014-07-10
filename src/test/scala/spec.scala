package tubesocks

import org.specs2.mutable._

object TubesocksSpec extends Specification
  with unfiltered.specs2.netty.Served {

  import unfiltered.netty.websockets.Planify
  import unfiltered.netty.websockets.{ Message, Text }

  def setup =
    _.handler(Planify({
      case _ => {
          case Message(s, Text(msg)) => s.send(msg)
        }
    }))

  "Socks" should {
    "receive messages" in {
      import java.util.concurrent.{ CountDownLatch, TimeUnit }
      var m = scala.collection.mutable.Map.empty[String, String]
      val l = new CountDownLatch(1)
      Sock.uri(host.to_uri.toString.replace("http", "ws")) {
        case tubesocks.Open(s) =>
          s.send("i'm open")
        case tubesocks.Message(t, _) =>
          m += ("rec" -> t)
          l.countDown
      }
      l.await(2, TimeUnit.MILLISECONDS)
      m must havePair(("rec", "i'm open"))
    }
  }
}
