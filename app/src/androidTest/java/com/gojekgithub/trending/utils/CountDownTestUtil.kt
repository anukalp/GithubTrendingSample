package com.gojekgithub.trending.utils

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Count down latch which wait's for 2 seconds [CountDownLatch].
 * Added to make sure the tests run's slowly and user interaction could be seen
 */
object CountDownTestUtil {
    fun waitForUI() {
        val latch = CountDownLatch(1)
        latch.await(2, TimeUnit.SECONDS)
    }
}