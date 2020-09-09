package io.taig.schelm.interpreter

import cats.effect.Sync
import cats.implicits._
import io.taig.schelm.algebra.Dom
import org.scalajs.dom
import org.scalajs.dom.{html, Event}

import scala.scalajs.js

final class BrowserDom[F[_]](implicit F: Sync[F]) extends Dom[F] {
  override type Node = dom.Node

  override type Element = dom.Element

  override type Text = dom.Text

  override type Document = html.Document

  override type Listener = js.Function1[dom.Event, _]

  override def addEventListener(node: Node, name: String, listener: js.Function1[Event, _]): F[Unit] =
    F.delay(node.addEventListener(name, listener))

  override def appendChild(parent: Element, child: Node): F[Unit] = F.delay(parent.appendChild(child)).void

  override def createElement(name: String): F[Element] = F.delay(document.createElement(name))

  override def createElementNS(namespace: String, name: String): F[Element] =
    F.delay(document.createElementNS(namespace, name))

  override def createTextNode(value: String): F[Text] = F.delay(document.createTextNode(value))

  override def childAt(element: Element, index: Int): F[Option[Node]] =
    F.delay(Option(element.childNodes.apply(index)))

  override def children(element: Element): F[List[Node]] = F.delay {
    val result = collection.mutable.ListBuffer.empty[Node]

    val childNodes = element.childNodes

    (0 until childNodes.length).foreach(index => result += childNodes.apply(index))

    result.toList
  }

  override def data(text: Text, value: String): F[Unit] = F.delay(text.data = value)

  override val document: Document = dom.document

  override def getAttribute(element: Element, key: String): F[Option[String]] =
    F.delay(Option(element.getAttribute(key)).filter(_.nonEmpty))

  override def getElementById(id: String): F[Option[Element]] =
    F.delay(Option(document.getElementById(id)))

  override val head: F[Element] = F.delay(document.head)

  override def innerHtml(element: Element, value: String): F[Unit] = F.delay(element.innerHTML = value)

  override def insertBefore(parent: Element, node: Node, reference: Option[Node]): F[Unit] =
    F.delay(parent.insertBefore(node, reference.orNull)).void

  override def parentNode(node: Node): F[Option[Node]] = F.delay(Option(node.parentNode))

  override def removeAttribute(element: Element, key: String): F[Unit] = F.delay(element.removeAttribute(key))

  override def removeChild(parent: Element, child: Node): F[Unit] = F.delay(parent.removeChild(child)).void

  override def removeEventListener(node: Node, name: String, listener: js.Function1[Event, _]): F[Unit] =
    F.delay(node.removeEventListener(name, listener))

  override def replaceChild(parent: Element, current: Node, next: Node): F[Unit] =
    F.delay(parent.replaceChild(next, current)).void

  override def setAttribute(element: Element, key: String, value: String): F[Unit] =
    F.delay(element.setAttribute(key, value))
}

object BrowserDom {
  def apply[F[_]: Sync]: Dom[F] = new BrowserDom[F]
}
