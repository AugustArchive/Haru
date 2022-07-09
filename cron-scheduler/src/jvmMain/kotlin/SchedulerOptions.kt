package dev.floofy.haru

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

public actual class SchedulerOptions {
    internal var _onError: ((Job<*>, Throwable) -> Unit)? = null
    private var _coroutineScope: CoroutineScope? = null
    private var _context: HaruContext = EmptyHaruContext

    @OptIn(DelicateCoroutinesApi::class)
    public actual var coroutineScope: CoroutineScope
        get() = _coroutineScope ?: GlobalScope
        set(value) {
            _coroutineScope = value
        }

    public actual var context: HaruContext
        get() = _context
        set(value) {
            _context = value
        }

    public actual fun onError(callable: (Job<*>, Throwable) -> Unit) {
        _onError = callable
    }
}
