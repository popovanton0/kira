package com.popovanton0.kira.processortest.base

import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Similar to the rule Junit provides, but this one also holds class name.
 */
class TestNameRule : TestWatcher() {
    lateinit var methodName: String
        private set

    lateinit var className: String
        private set

    override fun starting(d: Description) {
        methodName = d.methodName
        className = d.className
    }
}