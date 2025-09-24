package com.stanford.chatapp.ui.sessions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.stanford.chatapp.data.local.Session

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    navController: NavController,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val sessions by viewModel.sessions.collectAsState()
    var sessionToRename by remember { mutableStateOf<Session?>(null) }

    if (sessionToRename != null) {
        RenameSessionDialog(
            session = sessionToRename!!,
            onDismiss = { sessionToRename = null },
            onRename = { newTitle ->
                viewModel.renameSession(sessionToRename!!, newTitle)
                sessionToRename = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat Sessions") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.onNewSession()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "New Session")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(sessions) { session ->
                SessionItem(
                    session = session,
                    onSessionClicked = {
                        viewModel.onSessionSelected(session.id)
                        navController.popBackStack()
                    },
                    onDeleteClicked = { viewModel.onDeleteSession(session.id) },
                    onLongPress = { sessionToRename = session }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SessionItem(
    session: Session,
    onSessionClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onLongPress: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onSessionClicked,
                onLongClick = onLongPress
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(session.title)
            Text(session.timestamp.toString()) // TODO: Format this date
        }
        IconButton(onClick = onDeleteClicked) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Session")
        }
    }
}

@Composable
fun RenameSessionDialog(
    session: Session,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {
    var newTitle by remember { mutableStateOf(session.title) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Session") },
        text = {
            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                label = { Text("New Title") }
            )
        },
        confirmButton = {
            Button(onClick = { onRename(newTitle) }) {
                Text("Rename")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun SessionScreenPreview() {
    SessionScreen(rememberNavController())
}
