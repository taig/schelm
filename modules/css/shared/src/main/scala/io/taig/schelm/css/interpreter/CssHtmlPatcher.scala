package io.taig.schelm.css.interpreter

import cats.data.{Ior, Kleisli}
import cats.implicits._
import cats.{Applicative, MonadError}
import io.taig.schelm.algebra.{Dom, Patcher}
import io.taig.schelm.css.data._
import io.taig.schelm.data.{HtmlAttachedReference, HtmlDiff}
import io.taig.schelm.interpreter.HtmlPatcher

object CssHtmlPatcher {
  def apply[F[_]: Applicative](
      html: Patcher[F, HtmlAttachedReference[F], HtmlDiff[F]],
      css: Patcher[F, Map[Selector, Style], List[CssDiff]]
  ): Patcher[F, StyledHtmlAttachedReference[F], CssHtmlDiff[F]] = Kleisli {
    case (x @ StyledHtmlAttachedReference(styles, reference), diff) =>
      diff.value match {
        case Ior.Left(diff)              => html.run(reference, diff).map(StyledHtmlAttachedReference(styles, _))
        case Ior.Right(diff)             => css.run((styles, diff.toList)).as(x)
        case Ior.Both(htmlDiff, cssDiff) => ??? // html.patch(nodes, htmlDiff) *> css.patch(styles, cssDiff.toList)
      }
  }

  def default[F[_]: MonadError[*[_], Throwable]](
      dom: Dom[F]
  ): Patcher[F, StyledHtmlAttachedReference[F], CssHtmlDiff[F]] =
    CssHtmlPatcher(HtmlPatcher.default(dom), Patcher.noop)
}
