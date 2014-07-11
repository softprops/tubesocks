package tubesocks

import com.ning.http.client.{ AsyncHttpClient, AsyncHttpClientConfig }
import com.ning.http.client.websocket.{
  DefaultWebSocketListener, WebSocket, WebSocketListener, WebSocketUpgradeHandler }
import java.net.URI
import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration

/** A builder of sorts for WebSockets */
object Sock {
  /** A partial function signature for handing Socket events */
  type Handler = PartialFunction[Event, Any]

  object Listen {

    /** @return a future that will not be satisfied until */
    def apply[T](pf: Handler, closed: () => T): WebSocketListener = {
      def complete = pf.lift(_)
      new DefaultWebSocketListener {
        override def onMessage(m: String) =
          complete(Message(m, new DefaultSocket(this.webSocket)))
        override def onOpen(ws: WebSocket) =
          complete(Open(new DefaultSocket(ws)))
        override def onClose(ws: WebSocket) =
          complete(Close(new DefaultSocket(ws)))
          closed()
        override def onError(t: Throwable) =
          complete(Error(t))
        override def onFragment(fragment: String, last: Boolean) =
          complete(if (last) EOF(fragment) else Fragment(fragment))
        override def onPing(msg: Array[Byte]) =
          complete(Ping(msg))
        override def onPong(msg: Array[Byte]) =
          complete(Pong(msg))
      }
    }
  }

  def reconnecting
    (times: Int = 0, pausing: FiniteDuration = 0.seconds)(uri: URI)(f: Handler)(implicit ec: ExecutionContext): Future[Unit] =
     configure(identity)(times, pausing)(uri)(f)

  /** URI factory for returning a websocket
   *  @param str string uri
   *  @return a function that takes a Handler and returns a Socket */
  def uri(str: String)(f: Handler)(implicit ec: ExecutionContext) =
    apply(new URI(if (str.startsWith("ws")) str else s"ws://$str"))(f)

  /** Default client-configured Socket
   *  @param uri websocket endpoint
   *  @param f Handler function */
  def apply(uri: URI)(f: Handler)(implicit ec: ExecutionContext): Future[Unit] =
    configure(identity)()(uri)(f)

  /** Provides a means of customizing client configuration
   *  @param conf configuration building function
   *  @param uri websocket endpoint
   *  @param f Handler function */
  def configure
    (conf: AsyncHttpClientConfig.Builder => AsyncHttpClientConfig.Builder)
    (reconnectAttempts: Int = 0, pausing: FiniteDuration = 0.seconds)
    (uri: URI)(f: Handler)(implicit ec: ExecutionContext): Future[Unit] = {
      def listen = {
        val closer = Promise[Unit]()
        val listener = Listen(f, () => closer.success(()))
      
        new DefaultSocket(mkClient(conf(defaultConfig))
                          .prepareGet(uri.toString)
                          .execute(new WebSocketUpgradeHandler.Builder()
                                   .addWebSocketListener(listener)
                                   .build())
                          .get())
        closer.future
      }
      implicit val success: retry.Success[Unit] = new retry.Success(_ => false)
      import retry.Defaults.timer
      retry.Backoff(max = reconnectAttempts, delay = pausing)(() => listen)
    }

  private def defaultConfig =
    new AsyncHttpClientConfig.Builder()
      .setUserAgent("Tubesocks/%s" format BuildInfo.version)

  private def mkClient(config: AsyncHttpClientConfig.Builder) =
    new AsyncHttpClient(config.build())
}
