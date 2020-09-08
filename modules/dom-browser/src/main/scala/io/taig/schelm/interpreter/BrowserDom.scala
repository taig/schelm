package io.taig.schelm.interpreter

import scala.scalajs.js

import cats.effect.implicits._
import cats.effect.{Effect, IO, Sync}
import cats.implicits._
import io.taig.schelm.algebra.{Dom, EventManager}
import io.taig.schelm.data.Listener
import io.taig.schelm.data.Listener.Action
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.HTMLInputElement

final class BrowserDom[F[_]: Effect](implicit F: Sync[F]) extends Dom[F] {
  override def addEventListener(node: dom.Node, name: String, listener: js.Function1[dom.Event, _]): F[Unit] =
    F.delay(node.addEventListener(name, listener))

  override def appendChild(parent: dom.Element, child: dom.Node): F[Unit] = F.delay(parent.appendChild(child)).void

  override def createElement(name: String): F[dom.Element] = F.delay(document.createElement(name))

  override def createElementNS(namespace: String, name: String): F[dom.Element] =
    F.delay(document.createElementNS(namespace, name))

  override def createTextNode(value: String): F[dom.Text] = F.delay(document.createTextNode(value))

  override def childAt(element: dom.Element, index: Int): F[Option[dom.Node]] =
    F.delay(Option(element.childNodes.apply(index)))

  override def children(element: dom.Element): F[List[dom.Node]] = F.delay {
    val result = collection.mutable.ListBuffer.empty[dom.Node]

    val childNodes = element.childNodes

    (0 until childNodes.length).foreach(index => result += childNodes.apply(index))

    result.toList
  }

  override def data(text: dom.Text, value: String): F[Unit] = F.delay(text.data = value)

  override def getAttribute(element: dom.Element, key: String): F[Option[String]] =
    F.delay(Option(element.getAttribute(key)).filter(_.nonEmpty))

  override def getElementById(id: String): F[Option[dom.Element]] =
    F.delay(Option(document.getElementById(id)))

  override val head: F[dom.Element] = F.delay(document.head)

  override def innerHtml(element: dom.Element, value: String): F[Unit] = F.delay(element.innerHTML = value)

  override def insertBefore(parent: dom.Element, node: dom.Node, reference: Option[dom.Node]): F[Unit] =
    F.delay(parent.insertBefore(node, reference.orNull)).void

  override def parentNode(node: dom.Node): F[Option[dom.Node]] = F.delay(Option(node.parentNode))

  override def removeAttribute(element: dom.Element, key: String): F[Unit] = F.delay(element.removeAttribute(key))

  override def removeChild(parent: dom.Element, child: dom.Node): F[Unit] = F.delay(parent.removeChild(child)).void

  override def removeEventListener(node: dom.Node, name: String, listener: js.Function1[dom.Event, _]): F[Unit] =
    F.delay(node.removeEventListener(name, listener))

  override def replaceChild(parent: dom.Element, current: dom.Node, next: dom.Node): F[Unit] =
    F.delay(parent.replaceChild(next, current)).void

  override def setAttribute(element: dom.Element, key: String, value: String): F[Unit] =
    F.delay(element.setAttribute(key, value))
}

object BrowserDom {
  def apply[F[_]: Effect, Event](manager: EventManager[F, Event]): Dom[F, Event] =
    new BrowserDom[F, Event](manager)
}
