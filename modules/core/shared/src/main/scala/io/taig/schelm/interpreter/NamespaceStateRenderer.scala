package io.taig.schelm.interpreter

import cats.data.Kleisli
import cats.implicits._
import cats.{Applicative, Functor, Id}
import io.taig.schelm.algebra.{Renderer, StateManager}
import io.taig.schelm.data._
import io.taig.schelm.util.FunctionKs

import scala.annotation.tailrec

/** Render a `Namespace[State[X]]` into a `Namespace[X]` */
object NamespaceStateRenderer {
  def pure[F[_], G[_]: Functor](manager: StateManager[F])(
      prefix: Identification,
      snapshot: StateTree[Any],
      namespace: Fix[λ[A => Namespace[State[F, G[A]]]]]
  ): Fix[λ[A => Namespace[G[A]]]] =
    Fix[λ[A => Namespace[G[A]]]](namespace.unfix.mapWithIdentification { (identification, state) =>
      val data = identification.last.fold(snapshot)(snapshot.get(_).orEmpty)
      flatten(manager)(prefix ++ identification, data, state)
    })

  @tailrec
  private def flatten[F[_], G[_]: Functor](manager: StateManager[F])(
      identification: Identification,
      snapshot: StateTree[Any],
      state: State[F, G[Fix[λ[A => Namespace[State[F, G[A]]]]]]]
  ): G[Fix[λ[A => Namespace[G[A]]]]] = state match {
    case state: Stateful[F, _, G[Fix[λ[B => Namespace[State[F, G[B]]]]]]] =>
      val update = state.internalStateUpdate(manager, identification)
      val current = state.internalStateCurrent(snapshot.states)
      flatten(manager)(identification, snapshot, state.render(update, current))
    case state: Stateless[G[Fix[λ[B => Namespace[State[F, G[B]]]]]]] =>
      state.value.map(pure(manager)(identification, snapshot, _))
  }

  def id[F[_], G[_]: Functor](
      manager: StateManager[F]
  ): Renderer[Id, (Identification, StateTree[Any], Fix[λ[A => Namespace[State[F, G[A]]]]]), Fix[
    λ[B => Namespace[G[B]]]
  ]] =
    Kleisli[Id, (Identification, StateTree[Any], Fix[λ[A => Namespace[State[F, G[A]]]]]), Fix[λ[A => Namespace[G[A]]]]](
      (pure[F, G](manager) _).tupled
    )

  def apply[F[_]: Applicative, G[_]: Functor](
      manager: StateManager[F]
  ): Renderer[F, (Identification, StateTree[Any], Fix[λ[A => Namespace[State[F, G[A]]]]]), Fix[
    λ[A => Namespace[G[A]]]
  ]] =
    id[F, G](manager).mapK(FunctionKs.liftId[F])
}
