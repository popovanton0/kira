package com.popovanton0.kira.suppliers.base

@RequiresOptIn(
    "This API uses reflection, which decreases UI performance. If this is acceptable, opt in. " +
            "Code generated by kira-processor should not use this API",
    level = RequiresOptIn.Level.WARNING
)
public annotation class ReflectionUsage
