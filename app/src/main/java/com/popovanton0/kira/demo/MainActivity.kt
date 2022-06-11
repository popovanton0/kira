@file:Suppress("IllegalIdentifier")

package com.popovanton0.kira.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.demo.ui.theme.KiraTheme
import com.popovanton0.kira.generated.com.popovanton0.exampleui.Kira_SimpleTextCard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KiraTheme {
                Scaffold {
                    //KiraRegistryModificationExample()
                    //KiraScreen(KiraRegistry.kiraProviders.filterKeys { it.contains("asd") }.values.first())
                    //KiraScreen(`Kira_AsdQ😃∂`())
                    KiraScreen(Kira_SimpleTextCard().kira.modify {
                        generated {
                            this.rock.isNullByDefault = false
                            this.food.values
                        }
                    })
                }
            }
        }
    }
}

