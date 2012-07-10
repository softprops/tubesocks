package tubesocks

import com.ning.http.client.{ AsyncHttpClient, AsyncHttpClientConfig }
import com.ning.http.client.websocket.{
  WebSocket, DefaultWebSocketListener, WebSocketUpgradeHandler }
import java.net.URI

// we are using the netty client
// the grizly client implements streaming
// consider this...
trait Socket {
  def send(s: String): Unit
  def open: Boolean
  def close: Unit
}

class DefaultSocket(underlying: WebSocket) extends Socket {
  def send(s: String) =
    if (underlying.isOpen) underlying.sendTextMessage(s)
    else ()
  def open = underlying.isOpen
  def close = if (underlying.isOpen) underlying.close else ()
  override def toString() = "%s(%s)" format(
    getClass().getName, if(open) "open" else "closed")
}

sealed trait Event
case class Message(text: String, socket: Socket) extends Event
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
      // note: we would just use the TextListener below BUT
      // it's very convenient to make #onMessage(m) respondable
      new DefaultWebSocketListener {
        override def onMessage(m: String) = complete(Message(m, new DefaultSocket(this.webSocket)))
        override def onOpen(ws: WebSocket) = complete(Open(new DefaultSocket(ws)))
        override def onClose(ws: WebSocket) = complete(Close(new DefaultSocket(ws)))
        override def onError(t: Throwable) = complete(Error(t))
        override def onFragment(fragment: String, last: Boolean) =
          complete(if (last) EOF(fragment) else Fragment(fragment))
      }
    }
  }

  /** URI factory for returning a websocket
   *  @param str string uri
   *  @return a function that takes a Handler and returns a Socket */
  def uri(str: String) =
    apply(new URI(if (str.startsWith("ws")) str else "ws://%s" format str))_

  /** Default client-configured Socket
   *  @param uri websocket endpoint
   *  @param f Handler function */
  def apply(uri: URI)(f: Handler): Socket =
    configure(identity)(uri)(f)

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

  private def defaultConfig =
    new AsyncHttpClientConfig.Builder()
      .setUserAgent("tubesocks/0.1.0")

  private def mkClient(config: AsyncHttpClientConfig.Builder) =
    new AsyncHttpClient(config.build())
}
