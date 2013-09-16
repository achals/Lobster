package com.achals.Lobster.Middleware

import akka.actor.{Actor, Props}

class ClientActor extends Actor {

    override def preStart(): Unit = {
    // create the greeter actor
    val greeter = context.actorOf(Props[Greeter], "greeter")
    // tell it to perform the greeting
    greeter ! Greeter.Greet
  }
 
  def receive = {
    // when the greeter is done, stop this actor and with it the application
    case Greeter.Done ⇒ context.stop(self)
  }

}

object Greeter {
  case object Greet
  case object Done
}
 
class Greeter extends Actor {
  def receive = {
    case Greeter.Greet ⇒
      println("Hello World!")
      sender ! Greeter.Done
  }
}