package com.achals.Lobster.Middleware

import akka.routing.ConsistentHashingRouter

class Router extends ConsistentHashingRouter {
	override val hashMapping:akka.routing.ConsistentHashingRouter.ConsistentHashMapping = Map.empty

}

case class Join(key:String)