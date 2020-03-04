package at.forsyte.apalache.tla.bmcmt.trex
import at.forsyte.apalache.tla.bmcmt.rewriter.Recoverable
import at.forsyte.apalache.tla.bmcmt.rules.aux.SparseOracle
import at.forsyte.apalache.tla.lir.TlaEx

/**
  * <p>A general-purpose symbolic transition executor (or T-Rex).
  * It accumulates the basic logic for executing TLA+
  * transitions, which is used both in the sequential and parallel model checkers.
  * (It could be used to implement predicate abstraction too.)
  * This class is imperative, as taking SMT snapshots and recovering them
  * in the non-incremental case is far from being functional.</p>
  *
  * <p>This class is parameterized by the type of an executor context. Currently, there are two choices:
  * (1) IncrementalExecutorContext and (2) OfflineExecutorContext.</p>
  *
  * @author Igor Konnov
  */
trait TransitionExecutor[ExecutorContextT] extends Recoverable[ExecutorSnapshot[ExecutorContextT]] {

  /**
    * The step that is currently encoded.
    */
  def stepNo: Int

  /**
    * Retrieve the translated symbolic execution
    *
    * @return the accumulated execution
    */
  def execution: ReducedExecution

  /**
    * Translate a transition into SMT and save it under the given number. This method returns false,
    * if the transition was found to be disabled during the translation. In this case, the translation result
    * is still saved in the SMT context. It is user's responsibility to pop the context, e.g., by recovering from
    * a snapshot. (In an incremental solver, it is cheap; in an offline solver, this may slow down the checker.)
    *
    * @param transitionNo a number associated with the transition, must be unique for the current step
    * @param transitionEx the expression that encodes the transition, it must be a TLA+ action expression
    * @return true, if the transition has been successfully translated;
    *         false, if the translation has found that the transition is disabled
    */
  def prepareTransition(transitionNo: Int, transitionEx: TlaEx): Boolean

  /**
    * Assume that a previously prepared transition fires. Use this method to check,
    * whether a prepared transition is enabled.
    * This method should be called after prepareTransition.
    *
    * @param transitionNo the number of a previously prepared transition
    */
  def assumeTransition(transitionNo: Int): Unit

  /**
    * Push an assertion about the current controlState.
    *
    * @param assertion a Boolean-valued TLA+ expression, usually a controlState expression,
    *                  though it may be an action expression.
    */
  def assertState(assertion: TlaEx): Unit

  /**
    * Pick non-deterministically one transition among the transitions that are prepared
    * in the current step. Further, assume that the picked transition has fired.
    * This method must be called after at least one call to prepareTransition.
    */
  def pickTransition(): SparseOracle

  /**
    * Advance symbolic execution by renaming primed variables to non-primed.
    * This method must be called after pickTransition.
    */
  def nextState(): Unit

  /**
    * Check, whether the current context of the symbolic execution is satisfiable.
    *
    * @param timeoutSec timeout in seconds
    * @return Some(true), if the context is satisfiable;
    *         Some(false), if the context is unsatisfiable;
    *         None, if the solver timed out or reported *unknown*.
    */
  def sat(timeoutSec: Long): Option[Boolean]
}