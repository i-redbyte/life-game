package org.redbyte.life.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import org.redbyte.life.ui.theme.baseBlack
import org.redbyte.life.ui.theme.baseGreen
import org.redbyte.life.ui.theme.baseLightGray
import org.redbyte.life.ui.theme.baseWhite
import org.redbyte.life.ui.theme.greenSeaWave

@Composable
fun SettingsScreen(navController: NavHostController, viewModel: SharedGameSettingsViewModel) {
    var width by remember { mutableStateOf("32") }
    var height by remember { mutableStateOf("32") }
    var initialPopulation by remember { mutableStateOf("128") }

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

            GameSelectionButtons(
                width = width,
                height = height,
                initialPopulation = initialPopulation,
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
fun GameSelectionButtons(
    width: String,
    height: String,
    initialPopulation: String,
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
                        initialPopulation = initialPopulation.toInt()
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
