package com.popovanton0.kira

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.popovanton0.kira.suppliers.BooleanSupplierBuilder
import com.popovanton0.kira.suppliers.StringSupplierBuilder
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.boolean
import com.popovanton0.kira.suppliers.compound.*
import com.popovanton0.kira.suppliers.string
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test

internal class AutoGeneratedCompoundSupplier {

    public class TextCardScope : GeneratedKiraScopeWithImpls<TextCardScope.SupplierImplsScope>() {
        override val supplierImplsScope: SupplierImplsScope = SupplierImplsScope(this)

        public class SupplierImplsScope(private val scope: TextCardScope) :
            GeneratedKiraScopeWithImpls.SupplierImplsScope() {
            public var text: StringSupplierBuilder
                get() = scope.text as StringSupplierBuilder
                set(value) {
                    scope.text = value
                }
            public var isRed: BooleanSupplierBuilder
                get() = scope.isRed as BooleanSupplierBuilder
                set(value) {
                    scope.isRed = value
                }
            public var engine: CompoundSupplierBuilder<Engine, *>
                get() = scope.engine as CompoundSupplierBuilder<Engine, *>
                set(value) {
                    scope.engine = value
                }
        }

        public lateinit var text: Supplier<String>
        public lateinit var isRed: Supplier<Boolean>
        public lateinit var engine: Supplier<Engine>

        override fun collectSuppliers(): List<Supplier<*>> =
            listOf(text, isRed, engine)
    }

    public class EngineScope : GeneratedKiraScopeWithImpls<EngineScope.SupplierImplsScope>() {
        override val supplierImplsScope: SupplierImplsScope = SupplierImplsScope(this)

        public class SupplierImplsScope(private val scope: EngineScope) :
            GeneratedKiraScopeWithImpls.SupplierImplsScope() {
            public var model: StringSupplierBuilder
                get() = scope.model as StringSupplierBuilder
                set(value) {
                    scope.model = value
                }
            public var diesel: BooleanSupplierBuilder
                get() = scope.diesel as BooleanSupplierBuilder
                set(value) {
                    scope.diesel = value
                }
        }

        public lateinit var model: Supplier<String>
        public lateinit var diesel: Supplier<Boolean>

        override fun collectSuppliers(): List<Supplier<*>> =
            listOf(model, diesel)
    }

    data class Engine(
        val model: String = "Merlin",
        val diesel: Boolean = false,
    )

    @Composable
    public fun TextCard(
        text: String,
        isRed: Boolean,
        engine: Engine = Engine()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = text, color = if (isRed) Color.Red else Color.Unspecified)
            Text(text = if (isRed) "red" else "not red")
            Text(text = engine.toString())
        }
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createRoot() = kiraRoot(TextCardScope()) {
        text = string(paramName = "text", defaultValue = "Lorem")
        isRed = boolean(paramName = "isRed", defaultValue = false)
        engine = compound(
            scope = EngineScope(),
            paramName = "engine",
            label = "Engine",
        ) {
            model = string(paramName = "model", defaultValue = "Merlin")
            diesel = boolean(paramName = "diesel", defaultValue = false)
            injector {
                Engine(
                    model = model.currentValue(),
                    diesel = diesel.currentValue(),
                )
            }
        }

        injector {
            TextCard(
                text = text.currentValue(),
                isRed = isRed.currentValue(),
                engine = engine.currentValue()
            )
        }
    }

    @Test
    fun modify_default_value_of_one_of_the_suppliers(): Unit = with(composeTestRule) {
        val root = createRoot().modify {
            generatedSupplierImpls {
                text.defaultValue = "changed default value"
            }
        }
        setContent { KiraScreen(root) }
        onAllNodesWithText("changed default value").assertCountEquals(2) // card and text field

        onNodeWithText("isRed").assertExists()
    }

    @Test
    fun modify_default_value_of_one_of_the_suppliers_after_init(): Unit = with(composeTestRule) {
        var stringSupplier: StringSupplierBuilder? = null
        val root = createRoot().modify {
            generatedSupplierImpls {
                stringSupplier = text
            }
        }
        setContent { KiraScreen(root) }

        stringSupplier!!.defaultValue = "changed default value"
        // original value is still present
        onAllNodesWithText("Lorem").assertCountEquals(2) // card and text field

        onNodeWithText("isRed").assertExists()
    }

    @Test
    fun reassign_one_of_the_suppliers_with_the_new_impl_of_the_same_type(
    ): Unit = with(composeTestRule) {
        val root = createRoot().modify {
            generatedSupplierImpls {
                text = string("new text", defaultValue = "new default value")
            }
        }
        setContent { KiraScreen(root) }

        onNodeWithText("new text").assertExists()
        onAllNodesWithText("new default value").assertCountEquals(2) // card and text field

        onNodeWithText("isRed").assertExists()
    }

    val otherStringSupplier = object : Supplier<String> {
        @Composable
        override fun currentValue(): String = "new value"

        @Composable
        override fun Ui() = Text(text = "otherStringSupplier's UI")
    }

    @Test
    fun reassign_one_of_the_suppliers_with_the_new_impl_of_the_other_type(
    ): Unit = with(composeTestRule) {
        val root = createRoot().modify {
            text = otherStringSupplier
        }
        setContent { KiraScreen(root) }

        onNodeWithText("text").assertDoesNotExist()
        onNodeWithText("new value").assertExists()
        onNodeWithText("otherStringSupplier's UI").assertExists()

        onNodeWithText("isRed").assertExists()
    }

    /**
     * Calling [modify] after initialization is prohibited; initialization is performed in
     * [KiraScreen].
     */
    @Test
    fun reassign_one_of_the_suppliers_with_the_new_impl_of_the_other_type_after_init(
    ): Unit = with(composeTestRule) {
        val root = createRoot()
        setContent { KiraScreen(root) }

        assertThrows(Throwable::class.java) {
            root.modify {
                text = otherStringSupplier
            }
        }
        onNodeWithText("text").assertExists()
        onNodeWithText("new value").assertDoesNotExist()
        onNodeWithText("otherStringSupplier's UI").assertDoesNotExist()

        onNodeWithText("isRed").assertExists()
    }

    /**
     * This is en example of the wrong usage of the kira's API. It demonstrates, why it is wrong and
     * what happens in this case.
     */
    @Test
    fun reassign_one_of_the_suppliers_with_the_new_impl_of_the_other_type_after_init_maliciously(
    ): Unit = with(composeTestRule) {
        var maliciouslyExtractedScope: TextCardScope? = null

        val root = createRoot().modify { maliciouslyExtractedScope = this }
        setContent { KiraScreen(root) }

        with(maliciouslyExtractedScope!!) { text = otherStringSupplier }

        onNodeWithText("text").assertExists() // ui of the StringSupplier
        onAllNodesWithText("Lorem").assertCountEquals(2) // card and text field
        onNodeWithText("new value").assertDoesNotExist()

        onNodeWithText("isRed").assertExists()
            /**
             * This click triggers recomposition on the [injector]'s scope, which causes
             * `text.currentValue()` expression to be recalled again, but this time `text`
             * references [otherStringSupplier], causing "new value" to be displayed.
             *
             * But UI of the StringSupplier is still visible, because all suppliers were copied
             * into a list during initialization. Thus, [otherStringSupplier]'s UI is not
             * displayed.
             *
             * That is why it is prohibited to reassign vars from [GeneratedKiraScope]s AFTER
             * initialization (happens in [KiraScreen]).
             */
            .performClick()

        onNodeWithText("text").assertExists() // ui of the StringSupplier
        onAllNodesWithText("Lorem")
            .assertCountEquals(1) // only text field; ui of the StringSupplier is still present
        onNodeWithText("new value").assertExists()
    }

    @Test
    fun compound_in_compound(): Unit = with(composeTestRule) {
        val root = createRoot()
        setContent { KiraScreen(root) }

        onNodeWithText("diesel=false", substring = true).assertExists()
        onNodeWithText("diesel").performClick()
        onNodeWithText("diesel=true", substring = true).assertExists()
    }

    @Test
    fun reassign_one_of_the_suppliers_with_the_new_impl_of_the_other_type_and_modify_injector(
    ): Unit = with(composeTestRule) {
        val root = createRoot().modifyInjector { previousInjector ->
            text = otherStringSupplier
            injector {
                Column {
                    Text(text = "custom pre-header")
                    previousInjector()
                }
            }
        }
        setContent { KiraScreen(root) }

        onNodeWithText("custom pre-header").assertExists()
        onNodeWithText("text").assertDoesNotExist()
        onNodeWithText("new value").assertExists()
        onNodeWithText("otherStringSupplier's UI").assertExists()

        onNodeWithText("isRed").assertExists()
    }

    @Test
    fun reassign_one_of_the_suppliers_with_the_new_impl_of_the_other_type_and_replace_injector(
    ): Unit = with(composeTestRule) {
        val root = createRoot().modifyInjector { previousInjector ->
            text = otherStringSupplier
            injector {
                TextCard(text = text.currentValue().take(2), isRed = !isRed.currentValue())
            }
        }
        setContent { KiraScreen(root) }

        onNodeWithText("text").assertDoesNotExist() // replaced by otherStringSupplier's UI
        onNodeWithText("ne").assertExists() // customized value from otherStringSupplier
        onNodeWithText("otherStringSupplier's UI").assertExists()

        onNodeWithText("isRed").assertExists() // untouched supplier is still there
        // isRed's supplier value is inverted (default `false` -> `true`)
        onNodeWithText("red").assertExists()
    }
}
