package ca.gbc.comp3074.qureshi_alia.roomitemslab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                ItemScreen()
            }
        }
    }
}

@Composable
fun ItemScreen() {

    val context = androidx.compose.ui.platform.LocalContext.current

    val database = remember {
        ItemDatabase.getDatabase(context)
    }

    val dao = database.itemDao()

    val itemList by dao.getAllItems()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Room Items",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Quantity") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {

                val quantityNumber = quantity.toIntOrNull()

                if (name.isNotBlank() && quantityNumber != null) {

                    scope.launch {

                        withContext(Dispatchers.IO) {
                            dao.insertItem(
                                Item(
                                    name = name,
                                    quantity = quantityNumber
                                )
                            )
                        }

                        name = ""
                        quantity = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Item")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Items",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            items(
                items = itemList,
                key = { it.id }
            ) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {

                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Quantity: ${item.quantity}"
                            )
                        }

                        Button(
                            onClick = {

                                scope.launch {

                                    withContext(Dispatchers.IO) {
                                        dao.deleteItem(item)
                                    }
                                }
                            }
                        ) {
                            Text("Remove")
                        }
                    }
                }
            }
        }
    }
}