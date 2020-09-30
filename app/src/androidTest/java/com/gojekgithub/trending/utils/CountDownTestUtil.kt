package com.gojekgithub.trending.utils

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Count down latch which wait's for 2 seconds [CountDownLatch].
 * Added to make sure the tests run's slowly and user interaction could be seen
 */
object CountDownTestUtil {
    fun waitForUI(time: Long, timeUnit: TimeUnit = TimeUnit.SECONDS) {
        val latch = CountDownLatch(1)
        latch.await(time, timeUnit)
    }
}