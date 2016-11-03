package circetest

import java.time.ZonedDateTime

import scala.util.{Failure, Success, Try}

trait AggregateRoot {

  type Id
  type Event <: EventLike
  type State <: StateLike

  /**
    * The various (immutable) states that the aggregate root can have.
    */
  trait StateLike { this: State =>
    def id: Id
  }

  /**
    * An event, representing a transition from one [[State]] to another,
    * or leading to the initial [[State]] (see [[InitialEvent]])
    */
  trait EventLike { this: Event =>
    type TargetState <: State
    val timestamp: ZonedDateTime
  }

  /**
    * An event initiating the aggregate root.
    */
  trait InitialEvent extends EventLike { this: Event =>
    def create: TargetState
  }

  /**
    * An [[Event]] representing a transition from one [[State]] to another.
    */
  trait TransitionEvent extends EventLike { this: Event =>
    def isValidFrom(state: State): Boolean = {
      transition.isDefinedAt(state)
    }

    def applyOn(state: State): TargetState = {
      if (isValidFrom(state)) {
        transition(state)
      } else {
        throw new IllegalStateException(s"Cannot apply $this on $state")
      }
    }

    def transition: PartialFunction[State, TargetState]
  }

  def build(event: InitialEvent): Try[event.TargetState] = {
    build(List(event.asInstanceOf[Event])).asInstanceOf[Try[event.TargetState]]
  }

  /**
    * Create a state root out of a collection of events
    *
    * @return a state or a `Failure` if
    *         - the collection does not start with an `InitialEvent`
    *         - the collection is empty
    *         - an event cannot be applied (invalid transition)
    */
  def build(events: Traversable[Event]): Try[State] = {
    events.headOption match {
      case Some(event: InitialEvent) =>
        events.tail.foldLeft[Try[State]](Try(event.create)) {
          case (f: Failure[State], _) => f
          case (Success(state), event: TransitionEvent) if event.isValidFrom(state) => Try(event.applyOn(state))
          case (Success(state), event) => Failure(new IllegalStateException(s"Cannot transition from $state with $event"))
        }

      case Some(event) => Failure(new IllegalStateException(s"Cannot create state from $event"))
      case None => Failure(new NoSuchElementException("There are no events to create state root from"))
    }
  }
}