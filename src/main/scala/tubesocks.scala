package tubesocks

import com.ning.http.client.{ AsyncHttpClient, AsyncHttpClientConfig }
import com.ning.http.client.websocket.{
  WebSocket, WebSocketTextListener, WebSocketUpgradeHandler }
import java.net.URI

trait Socket {
  def send(s: String): Unit
  def open: Boolean
  def close: Unit
  def apply(h: Channe.Handler)
}

class DefaultSocket(underlying: WebSocket) extends Socket {
  def send(s: String) =
    if (underlying.isOpen) underlying.sendTextMessage(s)
    else ()
  def open = underlying.isOpen
  def close = if (underlying.isOpen) underlying.close else ()
}

sealed trait Event
case class Message(text: String) extends Event
case class Open(socket: Socket) extends Event
case class Close(socket: Socket) extends Event 
case class Error(exception: Throwable) extends Event
case class Fragment(text: String) extends Event
case class EOF(text: String) extends Event

object Channel {
  /** A partial function signature for handing Socket events */
  type Handler = PartialFunction[Event, Any]

  object Listen {
    lazy val discard: Handler = {
      case e: Event => ()
    }
    def apply(pf: Handler) = {
      def complete(e: Event) = (pf orElse discard)(e)
      new WebSocketTextListener {
        def onMessage(m: String) = complete(Message(m))
        def onOpen(ws: WebSocket) = complete(Open(new DefaultSocket(ws)))
        def onClose(ws: WebSocket) = complete(Close(new DefaultSocket(ws)))
        def onError(t: Throwable) = complete(Error(t))
        def onFragment(fragment: String, last: Boolean) =
          complete(if (last) EOF(fragment) else Fragment(fragment))
      }
    }
  }

  /** URI factory for returning a websocket
   *  @param str string uri
   *  @return a function that takes a Handler and returns a Socket */
  def uri(str: String) =
    apply(new URI(if (str.startsWith("ws")) str else "ws://%s" format str))_

  /** Provides a means of customizing client configuration
   *  @param conf configuration building function
   *  @param uri websocket endpoint
   *  @param f Handler function */
  def configure(conf: AsyncHttpClientConfig.Builder => AsyncHttpClientConfig.Builder)
               (uri: URI)(f: Handler): Socket =
    new DefaultSocket(mkClient(conf(defaultConfig))
                      .prepareGet(uri.toString)
                      .execute(new WebSocketUpgradeHandler.Builder()
                               .addWebSocketListener(Listen(f))
                      .build())
                      .get())
  /** Default client-configured Socket
   *  @param uri websocket endpoint
   *  @param f Handler function */
  def apply(uri: URI)(f: Handler): Socket =
    configure(identity)(uri)(f)

  private def defaultConfig = new AsyncHttpClientConfig.Builder()

  private def mkClient(config: AsyncHttpClientConfig.Builder) =
    new AsyncHttpClient(config.build())
}
