package com.stanford.chatapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val theme by viewModel.theme.collectAsState()
    val geminiApiKey by viewModel.geminiApiKey.collectAsState()
    val openAiApiKey by viewModel.openAiApiKey.collectAsState()
    val xaiApiKey by viewModel.xaiApiKey.collectAsState()
    val geminiContextLengthLimit by viewModel.geminiContextLengthLimit.collectAsState()
    val openAiContextLengthLimit by viewModel.openAiContextLengthLimit.collectAsState()
    val xaiContextLengthLimit by viewModel.xaiContextLengthLimit.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ThemeSelector(
                selectedTheme = theme,
                onThemeSelected = { viewModel.setTheme(it) }
            )
            ApiKeyEditor(
                apiKeyName = "Gemini API Key",
                apiKey = geminiApiKey,
                onApiKeyChanged = { viewModel.setGeminiApiKey(it) }
            )
            ApiKeyEditor(
                apiKeyName = "Gemini Context Length Limit",
                apiKey = geminiContextLengthLimit.toString(),
                onApiKeyChanged = { viewModel.setGeminiContextLengthLimit(it.toIntOrNull() ?: 0) }
            )
            ApiKeyEditor(
                apiKeyName = "OpenAI API Key",
                apiKey = openAiApiKey,
                onApiKeyChanged = { viewModel.setOpenAiApiKey(it) }
            )
            ApiKeyEditor(
                apiKeyName = "OpenAI Context Length Limit",
                apiKey = openAiContextLengthLimit.toString(),
                onApiKeyChanged = { viewModel.setOpenAiContextLengthLimit(it.toIntOrNull() ?: 0) }
            )
            ApiKeyEditor(
                apiKeyName = "XAI API Key",
                apiKey = xaiApiKey,
                onApiKeyChanged = { viewModel.setXaiApiKey(it) }
            )
            ApiKeyEditor(
                apiKeyName = "XAI Context Length Limit",
                apiKey = xaiContextLengthLimit.toString(),
                onApiKeyChanged = { viewModel.setXaiContextLengthLimit(it.toIntOrNull() ?: 0) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelector(
    selectedTheme: String,
    onThemeSelected: (String) -> Unit
) {
    val themes = listOf("SYSTEM_DEFAULT", "LIGHT", "DARK")
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Theme")
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedTheme,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                themes.forEach { theme ->
                    DropdownMenuItem(
                        text = { Text(theme) },
                        onClick = {
                            onThemeSelected(theme)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ApiKeyEditor(
    apiKeyName: String,
    apiKey: String,
    onApiKeyChanged: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(apiKeyName)
        OutlinedTextField(
            value = apiKey,
            onValueChange = onApiKeyChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter your API key") }
        )
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(rememberNavController())
}
