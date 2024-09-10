package org.redbyte.life.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.redbyte.life.R
import org.redbyte.life.common.data.GameSettings
import org.redbyte.life.common.domain.*
import org.redbyte.life.ui.theme.*

@Composable
fun SettingsScreen(navController: NavHostController, viewModel: SharedGameSettingsViewModel) {
    var width by remember { mutableStateOf("32") }
    var height by remember { mutableStateOf("32") }
    var initialPopulation by remember { mutableStateOf("128") }
    var selectedRule by remember { mutableStateOf<Rule>(ClassicRule) } // Выбранное правило

    Surface(color = baseBlack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderTitle(text = stringResource(R.string.game_settings))
            Spacer(modifier = Modifier.height(16.dp))

            GameSettingsInputFields(
                width = width,
                height = height,
                initialPopulation = initialPopulation,
                onWidthChange = { width = it },
                onHeightChange = { height = it },
                onInitialPopulationChange = { initialPopulation = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RuleSelectionDropdown(selectedRule = selectedRule, onRuleSelected = { selectedRule = it })

            Spacer(modifier = Modifier.height(16.dp))

            GameSelectionButtons(
                width = width,
                height = height,
                initialPopulation = initialPopulation,
                selectedRule = selectedRule,  // Передаем выбранное правило
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun HeaderTitle(text: String) {
    Text(
        text = text,
        color = greenSeaWave,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun GameSettingsInputFields(
    width: String,
    height: String,
    initialPopulation: String,
    onWidthChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onInitialPopulationChange: (String) -> Unit
) {
    NumberInputField(value = width, onValueChange = onWidthChange, label = stringResource(R.string.field_width))
    NumberInputField(value = height, onValueChange = onHeightChange, label = stringResource(R.string.field_height))
    NumberInputField(value = initialPopulation, onValueChange = onInitialPopulationChange, label = stringResource(R.string.initial_population))
}

@Composable
fun RuleSelectionDropdown(selectedRule: Rule, onRuleSelected: (Rule) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val rules = listOf(
        ClassicRule,
        HighLifeRule,
        DayAndNightRule,
        MorleyRule,
        TwoByTwoRule,
        DiamoebaRule,
        LifeWithoutDeathRule,
        ReplicatorRule,
        SeedsRule
    )
    val ruleNames = listOf(
        stringResource(R.string.classic_rule),
        stringResource(R.string.highlife_rule),
        stringResource(R.string.day_and_night_rule),
        stringResource(R.string.morley_rule),
        stringResource(R.string.two_by_two_rule),
        stringResource(R.string.diamoeba_rule),
        stringResource(R.string.life_without_death_rule),
        stringResource(R.string.replicator_rule),
        stringResource(R.string.seeds_rule)
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = ruleNames[rules.indexOf(selectedRule)],
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(16.dp),
            fontWeight = FontWeight.Bold,
            color = baseWhite
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            rules.forEachIndexed { index, rule ->
                DropdownMenuItem(
                    text = {
                        Text(text = ruleNames[index])
                    },
                    onClick = {
                        onRuleSelected(rule)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun GameSelectionButtons(
    width: String,
    height: String,
    initialPopulation: String,
    selectedRule: Rule,
    navController: NavHostController,
    viewModel: SharedGameSettingsViewModel
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        GameButton(
            imageId = R.drawable.ic_biohazard,
            contentDescription = stringResource(R.string.compose_game),
            onClick = {
                viewModel.setupSettings(
                    GameSettings(
                        width = width.toInt(),
                        height = height.toInt(),
                        initialPopulation = initialPopulation.toInt(),
                        rule = selectedRule // Передаем выбранное правило
                    )
                )
                navController.navigate("genomGame")
            },
            modifier = Modifier.weight(1f)
        )
        GameButton(
            imageId = R.drawable.ic_biohazard2d,
            contentDescription = stringResource(R.string.opengl_game),
            onClick = { navController.navigate("openGLGame") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun GameButton(imageId: Int, contentDescription: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Image(
        bitmap = ImageBitmap.imageResource(id = imageId),
        contentDescription = contentDescription,
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() }
    )
}

@Composable
fun NumberInputField(value: String, onValueChange: (String) -> Unit, label: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = numberInputFieldColors(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun numberInputFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = baseWhite,
    unfocusedTextColor = baseWhite,
    cursorColor = greenSeaWave,
    focusedContainerColor = baseBlack,
    unfocusedContainerColor = baseBlack,
    focusedIndicatorColor = greenSeaWave,
    unfocusedIndicatorColor = baseLightGray,
    selectionColors = TextSelectionColors(
        handleColor = greenSeaWave,
        backgroundColor = baseGreen.copy(alpha = 0.3f)
    ),
    focusedLabelColor = greenSeaWave,
    unfocusedLabelColor = baseLightGray
)
