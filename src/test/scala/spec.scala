package tubesocks

import org.specs._

object TubesocksSpec extends Specification
  with unfiltered.spec.netty.Served {

  import unfiltered.netty.websockets.Planify
  import unfiltered.netty.websockets.{ Message, Text }

  def setup =
    _.handler(Planify({
      case _ => {
          case Message(s, Text(msg)) => s.send(msg)
        }
    }))

  "Tubesocks" should {
    "recieve messages" in {
      import java.util.concurrent.CountDownLatch
      var m = scala.collection.mutable.Map.empty[String, String]
      val l = new CountDownLatch(1)
      Channel.uri(host.to_uri.toString.replace("http", "ws")) {
        case tubesocks.Open(s) =>
          s.send("i'm open")
        case tubesocks.Message(t) =>
          m += ("rec" -> t)
          l.countDown
      }
      l.await
      m must havePair(("rec", "i'm open"))
    }
  }
}
