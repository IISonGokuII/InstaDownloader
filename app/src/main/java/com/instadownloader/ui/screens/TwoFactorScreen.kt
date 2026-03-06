package com.instadownloader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.instadownloader.service.AuthResult
import com.instadownloader.ui.components.GlassCard
import com.instadownloader.ui.components.GradientButton
import com.instadownloader.ui.theme.subtleGradient
import com.instadownloader.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoFactorScreen(
    identifier: String,
    method: AuthResult.TwoFactorMethod,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val methodText = if (method == AuthResult.TwoFactorMethod.SMS) {
        "Bitte gib den 6-stelligen Code ein, den wir dir per SMS gesendet haben."
    } else {
        "Bitte gib den 6-stelligen Code aus deiner Authenticator-App ein."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(subtleGradient),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Zwei-Faktor-Authentifizierung",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = methodText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            code = it
                            errorMessage = null
                        }
                    },
                    label = { Text("6-stelliger Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                GradientButton(
                    text = "Bestätigen",
                    isLoading = isLoading,
                    enabled = code.length == 6,
                    onClick = {
                        isLoading = true
                        viewModel.submitTwoFactor(identifier, code) { result ->
                            isLoading = false
                            when (result) {
                                is AuthResult.Success -> onSuccess()
                                is AuthResult.Error -> errorMessage = result.message
                                else -> errorMessage = "Ein unerwarteter Fehler ist aufgetreten."
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onBack) {
                    Text("Zurück zum Login", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}