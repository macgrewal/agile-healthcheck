package controllers

import javax.inject.{Inject, Singleton}
import models.Session
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SessionController @Inject()(components: ControllerComponents,
                                  val reactiveMongoApi: ReactiveMongoApi)
  extends AbstractController(components) with MongoController with ReactiveMongoComponents {

  implicit val ec: ExecutionContext = components.executionContext

  def sessionCollection: Future[JSONCollection] =
    database.map(_.collection[JSONCollection]("sessions"))

  def create() = Action { request =>

    request.body.asJson map { json =>
      val teamName: String = (json \ "teamName").as[String]
      val numberOfParticipants: Int = (json \ "numberOfParticipants").as[Int]

      val session = Session(java.util.UUID.randomUUID.toString, teamName, numberOfParticipants)
      createSession(session)
      Ok(Json.toJson(session))
    } getOrElse {
      BadRequest("poo")
    }
  }

  def createSession(session: Session): String = {
    sessionCollection.flatMap(_.insert(session))
    session.id
  }

}