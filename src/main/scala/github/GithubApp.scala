package github

import sttp.client._
import sttp.client.asynchttpclient.zio.{AsyncHttpClientZioBackend, SttpClient}
import zio.console.putStrLn
import zio.{App, ExitCode, ZIO}
import github.GithubClient.{ContributionsCollection, PullRequestContributionsByRepository, _}
import sttp.model.Header

object GithubApp extends App {

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {

    case class Repo(name: String)

    val pullRequest = PullRequestContributionsByRepository.repository(Repository.name).map(Repo)

    val query = Query.viewer(
      User.contributionsCollection(from = Some("2019-12-11T04:00:00Z")) {
        ContributionsCollection.pullRequestContributionsByRepository() {
          pullRequest
        }
      }
    )

    val uri = uri"https://api.github.com/graphql"

    SttpClient
      .send(query.toRequest(uri))
      .map(_.body)
      .absolve
      .tap(res => putStrLn(s"Result: $res"))
      .provideCustomLayer(AsyncHttpClientZioBackend.layer())
      .foldM(ex => putStrLn(ex.toString).as(ExitCode.failure), _ => ZIO.succeed(ExitCode.success))
  }
}
