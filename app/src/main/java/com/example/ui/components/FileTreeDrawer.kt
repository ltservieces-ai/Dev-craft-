package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectFileEntity
import com.example.ui.theme.*

@Composable
fun FileTreeDrawer(
    project: ProjectEntity,
    files: List<ProjectFileEntity>,
    currentFile: ProjectFileEntity?,
    onFileClick: (ProjectFileEntity) -> Unit,
    onBackToProjects: () -> Unit,
    onAddNewFile: () -> Unit,
    onDeleteFile: (String) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(IdeDarkSurface)
            .padding(top = 16.dp)
    ) {
        // Top Header of drawer matching Screenshot 8
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackToProjects) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Projects",
                        tint = IdeTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = project.name,
                    color = IdeTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row {
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Project Settings",
                        tint = IdeTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onAddNewFile) {
                    Icon(
                        imageVector = Icons.Default.CreateNewFolder,
                        contentDescription = "New File",
                        tint = IdeAccentPeach,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        HorizontalDivider(color = IdeDarkOutline, thickness = 1.dp)

        // File List matching hierarchy in Screenshot 8
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Project root folder
            item {
                FileTreeItem(
                    label = project.name,
                    icon = Icons.Default.Folder,
                    iconTint = IdeAccentPeach,
                    indent = 0,
                    isFolder = true,
                    isSelected = false,
                    onClick = {}
                )
            }

            // .acside folder
            item {
                FileTreeItem(
                    label = ".acside",
                    icon = Icons.Default.FolderOpen,
                    iconTint = IdeTextSecondary,
                    indent = 1,
                    isFolder = true,
                    isSelected = false,
                    onClick = {}
                )
            }

            // List of actual project files grouped
            items(files) { file ->
                val isSelected = currentFile?.id == file.id
                val (icon, tint) = getFileIconAndTint(file.relativePath)
                val displayName = file.relativePath.substringAfterLast('/')
                val depth = file.relativePath.count { it == '/' } + 1

                FileTreeItem(
                    label = displayName,
                    icon = icon,
                    iconTint = tint,
                    indent = depth.coerceAtMost(4),
                    isFolder = file.isDirectory,
                    isSelected = isSelected,
                    onClick = { onFileClick(file) },
                    onDelete = { onDeleteFile(file.relativePath) }
                )
            }
        }

        HorizontalDivider(color = IdeDarkOutline, thickness = 1.dp)

        // Bottom drawer icon bar matching Screenshot 8
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = "Files",
                tint = IdeAccentPeach,
                modifier = Modifier.size(22.dp)
            )
            Icon(
                imageVector = Icons.Default.Android,
                contentDescription = "Android",
                tint = IdeAccentGreen,
                modifier = Modifier.size(22.dp)
            )
            Icon(
                imageVector = Icons.Default.AltRoute,
                contentDescription = "Git",
                tint = IdeTextSecondary,
                modifier = Modifier.size(22.dp)
            )
            Icon(
                imageVector = Icons.Default.BugReport,
                contentDescription = "Debug",
                tint = IdeTextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun FileTreeItem(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    indent: Int,
    isFolder: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (indent * 12).dp)
            .background(
                if (isSelected) IdeDarkSurfaceVariant else Color.Transparent,
                RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isFolder) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = IdeTextTertiary,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(16.dp))
        }

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            color = if (isSelected) IdeAccentPeach else IdeTextPrimary,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        if (onDelete != null && !isFolder) {
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = IdeTextTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(IdeDarkSurfaceVariant)
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete", color = Color(0xFFF87171)) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFF87171))
                        }
                    )
                }
            }
        }
    }
}

fun getFileIconAndTint(path: String): Pair<ImageVector, Color> {
    return when {
        path.endsWith("AndroidManifest.xml") -> Pair(Icons.Default.Code, IdeAccentPeach)
        path.endsWith(".gradle") || path.endsWith(".gradle.kts") -> Pair(Icons.Default.Build, IdeAccentCyan)
        path.endsWith(".kt") -> Pair(Icons.Default.Code, IdeAccentPeach)
        path.endsWith(".java") -> Pair(Icons.Default.Coffee, Color(0xFFE57373))
        path.endsWith(".py") -> Pair(Icons.Default.Terminal, Color(0xFFECC94B))
        path.endsWith(".html") || path.endsWith(".js") || path.endsWith(".css") -> Pair(Icons.Default.Language, Color(0xFF4ADE80))
        path.endsWith(".c") || path.endsWith(".cpp") || path.endsWith(".h") -> Pair(Icons.Default.Memory, Color(0xFF60A5FA))
        path.endsWith(".xml") -> Pair(Icons.Default.Description, Color(0xFF818CF8))
        path.endsWith(".pro") || path.endsWith(".properties") -> Pair(Icons.Default.Settings, IdeTextSecondary)
        else -> Pair(Icons.Default.InsertDriveFile, IdeTextSecondary)
    }
}
