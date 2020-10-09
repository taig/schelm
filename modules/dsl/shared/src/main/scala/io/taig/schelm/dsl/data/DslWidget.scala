package io.taig.schelm.dsl.data

import io.taig.schelm.css.data.Css
import io.taig.schelm.data.{Listeners, Node, State, Widget}

sealed abstract class DslWidget[+F[_], -Context] extends Product with Serializable

object DslWidget {
  final case class Pure[F[_], Context](
      widget: Widget[Context, State[F, Css[Node[F, Listeners[F], DslWidget[F, Context]]]]]
  ) extends DslWidget[F, Context]

  abstract class Component[+F[_], -Context] extends DslWidget[F, Context] {
    def render: DslWidget[F, Context]
  }
}
