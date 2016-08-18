import akka.actor._

import spray.http._
import spray.client.pipelining._
import spray.httpx.encoding.Gzip

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by HaSh on 21-06-2016.
  */

class FacebookCrawler(AccessToken: ActorRef) extends Actor{

  //val system = ActorSystem("FacebookCrawler")
  val access_token_URL = "https://graph.facebook.com/oauth/access_token?client_id=%s&client_secret=%s&grant_type=client_credentials"
  val owners_id = "100001239058000"
  val post_id = "993905987327360"
  val field = "likes"
  val URL = "https://graph.facebook.com/v2.6/%s/%s?summary=true&access_token=%s"
  val photoID = "1154624721222116"
  val appID = "APP_ID"
  val appSecret = "APP_SECRET"
  val commentsID = "1192927604072119"
  val url = String.format(access_token_URL, appID, appSecret)
  val request = Get(url)

  def receive = {
    case "Ready" =>
      AccessToken ! getToken(request)

    case accesstoken: String =>

      val system = ActorSystem()
      import system.dispatcher
      val accessToken = accesstoken.replaceAll(".*[=]", "")
      //println("ACCESS TOKEN: "+accessToken)

      //Extract Comments
      val commentsURL = String.format(URL, post_id, field, accessToken)
      //println("URL: "+commentsURL)

      val pipeline: HttpRequest => Future[String] = sendReceive ~> decode(Gzip) ~> unmarshal[String]
      val response: Future[String] = pipeline(Get(commentsURL))
      val result = Await.result(response, 30.seconds)
      println(result)
  }
  //Get http response
  /*var request = HttpRequest(HttpMethods.GET, Uri(url))
  var response: Future[HttpResponse] = (IO(Http) ? request).mapTo[HttpResponse]
  var result = Await.result(response, 30.seconds)
  println(result)*/
}