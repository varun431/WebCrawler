import akka.actor._

/**
  * Created by HaSh on 24-06-2016.
  */

object Test extends App {

  val system = ActorSystem()
  val accessToken = system.actorOf(Props(new AccessToken(facebookCrawler:ActorRef, twitterCrawler:ActorRef, youtubeCrawler:ActorRef)), name = "accessToken")
  val facebookCrawler:ActorRef = system.actorOf(Props(new FacebookCrawler(accessToken)), name = "facebookCrawler")
  val twitterCrawler:ActorRef = system.actorOf(Props(new TwitterCrawler(accessToken)), name = "twitterCrawler")
  val youtubeCrawler:ActorRef = system.actorOf(Props(new YoutubeCrawler(accessToken)), name = "youtubeCrawler")
  println("**********Facebook********\n")
  facebookCrawler ! "Ready"
  Thread.sleep(5000)
  println("\n**********Twitter********\n")
  twitterCrawler ! "Ready"
  Thread.sleep(5000)
  println("\n**********Youtube********\n")
  youtubeCrawler ! "Ready"
}
