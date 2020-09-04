package io.taig.schelm.interpreter

import cats.effect.Concurrent
import io.taig.schelm.algebra.{EventManager, Schelm}
import io.taig.schelm.data.Html
import org.jsoup.nodes.{Element => JElement}

object JvmHtmlSchelm {
  def default[F[_]: Concurrent, Event]: Schelm[F, Html[Event], Event, JElement] = {
    val dom = JsoupDom.default[F, Event]
    val renderer = HtmlRenderer(dom)
    val differ = HtmlDiffer[Event]
    val patcher = HtmlPatcher(dom, renderer)
    DomSchelm(dom, EventManager.noop[F, Event], renderer, differ, patcher)
  }
}
