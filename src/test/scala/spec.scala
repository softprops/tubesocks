package tubesocks

import unfiltered.netty.websockets.Planify
import unfiltered.netty.websockets.{ Message => UFMessage, Text }
import org.specs2.mutable.Specification
import scala.concurrent.Promise
import scala.concurrent.duration._

object TubesocksSpec extends Specification
  with unfiltered.specs2.netty.Served {

  def setup =
    _.handler(Planify({
      case _ => {
          case UFMessage(s, Text(msg)) => s.send(msg)
        }
    }))

  "Socks" should {
    "receive messages" in {
      val promise = Promise[String]()
      Sock.uri(host.to_uri.toString.replace("http", "ws")) {
        case tubesocks.Open(s) =>
          s.send("i'm open")
        case tubesocks.Message(t, _) =>
          if (!promise.isCompleted) {
            promise.success(t)
          }
      }
      promise.future must be_==("i'm open").await(
        retries = 5, Duration(2, MILLISECONDS)
      )
    }
  }
}
