package com.example.npvcalculatorapp

import android.os.Bundle
package br.com.unicentro.npvcalculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.unicentro.npvcalculatorapp.ui.theme.NPVCalculatorAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NPVCalculatorAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NPVCalculatorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NPVCalculatorScreen() {
    var discountRateInput by remember { mutableStateOf("") }
    var cashFlowsInput by remember { mutableStateOf("") }
    var npvResult by remember { mutableStateOf<Double?>(null) }
    var history by remember { mutableStateOf(listOf<String>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculateNPV(discountRate: Double, cashFlows: List<Double>): Double {
        var npv = 0.0
        for (t in cashFlows.indices) {
            npv += cashFlows[t] / Math.pow(1 + discountRate, (t + 1).toDouble())
        }
        return npv
    }

    fun addToHistory(entry: String) {
        history = listOf(entry) + history
    }

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("NPV Calculator", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = discountRateInput,
                onValueChange = { discountRateInput = it },
                label = { Text("Discount Rate (e.g. 0.1 for 10%)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("0.1") },
                isError = errorMessage != null
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cashFlowsInput,
                onValueChange = { cashFlowsInput = it },
                label = { Text("Cash Flows (comma separated)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("1000, 2000, 3000") },
                isError = errorMessage != null
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val discountRate = discountRateInput.toDoubleOrNull()
                    val cashFlows = cashFlowsInput.split(",").mapNotNull { it.trim().toDoubleOrNull() }
                    if (discountRate == null || cashFlows.isEmpty()) {
                        errorMessage = "Please enter valid discount rate and cash flows."
                        npvResult = null
                    } else {
                        errorMessage = null
                        val npv = calculateNPV(discountRate, cashFlows)
                        npvResult = npv
                        addToHistory("Discount Rate: $discountRate, Cash Flows: $cashFlows, NPV: $npv")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate NPV")
            }

            Spacer(modifier = Modifier.height(16.dp))

            npvResult?.let {
                Text("NPV Result: $it", style = MaterialTheme.typography.headlineSmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Calculation History", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxHeight(0.4f)) {
                items(history) { entry ->
                    Text(entry, modifier = Modifier.padding(vertical = 4.dp))
                    Divider()
                }
            }
        }
    }
}
