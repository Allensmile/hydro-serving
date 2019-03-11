package io.hydrosphere.serving.manager.domain.clouddriver

import cats.effect.Sync
import io.hydrosphere.serving.manager.config.CloudDriverConfiguration
import io.hydrosphere.serving.manager.domain.servable.Servable

final case class ServingInstance(
  name: String,
  id: String,
  host: String,
  port: Int
)

trait CloudDriver2[F[_]] {
  
  def instances: F[List[ServingInstance]]
  
  def instance(name: String, id: String): F[Option[ServingInstance]]
  
  def run(servable: Servable): F[ServingInstance]
  
  def remove(name: String, id: String): F[Unit]
}

object CloudDriver2 {
  
  def fromConfig[F[_]](config: CloudDriverConfiguration)(implicit F: Sync[F]): CloudDriver2[F] = {
     config match {
       case dockerConf: CloudDriverConfiguration.Docker =>
         val client = DockerdClient.create
         new DockerDriver2[F](client, dockerConf)
       case x =>
         throw new Exception(s"Not implemented for $x")
     }
  }
}