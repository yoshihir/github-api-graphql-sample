package config

import com.typesafe.config.ConfigFactory

case class GithubConfig(token: String)

class Config {
  private val config = ConfigFactory.load()

  def githubConfig: GithubConfig = {
    val githubConf = config.getConfig("github")
    GithubConfig(
      token = githubConf.getString("token")
    )
  }
}
