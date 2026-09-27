package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.ui.theme.*

data class CanvasWidget(
    var id: String,
    val type: String, // "TextView", "Button", "EditText", "ImageView", "CardView", "Linear(H)", "Linear(V)"
    var text: String = "",
    var textSize: Int = 16,
    var textColorHex: String = "#FFFFFF",
    var bgColorHex: String = "#2C2A36"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualDesignerScreen(
    project: ProjectEntity,
    onBack: () -> Unit,
    onRunBuild: () -> Unit,
    onOpenConfiguration: () -> Unit
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    var activeTab by remember { mutableStateOf("VIEW") }
    var selectedWidgetForEdit by remember { mutableStateOf<CanvasWidget?>(null) }

    val widgetsList = remember {
        mutableStateListOf(
            CanvasWidget(id = "tv_title", type = "TextView", text = "Welcome to ${project.name}", textSize = 22, textColorHex = "#F2F1F6", bgColorHex = "transparent"),
            CanvasWidget(id = "tv_subtitle", type = "TextView", text = "Drag & Drop Visual Mobile IDE (Beta)", textSize = 13, textColorHex = "#DCA683", bgColorHex = "transparent"),
            CanvasWidget(id = "et_input", type = "EditText", text = "Enter username or command...", textSize = 14, textColorHex = "#A19EAA", bgColorHex = "#1E1D24"),
            CanvasWidget(id = "btn_submit", type = "Button", text = "Tap Me / Run Action", textSize = 15, textColorHex = "#28180E", bgColorHex = "#DCA683")
        )
    }

    Scaffold(
        containerColor = Color(0xFF0288D1), // Sketchware iconic vibrant blue header
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            // Matching Screenshot 4 Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF0288D1))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(project.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text("Beta Drag-Drop • 601", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                }

                IconButton(onClick = { Toast.makeText(context, "Undo action", Toast.LENGTH_SHORT).show() }) {
                    Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = Color.White)
                }
                IconButton(onClick = { Toast.makeText(context, "Redo action", Toast.LENGTH_SHORT).show() }) {
                    Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo", tint = Color.White)
                }
                IconButton(onClick = { Toast.makeText(context, "Saved visual layout to main.xml", Toast.LENGTH_SHORT).show() }) {
                    Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.White)
                }
                IconButton(onClick = onOpenConfiguration) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                }
            }
        },
        bottomBar = {
            // Matching Screenshot 4 Bottom Bar: "main.xml", Equalizer, "Run"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(Color(0xFF0288D1))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { Toast.makeText(context, "Layout: res/layout/main.xml", Toast.LENGTH_SHORT).show() }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("main.xml ▾", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onOpenConfiguration) {
                        Icon(Icons.Default.Tune, contentDescription = "Configuration", tint = Color.White)
                    }
                    Button(
                        onClick = onRunBuild,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Run", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF1F5F9))
        ) {
            // Three Tabs: VIEW | EVENT | COMPONENT (Screenshot 4)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0288D1))
            ) {
                listOf("VIEW", "EVENT", "COMPONENT").forEach { tab ->
                    val isSel = activeTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeTab = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = tab,
                                color = if (isSel) Color.White else Color.White.copy(alpha = 0.6f),
                                fontSize = 13.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                            )
                            if (isSel) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height(3.dp)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }
            }

            when (activeTab) {
                "VIEW" -> {
                    // Two Pane: Left Palette & Center Phone Mockup (Screenshot 4)
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Left Palette
                        LazyColumn(
                            modifier = Modifier
                                .width(120.dp)
                                .fillMaxHeight()
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0)),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item { PaletteSectionHeader("Layouts") }
                            item { PaletteItem("Linear(H)", Icons.Default.ViewColumn) { widgetsList.add(CanvasWidget("lin_h_${widgetsList.size}", "Linear(H)", text = "Horizontal Container")) } }
                            item { PaletteItem("Linear(V)", Icons.Default.TableRows) { widgetsList.add(CanvasWidget("lin_v_${widgetsList.size}", "Linear(V)", text = "Vertical Container")) } }
                            item { PaletteItem("Scroll(V)", Icons.Default.SwapVert) { widgetsList.add(CanvasWidget("scroll_v_${widgetsList.size}", "Scroll(V)", text = "Scrollable Container")) } }

                            item { PaletteSectionHeader("AndroidX") }
                            item { PaletteItem("CardView", Icons.Default.FeaturedPlayList) { widgetsList.add(CanvasWidget("card_${widgetsList.size}", "CardView", text = "Card View Panel", bgColorHex = "#1E1D24")) } }
                            item { PaletteItem("TabLayout", Icons.Default.Tab) { widgetsList.add(CanvasWidget("tab_${widgetsList.size}", "TabLayout", text = "Tab 1 | Tab 2")) } }
                            item { PaletteItem("BottomNav", Icons.Default.ViewStream) { widgetsList.add(CanvasWidget("bnav_${widgetsList.size}", "BottomNavigationView", text = "Home • Search • Profile")) } }

                            item { PaletteSectionHeader("Widgets") }
                            item { PaletteItem("TextView", Icons.Default.Title) { widgetsList.add(CanvasWidget("tv_${widgetsList.size}", "TextView", text = "New Text Label", textColorHex = "#F2F1F6")) } }
                            item { PaletteItem("EditText", Icons.Default.Edit) { widgetsList.add(CanvasWidget("et_${widgetsList.size}", "EditText", text = "Input field...", textColorHex = "#A19EAA", bgColorHex = "#1E1D24")) } }
                            item { PaletteItem("Button", Icons.Default.SmartButton) { widgetsList.add(CanvasWidget("btn_${widgetsList.size}", "Button", text = "Action Button", textColorHex = "#28180E", bgColorHex = "#DCA683")) } }
                            item { PaletteItem("MaterialBtn", Icons.Default.RadioButtonChecked) { widgetsList.add(CanvasWidget("mbtn_${widgetsList.size}", "MaterialButton", text = "Material Elevated Button", textColorHex = "#FFFFFF", bgColorHex = "#38C779")) } }
                            item { PaletteItem("ImageView", Icons.Default.Image) { widgetsList.add(CanvasWidget("img_${widgetsList.size}", "ImageView", text = "🖼️ Image Asset Preview")) } }
                            item { PaletteItem("Switch", Icons.Default.ToggleOn) { widgetsList.add(CanvasWidget("sw_${widgetsList.size}", "Switch", text = "Enable Sensor Toggle")) } }
                            item { PaletteItem("CheckBox", Icons.Default.CheckBox) { widgetsList.add(CanvasWidget("cb_${widgetsList.size}", "CheckBox", text = "Option Selected Check")) } }
                        }

                        // Center Phone Mockup Canvas (Screenshot 4)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFFE2E8F0))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Phone Bezel Frame
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF121214))
                                    .border(3.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                            ) {
                                // Phone Status Bar
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0288D1))
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("main.xml", color = Color.White, fontSize = 11.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("📶 🔋 1:37", color = Color.White, fontSize = 11.sp)
                                    }
                                }

                                // Blue Toolbar
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .background(Color(0xFF0288D1))
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Toolbar", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }

                                // Interactive Canvas List
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(widgetsList) { widget ->
                                        CanvasWidgetView(
                                            widget = widget,
                                            onClick = { selectedWidgetForEdit = widget }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                "EVENT" -> {
                    // Event Logic Blocks
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text("Activity Life-cycle & Widget Events", color = Color(0xFF1E293B), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        items(listOf(
                            Triple("Activity", "onCreate", "Initializes views, loads saved state, sets dark theme"),
                            Triple("View", "btn_action.onClick", "Fires event, increments counter, shows Toast"),
                            Triple("Component", "Timer.onTick", "Updates UI every 1000ms for realtime events"),
                            Triple("Firebase", "onDataChange", "Syncs remote JSON data directly into view")
                        )) { (category, eventName, desc) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { Toast.makeText(context, "Editing logic blocks for $eventName", Toast.LENGTH_SHORT).show() },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (category == "Activity") Color(0xFF0288D1) else Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("$category: $eventName", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(desc, color = Color(0xFF64748B), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                "COMPONENT" -> {
                    // Components tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text("Add Non-Visual Components", color = Color(0xFF1E293B), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        items(listOf(
                            Pair("Intent", "Navigate between screens or open external URLs"),
                            Pair("SharedPreferences", "Save and load key-value settings locally"),
                            Pair("MediaPlayer", "Play audio files, music tracks, and haptics"),
                            Pair("Timer", "Run periodic countdowns or background events"),
                            Pair("Dialog", "Show custom alerts and user confirmations"),
                            Pair("FirebaseDB", "Connect to real-time Firebase cloud database")
                        )) { (compName, desc) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { Toast.makeText(context, "Added $compName component to project", Toast.LENGTH_SHORT).show() },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(compName, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(desc, color = Color(0xFF64748B), fontSize = 12.sp)
                                    }
                                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFF0288D1))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Widget Property Editor Dialog
    if (selectedWidgetForEdit != null) {
        val w = selectedWidgetForEdit!!
        var textVal by remember { mutableStateOf(w.text) }
        var idVal by remember { mutableStateOf(w.id) }

        AlertDialog(
            onDismissRequest = { selectedWidgetForEdit = null },
            title = { Text("Edit ${w.type} Properties", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = idVal,
                        onValueChange = { idVal = it },
                        label = { Text("Widget ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = textVal,
                        onValueChange = { textVal = it },
                        label = { Text("Display Text") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        w.text = textVal
                        w.id = idVal
                        selectedWidgetForEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    widgetsList.remove(w)
                    selectedWidgetForEdit = null
                }) {
                    Text("Delete Widget", color = Color.Red)
                }
            }
        )
    }
}

@Composable
fun PaletteSectionHeader(title: String) {
    Text(
        text = title,
        color = Color(0xFF0288D1),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, top = 10.dp, bottom = 4.dp)
    )
}

@Composable
fun PaletteItem(label: String, icon: ImageVector, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAdd() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, color = Color(0xFF1E293B), fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
fun CanvasWidgetView(
    widget: CanvasWidget,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (widget.bgColorHex == "transparent") Color.Transparent
                else Color(android.graphics.Color.parseColor(widget.bgColorHex))
            )
            .border(1.dp, Color(0xFF0288D1).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        when (widget.type) {
            "Button", "MaterialButton" -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(android.graphics.Color.parseColor(widget.bgColorHex)))
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(widget.text, color = Color(android.graphics.Color.parseColor(widget.textColorHex)), fontWeight = FontWeight.Bold)
                }
            }
            "EditText" -> {
                Text(widget.text, color = Color(android.graphics.Color.parseColor(widget.textColorHex)), fontFamily = FontFamily.Monospace, fontSize = widget.textSize.sp)
            }
            "ImageView" -> {
                Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                    Text("🖼️ [Image Asset View: ${widget.id}]", color = Color(0xFF38C779), fontSize = 13.sp)
                }
            }
            "Switch" -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(widget.text, color = Color.White, fontSize = 13.sp)
                    Switch(checked = true, onCheckedChange = {})
                }
            }
            "CheckBox" -> {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = true, onCheckedChange = {})
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(widget.text, color = Color.White, fontSize = 13.sp)
                }
            }
            else -> {
                Text(
                    text = widget.text,
                    color = Color(android.graphics.Color.parseColor(widget.textColorHex)),
                    fontSize = widget.textSize.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
