package dev.garage.rpm

import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import dev.garage.rpm.util.ConsumerWrapper
import dev.garage.rpm.util.bufferSingleValueWhileIdle

/**
 * Reactive property for the [view's][PmView] state.
 * Can be observed and changed in reactive manner with it's [observable] and [PresentationModel.consumer].
 *
 * Use to represent a view state. It can be something simple, like some widget's text, or complex,
 * like inProgress or data.
 *
 * @param [initialValue] initial value.
 * @param [diffStrategy] diff strategy.
 *
 * @see Action
 * @see Command
 */
class State<T> internal constructor(
    internal val pm: PresentationModel,
    initialValue: T? = null,
    private val diffStrategy: DiffStrategy<T>? = null
) {

    private val behaviorRelay: BehaviorSubject<T?> = BehaviorSubject(initialValue)
    private val _observable: Observable<T> = behaviorRelay.notNull()

    internal val relay = behaviorRelay

    /**
     * Observable of this [State].
     */
    val observable: Observable<T>
        get() {
            return if (diffStrategy != null) {
                if (diffStrategy.computeAsync()) {
                    _observable
                        .observeOn(computationScheduler)
                        .distinctUntilChanged(diffStrategy::areTheSame)
                } else {
                    _observable.distinctUntilChanged(diffStrategy::areTheSame)
                }
            } else {
                _observable
            }
        }

    /**
     * Returns a current value.
     * @throws UninitializedPropertyAccessException if there is no value and [State] was created without `initialValue`.
     */
    val value: T
        get() {
            return behaviorRelay.value
                ?: throw UninitializedPropertyAccessException("The State has no value yet. Use valueOrNull() or pass initialValue to the constructor.")
        }

    /**
     * Returns a current value or null.
     */
    val valueOrNull: T? get() = behaviorRelay.value


    /**
     * Returns true if the [State] has any value.
     */
    fun hasValue() = behaviorRelay.hasValue()
}

private val UNPROCESSED_ERROR_CONSUMER: ((Throwable) -> Unit) = {
    throw IllegalStateException(
        "Unprocessed error encountered in the State. " +
                "State accepts only emitted items, so you need to process errors yourself.",
        it
    )
}

/**
 * Creates the [State].
 * Optionally subscribes to the provided [state source][stateSource] and
 * unsubscribes from it ON [DESTROY][PresentationModel.Lifecycle.DESTROYED].
 *
 * @param [initialValue] initial value.
 * @param [diffStrategy] diff strategy.
 * @param [stateSource] source of the state.
 */
@Suppress("UNCHECKED_CAST")
fun <T> PresentationModel.state(
    initialValue: T? = null,
    diffStrategy: DiffStrategy<T>? = DiffByEquals as DiffStrategy<T>,
    stateSource: (() -> Observable<T>)? = null
): State<T> {

    val state = State(pm = this, initialValue = initialValue, diffStrategy = diffStrategy)

    if (stateSource != null) {
        lifecycleObservable
            .filter { it == PresentationModel.Lifecycle.CREATED }
            .take(1)
            .subscribe {
                stateSource.let { source ->
                    source()
                        .subscribe(
                            onNext = state.relay::onNext,
                            onError = UNPROCESSED_ERROR_CONSUMER
                        )
                        .untilDestroy()
                }
            }
    }

    return state
}

/**
 * Subscribes to the [State][State] and adds it to the subscriptions list
 * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
 * so use it ONLY in [PmView.onBindPresentationModel].
 */
fun <T> State<T>.bindTo(consumer: Consumer<in T>) {
    with(pm) {
        this@bindTo.observable
            .observeOn(mainScheduler)
            .bufferSingleValueWhileIdle(paused)
            .subscribe(consumer)
            .untilUnbind()
    }
}

/**
 * Subscribes to the [State][State] and adds it to the subscriptions list
 * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
 * so use it ONLY in [PmView.onBindPresentationModel].
 */
fun <T> State<T>.bindTo(consumer: (T) -> Unit) {
    with(pm) {
        this@bindTo.observable
            .observeOn(mainScheduler)
            .bufferSingleValueWhileIdle(paused)
            .subscribe(onNext = consumer)
            .untilUnbind()
    }
}

/**
 * Subscribes to the [State][State] and adds it to the subscriptions list
 * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
 * so use it ONLY in [PmView.onBindPresentationModel].
 */
fun <T> State<T>.bindTo(consumer: ConsumerWrapper<in T>) {
    with(pm) {
        this@bindTo.observable
            .observeOn(mainScheduler)
            .bufferSingleValueWhileIdle(paused)
            .subscribe(consumer)
            .untilUnbind()
    }
}

/**
 * Describes a strategy for comparing old and new values.
 *
 * It is used to optimize updates of the state[State] to avoid unnecessary UI redrawing.
 *
 * @see [DiffByEquals]
 * @see [DiffByReference]
 */
interface DiffStrategy<T> {

    /**
     * Compares the old and the new values.
     * @return [true] if both values ​​are identical or [false] if they are different.
     */
    fun areTheSame(new: T, old: T): Boolean

    /**
     * Defines a diff calculation on [the main thread][AndroidSchedulers.mainThread] or on [a computation thread][Schedulers.computation].
     */
    fun computeAsync(): Boolean
}

object DiffByEquals : DiffStrategy<Any> {

    override fun areTheSame(new: Any, old: Any) = new == old

    override fun computeAsync() = false
}

object DiffByReference : DiffStrategy<Any> {

    override fun areTheSame(new: Any, old: Any) = new === old

    override fun computeAsync() = false
}