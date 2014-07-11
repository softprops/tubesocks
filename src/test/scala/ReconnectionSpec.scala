package tubesocks

import unfiltered.netty.websockets.Planify
import unfiltered.netty.websockets.{ Open => UFOpen }
import org.specs2.mutable.Specification
import java.net.URI
import scala.concurrent.Promise
import scala.concurrent.duration._

object ReconnectionSpec extends Specification
  with unfiltered.specs2.netty.Served {

  def setup =
    _.handler(Planify({
      case _ => {
        case UFOpen(ws) => ws.channel.close()
        }
    }))

  "Socks" should {
    "retry connections" in {
      val promise = Promise[Boolean]()
      Sock.reconnecting(times = 1)(new URI(host.to_uri.toString.replace("http", "ws"))) {
        case Close(_) => println("closed")
      }.onComplete {
        case _ =>
          promise.success(true)
      }
      promise.future must be_==(true).await(
        retries = 5, Duration(10, MILLISECONDS)
      )
    }
  }
}
