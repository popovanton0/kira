package com.popovanton0.kira.ui

import com.popovanton0.kira.prototype1.SupplierBuilder

public class ReassignScope(private val map: MutableMap<String, Any>) {
    public var text: SupplierBuilder<String> by map
    public var isRed: SupplierBuilder<Boolean> by map
}