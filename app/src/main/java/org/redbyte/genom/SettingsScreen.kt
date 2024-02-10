package org.redbyte.genom

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.redbyte.genom.data.GameSettings

@Composable
fun SettingsScreen(navController: NavHostController, sharedViewModel: SharedGameViewModel) {
    val dialogState = remember { mutableStateOf(false) }
    var hasPacifists by remember { mutableStateOf(true) }
    var hasAggressors by remember { mutableStateOf(false) }
    var allowMutations by remember { mutableStateOf(false) }

    Surface(color = Color(0xFF1B1B1B)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Выберите типы клеток",
                color = Color.Green,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            CheckboxWithText(
                text = "Пацифисты",
                checked = hasPacifists,
                onCheckedChange = { checked ->
                    if (!checked && !hasAggressors) {
                        dialogState.value = true
                    } else {
                        hasPacifists = checked
                    }
                }
            )
            CheckboxWithText(
                text = "Агрессоры",
                checked = hasAggressors,
                onCheckedChange = { checked ->
                    if (!checked && !hasPacifists) {
                        dialogState.value = true
                    } else {
                        hasAggressors = checked
                    }
                }
            )
            if (hasPacifists && hasAggressors) {
                CheckboxWithText(
                    text = "Разрешить мутации",
                    checked = allowMutations,
                    onCheckedChange = { allowMutations = it }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    bitmap = ImageBitmap.imageResource(id = R.drawable.ic_biohazard),
                    contentDescription = "Compose Game",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clickable {
                            val gameSettings = GameSettings(hasPacifists, hasAggressors, allowMutations)
                            sharedViewModel.setGameSettings(gameSettings)
                            navController.navigate("genomGame")
                        }
                )
                Image(
                    bitmap = ImageBitmap.imageResource(id = R.drawable.ic_biohazard2d),
                    contentDescription = "OpenGL Game",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clickable { navController.navigate("openGLGame") }
                )
            }
        }
    }

    if (dialogState.value) {
        AlertDialog(
            onDismissRequest = { dialogState.value = false },
            confirmButton = {
                Button(onClick = { dialogState.value = false }) {
                    Text("ОК")
                }
            },
            text = { Text("Хотя бы один вид клеток должен быть выбран.") }
        )
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
                checkedColor = Color.Green,
                uncheckedColor = Color.DarkGray
            )
        )
        Text(text, color = Color.White)
    }
}


