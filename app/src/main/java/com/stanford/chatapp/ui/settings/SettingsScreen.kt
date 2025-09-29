package com.stanford.chatapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    val selectedProvider by viewModel.selectedLlmProvider.collectAsState()

    val geminiApiKey by viewModel.geminiApiKey.collectAsState()
    val openAiApiKey by viewModel.openAiApiKey.collectAsState()
    val grokApiKey by viewModel.grokApiKey.collectAsState()

    val geminiContextLengthLimit by viewModel.geminiContextLengthLimit.collectAsState()
    val openAiContextLengthLimit by viewModel.openAiContextLengthLimit.collectAsState()
    val grokContextLengthLimit by viewModel.grokContextLengthLimit.collectAsState()

    val geminiModel by viewModel.geminiModel.collectAsState()
    val openAiModel by viewModel.openAiModel.collectAsState()
    val grokModel by viewModel.grokModel.collectAsState()

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

            ProviderSelector(
                providers = modelProviders.map { it.name },
                selectedProvider = selectedProvider,
                onProviderSelected = {
                    viewModel.setSelectedLlmProvider(it)
                }
            )

            when (selectedProvider) {
                "Gemini" -> {
                    ProviderSettings(
                        providerName = "Gemini",
                        apiKey = geminiApiKey,
                        onApiKeyChanged = { viewModel.setGeminiApiKey(it) },
                        contextLimit = geminiContextLengthLimit,
                        onContextLimitChanged = { viewModel.setGeminiContextLengthLimit(it) },
                        model = geminiModel,
                        onModelChanged = { viewModel.setGeminiModel(it) }
                    )
                }
                "OpenAI" -> {
                    ProviderSettings(
                        providerName = "OpenAI",
                        apiKey = openAiApiKey,
                        onApiKeyChanged = { viewModel.setOpenAiApiKey(it) },
                        contextLimit = openAiContextLengthLimit,
                        onContextLimitChanged = { viewModel.setOpenAiContextLengthLimit(it) },
                        model = openAiModel,
                        onModelChanged = { viewModel.setOpenAiModel(it) }
                    )
                }
                "Grok" -> {
                    ProviderSettings(
                        providerName = "Grok",
                        apiKey = grokApiKey,
                        onApiKeyChanged = { viewModel.setGrokApiKey(it) },
                        contextLimit = grokContextLengthLimit,
                        onContextLimitChanged = { viewModel.setGrokContextLengthLimit(it) },
                        model = grokModel,
                        onModelChanged = { viewModel.setGrokModel(it) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderSettings(
    providerName: String,
    apiKey: String,
    onApiKeyChanged: (String) -> Unit,
    contextLimit: Int,
    onContextLimitChanged: (Int) -> Unit,
    model: String,
    onModelChanged: (String) -> Unit
) {
    val standardModels = modelProviders.find { it.name == providerName }?.models ?: emptyList()
    val fullModelList = standardModels + "Custom"
    val isCustom = model !in standardModels
    val dropdownSelection = if (isCustom) "Custom" else model

    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ApiKeyEditor(
            apiKeyName = "API Key",
            apiKey = apiKey,
            onApiKeyChanged = onApiKeyChanged
        )
        ApiKeyEditor(
            apiKeyName = "Context Length Limit",
            apiKey = contextLimit.toString(),
            onApiKeyChanged = { onContextLimitChanged(it.toIntOrNull() ?: 0) }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Model")
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = dropdownSelection,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    fullModelList.forEach { modelName ->
                        DropdownMenuItem(
                            text = { Text(modelName) },
                            onClick = {
                                onModelChanged(modelName)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        if (dropdownSelection == "Custom") {
            OutlinedTextField(
                value = if (isCustom) model else "",
                onValueChange = onModelChanged,
                label = { Text("Custom Model Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderSelector(
    providers: List<String>,
    selectedProvider: String,
    onProviderSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("LLM Provider")
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedProvider,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                providers.forEach { provider ->
                    DropdownMenuItem(
                        text = { Text(provider) },
                        onClick = {
                            onProviderSelected(provider)
                            expanded = false
                        }
                    )
                }
            }
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
            placeholder = { Text("Enter your $apiKeyName") }
        )
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(rememberNavController())
}
