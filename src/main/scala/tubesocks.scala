package tubesocks

import com.ning.http.client.{ AsyncHttpClient, AsyncHttpClientConfig }
import com.ning.http.client.websocket.{
  WebSocket, WebSocketTextListener, WebSocketUpgradeHandler }
import java.net.URI

case class Socket(underlying: WebSocket) {
  def send(s: String) = underlying.sendTextMessage(s)
  def open = underlying.isOpen
  def close = underlying.close
}

sealed trait Event
case class Message(text: String) extends Event
case class Open(socket: Socket) extends Event
case class Close(socket: Socket) extends Event 
case class Error(exception: Throwable) extends Event
case class Fragment(text: String) extends Event
case class EOF(text: String) extends Event

object Channel {

  type Handler = PartialFunction[Event, Any]

  object Listen {
    lazy val discard: Handler = {
      case e: Event => ()
    }
    def apply(pf: Handler) = {
      def complete(e: Event) = (pf orElse discard)(e)
      new WebSocketTextListener {
        def onMessage(m: String) = complete(Message(m))
        def onOpen(ws: WebSocket) = complete(Open(Socket(ws)))
        def onClose(ws: WebSocket) = complete(Close(Socket(ws)))
        def onError(t: Throwable) = complete(Error(t))
        def onFragment(fragment: String, last: Boolean) =
          complete(if (last) EOF(fragment) else Fragment(fragment))
      }
    }
  }

  def uri(str: String) =
    apply(new URI(if (str.startsWith("ws")) str else "ws://%s" format str))_

  def apply(uri: URI)(f: Handler) =
    Socket(mkClient.prepareGet(uri.toString)
     .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
       Listen(f)
      )
      .build())
      .get())

  private def mkClient =
    new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build())

}
