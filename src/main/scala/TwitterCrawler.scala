import akka.actor._

import spray.http.HttpHeaders.Authorization
import spray.http._
import spray.client.pipelining._
import spray.httpx.encoding.Gzip

import org.apache.commons.codec.binary.Base64
import org.json.JSONObject

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class TwitterCrawler(AccessToken: ActorRef) extends Actor {

  val ConsumerKey = "CONSUMER_KEY"
  val ConsumerSecret = "CONSUMER_SECRET"
  val search = "euro2016"
  val s = ConsumerKey + ":" + ConsumerSecret
  val encodedBytes = Base64.encodeBase64(s.getBytes())
  val authorization_header_string: String = new String(encodedBytes)
  val request = Post("https://api.twitter.com/oauth2/token?grant_type=client_credentials").withHeaders(Authorization(BasicHttpCredentials(authorization_header_string)))

  def receive = {
    case "Ready" =>
      AccessToken ! getToken(request)

    case accesstoken: String =>

      import system.dispatcher
      val system: ActorSystem = ActorSystem()


      val obj = new JSONObject(accesstoken)
      val access_token = obj.get("access_token").toString //obj.getJSONArray("access_token")
      //println("ACCESS TOKEN: " + access_token)

      val pipeline: HttpRequest => Future[String] = sendReceive ~> decode(Gzip) ~> unmarshal[String]
      val response: Future[String] = pipeline(Get("https://api.twitter.com/1.1/search/tweets.json?q=" + search).withHeaders(Authorization(OAuth2BearerToken(access_token))))
      val result = Await.result(response, 30.seconds)
      println(result)

  }
}
//HTTP post request(spray) with headers
/*var request = HttpRequest(HttpMethods.POST, Uri("https://api.twitter.com/oauth2/token?grant_type=client_credentials")).withHeaders(Authorization(BasicHttpCredentials(authorization_header_string)))
var response: Future[HttpResponse] = (IO(Http) ? request).mapTo[HttpResponse] //Future[HttpResponse] (spray)
var result = Await.result(response, 30.seconds) //HttpResponse (spray)*/

//val response: Future[String] = pipeline(Post("https://api.twitter.com/oauth2/token?grant_type=client_credentials").withHeaders(Authorization(BasicHttpCredentials(authorization_header_string))))
//val result = Await.result(response, 30.seconds)
//println(result)

//Alternative
//import spray.httpx.RequestBuilding._
//val response2: Future[HttpResponse] = (IO(Http) ? Get("https://api.twitter.com/1.1/search/tweets.json?q=euro2016&count=1").withHeaders(Authorization(OAuth2BearerToken(accessToken)))).mapTo[HttpResponse]
