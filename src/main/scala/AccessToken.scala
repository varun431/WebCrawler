import akka.actor._

import spray.http.HttpRequest
import spray.client.pipelining._
import spray.httpx.encoding.Gzip

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by HaSh on 23-06-2016.
  */

case class api(name: String)
case class getToken(request: HttpRequest)

class AccessToken(FacebookCrawler: ActorRef, TwitterCrawler: ActorRef, YoutubeCrawler: ActorRef) extends Actor {

  def receive = {

    case getToken(request) =>
      val system: ActorSystem = ActorSystem()
      import system.dispatcher

      val pipeline: HttpRequest => Future[String] = sendReceive ~> decode(Gzip) ~> unmarshal[String]
      val response: Future[String] = pipeline(request)
      val result = Await.result(response, 10.seconds)
      sender ! result

    case _ =>
      println("Invalid URL")
  }
}
