package com.gojekgithub.trending

import com.gojekgithub.trending.ui.main.MainActivityUiClickTest
import com.gojekgithub.trending.ui.main.MainActivityUiTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(MainActivityUiTest::class,
    MainActivityUiClickTest::class)
class EspressoTestSuite