package com.example.profilmahasiswa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.profilmahasiswa.ui.theme.ProfilMahasiswaTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data model untuk menyimpan data Mahasiswa
data class Mahasiswa(
    val nama: String,
    val nim: String,
    val prodi: String,
    val waktuSimpan: String = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProfilMahasiswaTheme {
                ProfilMahasiswaApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilMahasiswaApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // State Input Form
    var namaInput by rememberSaveable { mutableStateOf("") }
    var nimInput by rememberSaveable { mutableStateOf("") }
    var prodiInput by rememberSaveable { mutableStateOf("") }
    var prodiExpanded by remember { mutableStateOf(false) }

    val daftarProdi = listOf(
        "Informatika",
        "Sistem Informasi",
        "Teknik Komputer",
        "Teknologi Informasi",
        "Sains Data"
    )

    // State Data Profil Tersimpan
    var profilTersimpan by rememberSaveable { mutableStateOf<Mahasiswa?>(null) }

    // Logika Validasi Input
    // 1. Nama: minimal 3 karakter, hanya huruf alfabet dan spasi
    val isNamaValid = namaInput.trim().length >= 3 && namaInput.matches(Regex("^[a-zA-Z\\s'.]+$"))
    val namaError: String? = when {
        namaInput.isEmpty() -> null // Belum ada error saat pertama kali kosong
        namaInput.trim().length < 3 -> "Nama minimal 3 karakter"
        !namaInput.matches(Regex("^[a-zA-Z\\s'.]+$")) -> "Nama hanya boleh berisi huruf"
        else -> null
    }

    // 2. NIM: hanya angka, HARUS tepat 7 digit
    val isNimValid = nimInput.trim().length == 7 && nimInput.all { it.isDigit() }
    val nimError: String? = when {
        nimInput.isEmpty() -> null
        !nimInput.all { it.isDigit() } -> "NIM hanya boleh berisi angka"
        nimInput.trim().length < 7 -> "NIM harus tepat 7 digit (saat ini ${nimInput.length} digit)"
        nimInput.trim().length > 7 -> "NIM harus tepat 7 digit"
        else -> null
    }

    // 3. Program Studi: minimal 3 karakter / sudah dipilih
    val isProdiValid = prodiInput.trim().length >= 3
    val prodiError: String? = when {
        prodiInput.isEmpty() -> null
        prodiInput.trim().length < 3 -> "Silakan pilih Program Studi"
        else -> null
    }

    // Tombol Simpan hanya aktif jika SEMUA data valid
    val isFormValid = isNamaValid && isNimValid && isProdiValid

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionColor = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = "Mahasiswa",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "Formulir Data Mahasiswa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Lengkapi nama, NIM, dan program studi dengan benar agar tombol Simpan aktif.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Input 1: Nama Lengkap
            OutlinedTextField(
                value = namaInput,
                onValueChange = { namaInput = it },
                label = { Text("Nama Lengkap") },
                placeholder = { Text("Contoh: Budi Santoso") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Icon Nama",
                        tint = if (namaError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (namaInput.isNotEmpty()) {
                        IconButton(onClick = { namaInput = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Hapus Nama")
                        }
                    }
                },
                isError = namaError != null,
                supportingText = {
                    if (namaError != null) {
                        Text(
                            text = namaError,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (isNamaValid) {
                        Text(
                            text = "✓ Nama valid",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Input 2: NIM (tepat 7 digit)
            OutlinedTextField(
                value = nimInput,
                onValueChange = { input ->
                    // Membatasi hanya input angka dan maksimal 7 digit
                    if (input.all { it.isDigit() } && input.length <= 7) {
                        nimInput = input
                    }
                },
                label = { Text("Nomor Induk Mahasiswa (NIM - 7 Digit)") },
                placeholder = { Text("Contoh: 2110511") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Icon NIM",
                        tint = if (nimError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (nimInput.isNotEmpty()) {
                        IconButton(onClick = { nimInput = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Hapus NIM")
                        }
                    }
                },
                isError = nimError != null,
                supportingText = {
                    if (nimError != null) {
                        Text(
                            text = nimError,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (isNimValid) {
                        Text(
                            text = "✓ NIM valid (7 digit)",
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "${nimInput.length}/7 digit",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Input 3: Program Studi (Pilihan Dropdown & Opsi Cepat Informatika)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = prodiExpanded,
                    onExpandedChange = { prodiExpanded = !prodiExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = prodiInput,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilihan Program Studi") },
                        placeholder = { Text("Pilih Program Studi") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Icon Prodi",
                                tint = if (prodiError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = prodiExpanded)
                        },
                        isError = prodiError != null,
                        supportingText = {
                            if (prodiError != null) {
                                Text(
                                    text = prodiError,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else if (isProdiValid) {
                                Text(
                                    text = "✓ Program Studi dipilih: $prodiInput",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = prodiExpanded,
                        onDismissRequest = { prodiExpanded = false }
                    ) {
                        daftarProdi.forEach { prodi ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = prodi,
                                        fontWeight = if (prodi == prodiInput) FontWeight.Bold else FontWeight.Normal,
                                        color = if (prodi == prodiInput) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    prodiInput = prodi
                                    prodiExpanded = false
                                },
                                leadingIcon = {
                                    if (prodi == prodiInput) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                // Pilihan Cepat (Quick Chips)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pilihan cepat:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    daftarProdi.take(3).forEach { prodi ->
                        FilterChip(
                            selected = prodiInput == prodi,
                            onClick = { prodiInput = prodi },
                            label = { Text(prodi, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Tombol Simpan
            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()

                    val dataBaru = Mahasiswa(
                        nama = namaInput.trim(),
                        nim = nimInput.trim(),
                        prodi = prodiInput.trim()
                    )
                    profilTersimpan = dataBaru

                    // Tampilkan Snackbar
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Data mahasiswa atas nama ${dataBaru.nama} berhasil disimpan!",
                            actionLabel = "Tutup",
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                enabled = isFormValid,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isFormValid) "Simpan Data Mahasiswa" else "Lengkapi Data untuk Menyimpan",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Kartu Profil Mahasiswa (Tampil setelah data disimpan)
            AnimatedVisibility(
                visible = profilTersimpan != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
            ) {
                profilTersimpan?.let { mhs ->
                    CardProfil(
                        mahasiswa = mhs,
                        onHapusData = {
                            profilTersimpan = null
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Data profil telah dihapus.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

// Komponen Kartu Profil Bertema Hijau
@Composable
fun CardProfil(
    mahasiswa: Mahasiswa,
    onHapusData: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            // Header Kartu Profil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "KARTU MAHASISWA",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Status: Aktif",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Badge Tersimpan
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Tersimpan",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Konten Detail Mahasiswa
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Baris Nama
                ItemDetailProfil(
                    label = "Nama Lengkap",
                    value = mahasiswa.nama,
                    icon = Icons.Default.Person
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Baris NIM
                ItemDetailProfil(
                    label = "Nomor Induk Mahasiswa (NIM)",
                    value = mahasiswa.nim,
                    icon = Icons.Default.Info
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Baris Program Studi
                ItemDetailProfil(
                    label = "Program Studi",
                    value = mahasiswa.prodi,
                    icon = Icons.Default.DateRange
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Baris Waktu Simpan & Tombol Hapus
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Disimpan: ${mahasiswa.waktuSimpan}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    IconButton(
                        onClick = onHapusData,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Profil",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// Komponen Baris Detail Profil
@Composable
fun ItemDetailProfil(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Preview Android Studio
@Preview(showBackground = true, name = "Tampilan Terang")
@Composable
fun ProfilMahasiswaPreview() {
    ProfilMahasiswaTheme(darkTheme = false) {
        ProfilMahasiswaApp()
    }
}

@Preview(showBackground = true, name = "Preview Kartu Profil")
@Composable
fun CardProfilPreview() {
    ProfilMahasiswaTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CardProfil(
                mahasiswa = Mahasiswa(
                    nama = "Budi Santoso",
                    nim = "2110511",
                    prodi = "Informatika"
                ),
                onHapusData = {}
            )
        }
    }
}
