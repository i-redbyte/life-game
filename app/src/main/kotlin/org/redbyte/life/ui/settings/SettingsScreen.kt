package org.redbyte.life.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
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
import org.redbyte.life.ui.theme.baseDarkGray
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
            Text(
                stringResource(R.string.game_settings),
                color = greenSeaWave,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            NumberInputField(value = width, onValueChange = { width = it }, label = stringResource(R.string.field_width))
            NumberInputField(value = height, onValueChange = { height = it }, label = stringResource(R.string.field_height))
            NumberInputField(
                value = initialPopulation,
                onValueChange = { initialPopulation = it },
                label = stringResource(R.string.initial_population)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    bitmap = ImageBitmap.imageResource(id = R.drawable.ic_biohazard),
                    contentDescription = stringResource(R.string.compose_game),
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clickable {
                            val gameSettings =
                                GameSettings(
                                    width = width.toInt(),
                                    height = height.toInt(),
                                    initialPopulation = initialPopulation.toInt(),
                                )
                            viewModel.setupSettings(gameSettings)
                            navController.navigate("genomGame")
                        }
                )
                Image(
                    bitmap = ImageBitmap.imageResource(id = R.drawable.ic_biohazard2d),
                    contentDescription = stringResource(R.string.opengl_game),
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clickable { navController.navigate("openGLGame") }
                )
            }
        }
    }

}

@Composable
fun CheckboxWithText(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = greenSeaWave,
                uncheckedColor = baseDarkGray
            )
        )
        Text(text, color = baseWhite)
    }
}

@Composable
fun NumberInputField(value: String, onValueChange: (String) -> Unit, label: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = greenSeaWave) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.textFieldColors(
            textColor = baseWhite,
            backgroundColor = baseBlack,
            focusedIndicatorColor = greenSeaWave,
            unfocusedIndicatorColor = baseLightGray,
        )
    )
}
