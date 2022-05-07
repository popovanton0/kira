package com.popovanton0.kira.prototype1

import com.popovanton0.kira.prototype1.valueproviders.boolean
import com.popovanton0.kira.prototype1.valueproviders.nullableBoolean
import com.popovanton0.kira.prototype1.valueproviders.nullableString
import com.popovanton0.kira.prototype1.valueproviders.string

public class TextCardParams2(
    text: ValuesProviderProvider<String> = { string("123") },
    isRed: ValuesProviderProvider<Boolean> = { boolean(defaultValue = false) },
    textN: ValuesProviderProvider<String?> = { nullableString(null) },
    isRedN: ValuesProviderProvider<Boolean?> = { nullableBoolean(defaultValue = false) },
    //cornerRadius: ValuesProviderProvider<Dp>,
    public val functionCaller: (text: String, isRed: Boolean, textN: String?, isRedN: Boolean?) -> Unit = { _, _, _, _ -> },
) : FunctionParameters<Unit> {
    public val _text: ValuesProvider<String> = text(ParameterDetails(name = "text"))
    public val _isRed: ValuesProvider<Boolean> = isRed(ParameterDetails(name = "isRed"))
    public val _textN: ValuesProvider<String?> = textN(ParameterDetails(name = "textN"))
    public val _isRedN: ValuesProvider<Boolean?> = isRedN(ParameterDetails(name = "isRedN"))

    public override val valueProviders: List<ValuesProvider<*>> = listOf(
        _text,
        _isRed,
        _textN,
        _isRedN,
    )

    public override fun invoke(): Unit = functionCaller(
        _text.currentValue,
        _isRed.currentValue,
        _textN.currentValue,
        _isRedN.currentValue,
    )
}