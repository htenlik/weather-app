package com.kampplus.hava.feature.weather.presentation.list.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kampplus.hava.R
import com.kampplus.hava.core.common.demo.DemoScenario

/**
 * Sahte veri kaynağının senaryosunu seçen menü (yalnızca debug). Menünün açık/kapalı olması ekrana ait
 * geçici bir durumdur, bu yüzden `remember` ile burada tutulur; seçimin kendisi ViewModel'e bildirilir.
 */
@Composable
fun DemoScenarioMenu(selected: DemoScenario, onSelect: (DemoScenario) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = stringResource(R.string.demo_menu))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DemoScenario.entries.forEach { scenario ->
                DropdownMenuItem(
                    text = { Text(text = stringResource(scenario.labelRes())) },
                    onClick = {
                        expanded = false
                        onSelect(scenario)
                    },
                    trailingIcon = if (scenario == selected) {
                        { Icon(imageVector = Icons.Filled.Check, contentDescription = null) }
                    } else {
                        null
                    }
                )
            }
        }
    }
}

private fun DemoScenario.labelRes(): Int = when (this) {
    DemoScenario.NORMAL -> R.string.demo_scenario_normal
    DemoScenario.SLOW -> R.string.demo_scenario_slow
    DemoScenario.EMPTY -> R.string.demo_scenario_empty
    DemoScenario.ERROR -> R.string.demo_scenario_error
}
