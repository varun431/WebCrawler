import akka.actor._

import spray.http._
import spray.client.pipelining._
import spray.httpx.encoding.Gzip

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
/**
  * Created by HaSh on 24-06-2016.
  */
class YoutubeCrawler(AccessToken: ActorRef) extends Actor {

  val api_key = "API_KEY"
  val video_id = "GLSPub4ydiM"
  val authorization_url = "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&maxResults=50&videoId=%s&key=%s"
  val authorization_url_string = String.format(authorization_url, video_id, api_key)
  val request = Get(authorization_url_string)

  def receive = {
    case "Ready" =>
      AccessToken ! getToken(request)

    case accesstoken =>

      val system: ActorSystem = ActorSystem()
      import system.dispatcher

      val pipeline: HttpRequest => Future[String] = (
        sendReceive
          ~> decode(Gzip)
          ~> unmarshal[String]
      )
      val response: Future[String] = pipeline(Get(authorization_url_string))
      val result = Await.result(response, 30.seconds)
      println(result)

  }
}
