package io.udash.routing

import io.udash.GState

trait Routing {
  type RoutingEngine[S >: Null <: GState[S]] = io.udash.routing.RoutingEngine[S]
  type UrlChangeProvider = io.udash.routing.UrlChangeProvider
}

