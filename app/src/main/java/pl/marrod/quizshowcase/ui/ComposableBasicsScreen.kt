package pl.marrod.quizshowcase.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.marrod.quizshowcase.R
import pl.marrod.quizshowcase.ui.theme.QuizShowcaseTheme

/**
 * Ekran demonstracyjny (nie jest składnikiem aplikacji) podstawowych elementów
 * składowych Jetpack Compose.
 *
 * Każda sekcja prezentuje jedno zagadnienie w formie etykiety i interaktywnego przykładu:
 * 1. **Column** — układanie elementów w pionie
 * 2. **Row** — układanie elementów w poziomie
 * 3. **Box** — nakładanie / warstwowanie elementów
 * 4. **Modifier** — łańcuchowanie układu, wyglądu i zachowania
 * 5. **Card** — kontener treści z uniesieniem i opcjonalnym kliknięciem
 * 6. **Surface** — podstawowy kontener z kontrolą tła, kształtu i elewacji
 * 7. **Wynoszenie stanu** — przycisk licznika, którego stan żyje w WYWOŁUJĄCYM
 */
@Composable
fun ComposableBasicsScreen() {
    /** Stan `count` żyje tutaj, NIE wewnątrz [HoistedCounterCard] — to jest „wyniesiony" stan. */
    var count by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Composable Building Blocks", // celowo wpisane na sztywno, aby pokazać różnicę między hardcoded a string resources
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        SectionDivider("1 · Column")
        ColumnDemo()

        SectionDivider("2 · Row")
        RowDemo()

        SectionDivider("3 · Box")
        BoxDemo()

        SectionDivider("4 · Modifier")
        ModifierDemo()

        SectionDivider("5 · Card")
        CardDemo()

        SectionDivider("6 · Surface")
        SurfaceDemo()

        SectionDivider("7 · State Hoisting")
        /**
         * Przekazujemy zarówno wartość, jak i procedurę obsługi zdarzenia.
         * [HoistedCounterCard] nie wie, gdzie przechowywany jest `count` ani jak jest aktualizowany.
         */
        HoistedCounterCard(
            count = count,
            onIncrement = { count++ },
            onReset = { count = 0 }
        )
        /**
         * Przeciwnie do [HoistedCounterCard], ten CounterCard zarządza swoim własnym stanem wewnętrznym.
         * Nie jest to zalecane, jeśli chcesz ponownie używać tego composable z różnymi wartościami `count`
         * lub udostępniać jego stan do innych elementów UI.
         */
        CounterCard()
    }
}

/**
 * [Column] układa dzieci jedno pod drugim (oś główna = pionowa).
 *
 * Kluczowe parametry:
 * - `verticalArrangement` — odstępy i wyrównanie wzdłuż osi pionowej
 * - `horizontalAlignment` — wyrównanie dzieci na osi poziomej
 *
 * ### Modifier użyte w tym przykładzie
 * - `.fillMaxWidth()` — rozszerza Column do pełnej dostępnej szerokości
 * - `.background()` — wypełnia tłem w kolorze `surfaceVariant`, aby granica była widoczna
 * - `.padding()` — dodaje wewnętrzny odstęp między krawędzią Column a jej dziećmi
 */
@Composable
fun ColumnDemo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            /*      .fillMaxSize()*/
            .fillMaxWidth()
            /*.fillMaxHeight()*/
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        /** 8 dp odstępu między każdym dzieckiem */
        horizontalAlignment = Alignment.CenterHorizontally
        /** dzieci wyśrodkowane poziomo */
    ) {
        Text("First item", style = MaterialTheme.typography.bodyMedium)
        Text(
            "Second item",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.border(2.dp, Color.Red)
        )

        /**
         * Przycisk wewnątrz kolumny.
         * `Modifier.fillMaxWidth()` rozciąga przycisk na pełną szerokość Column.
         */
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Full-width button")
        }

        /**
         * Obraz wczytany z zasobu wektorowego przez `painterResource`.
         *
         * **Zasoby:** `R.drawable.ic_quiz_trophy` — rozwiązywany przez klasę R w czasie wykonania.
         * `.align(Alignment.End)` nadpisuje domyślne `CenterHorizontally` Column dla tego dziecka.
         */
        Image(
            painter = painterResource(R.drawable.ic_quiz_trophy),
            contentDescription = "Trophy",
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.End)
        )

    }
}

/**
 * [Row] układa dzieci obok siebie (oś główna = pozioma).
 *
 * Kluczowe parametry:
 * - `horizontalArrangement` — odstępy i wyrównanie wzdłuż osi poziomej
 * - `verticalAlignment` — wyrównanie dzieci na osi pionowej
 *
 * ### Modifier `.weight(1f)`
 * Użycie `.weight(1f)` na dziecku Row sprawia, że wypełnia ono pozostałą przestrzeń,
 * podczas gdy pozostałe dzieci zachowują swój naturalny rozmiar.
 * W tym przykładzie nie jest użyte (aby zachować czystość dema `SpaceBetween`),
 * ale jest pokazane jako komentarz.
 *
 * Przykład: `Text("...", modifier = Modifier.weight(1f))`
 */
@Composable
fun RowDemo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        /** przesuwa dzieci na przeciwne końce */
        verticalAlignment = Alignment.CenterVertically
        /** wyrównuje dzieci do środkowej linii */
    ) {
        /** Ikona z Material Icons — przechowywana w pamięci, bez klasy R. */
        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = "Star",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )

        Text(
            text = "Centred label",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Button(onClick = {}) {
            Text("Go")
        }
    }
}

/**
 * [Box] nakłada dzieci na siebie (warstwami).
 * Ostatnie narysowane dziecko jest na wierzchu. Każde dziecko może pozycjonować się
 * za pomocą `Modifier.align(Alignment.*)` względem granic Box.
 *
 * Warstwy w tym przykładzie:
 * - **Warstwa 1** — etykieta w lewym dolnym rogu (rysowana pierwsza = najdalej w tle)
 * - **Warstwa 2** — wyśrodkowany obraz (rysowany drugi = na wierzchu warstwy 1)
 * - **Warstwa 3** — odznaka w prawym górnym rogu (rysowana ostatnia = na wierzchu wszystkiego)
 */
@Composable
fun BoxDemo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        /** Warstwa 1 — etykieta w lewym dolnym rogu (rysowana pierwsza = najdalej w tle). */
        Text(
            text = "Background layer",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        )

        /** Warstwa 2 — wyśrodkowany obraz (rysowany drugi = na wierzchu warstwy 1). */
        Image(
            painter = painterResource(R.drawable.ic_quiz_trophy),
            contentDescription = "Trophy",
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.Center)
        )

        /**
         * Warstwa 3 — odznaka w prawym górnym rogu (rysowana ostatnia = na wierzchu wszystkiego).
         *
         * `Modifier.clip(CircleShape)` zmienia kształt obszaru rysowania przed wypełnieniem tłem.
         */
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.error),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "!",
                color = MaterialTheme.colorScheme.onError,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black
            )
        }
    }
}

/**
 * Demonstracja działania **Modifier** i znaczenia kolejności w łańcuchu.
 *
 * [Modifier] to uporządkowany łańcuch instrukcji stosowanych do composable.
 * **Kolejność ma znaczenie:** `.padding().background() ≠ .background().padding()`
 *
 * Przykład łańcucha:
 * ```
 * Modifier
 *   .size(80.dp)       — ogranicza rozmiar
 *   .clip(shape)       — przycina do kształtu (stosowane przed tłem)
 *   .background(color) — wypełnia przyciętą powierzchnię kolorem
 *   .padding(8.dp)     — wewnętrzny odstęp (stosowany wewnątrz przyciętego obszaru)
 * ```
 *
 * Przypadki w tym przykładzie:
 * - **A** `padding → background` — obszar padding NIE jest pokolorowany
 * - **B** `background → padding` — tło pokrywa obszar padding
 * - **C** `clip + background`    — pokazuje wpływ clip na kształt wypełnienia
 *
 * @param modifier opcjonalny Modifier przekazywany przez wywołującego
 */
@Composable
fun ModifierDemo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        /** A: padding na ZEWNĄTRZ tła — obszar padding NIE jest pokolorowany. */
        Text(
            text = "A: padding → background",
            modifier = Modifier
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .padding(12.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(8.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        /** B: tło NAJPIERW, potem padding — tło pokrywa obszar padding. */
        Text(
            text = "B: background → padding",
            modifier = Modifier
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(12.dp)
                .padding(8.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        /** C: clip + background + size — pokazuje wpływ clip na kształt wypełnienia. */
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text("C", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }
    }
}

/**
 * Demonstracja wariantów **Card**.
 *
 * [Card] to kontener Material3 z wbudowaną elewacją (cień), kształtem
 * i kolorem tła z motywu. To standardowy sposób grupowania powiązanych treści
 * w wizualnie wyróżniony kafelek.
 *
 * Warianty:
 * - `Card` — nieinteraktywny (bez efektu ripple)
 * - `ElevatedCard` — jak Card, ale z cieniem elewacji
 * - `OutlinedCard` — ramka zamiast cienia
 * - `Card(onClick=…)` — klikalny z efektem ripple
 */
@Composable
fun CardDemo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        /**
         * A: Podstawowy Card (wypełniony).
         *
         * Domyślnie używa `colorScheme.surfaceContainerHigh` jako tła.
         * Przekaż Modifier, aby kontrolować rozmiar i układ w rodzicu.
         */
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Basic Card",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Default elevation, filled background from the theme.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        /** B: ElevatedCard — cień sprawia, że element unosi się nad tłem. */
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Elevated Card",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Higher shadow — visually stands out more.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        /** C: OutlinedCard — obrys zamiast cienia, płaski wygląd. */
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Outlined Card",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Border stroke, no shadow — flat appearance.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        /**
         * D: Klikalny Card — kafelek z efektem ripple.
         *
         * Przekaż `onClick`, aby cały Card był interaktywny.
         * Użyj `CardDefaults.cardColors()`, aby nadpisać kolor kontenera.
         * `.size(12.dp)` + `.clip(CircleShape)` zamienia dowolny composable w okrągłą odznakę.
         */
        var clicked by remember { mutableStateOf(false) }
        Card(
            onClick = { clicked = !clicked },
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (clicked)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Clickable Card",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (clicked) "Selected ✓" else "Tap to select",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            if (clicked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )
                )
            }
        }
    }
}

/**
 * Demonstracja kontenera **Surface**.
 *
 * [Surface] to prymityw niższego poziomu, na którym zbudowany jest [Card].
 * Używaj go, gdy potrzebujesz pełnej kontroli nad:
 * - `tonalElevation`  — odcień koloru z nakładki elewacji motywu
 * - `shadowElevation` — głębokość cienia w dp
 * - `shape`           — `RoundedCornerShape`, `CircleShape`, `CutCornerShape` itp.
 * - `color`           — kolor tła (domyślnie `colorScheme.surface`)
 * - `contentColor`    — kolor przekazywany dzieciom przez `LocalContentColor`
 *
 * ### Card vs Surface
 * | Komponent | Kiedy używać                                          |
 * |-----------|-------------------------------------------------------|
 * | `Card`    | Gotowe domyślne ustawienia — kafelki z treścią        |
 * | `Surface` | Pełna kontrola — niestandardowe kontenery             |
 */
@Composable
fun SurfaceDemo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        /** A: Domyślna Surface — tło `colorScheme.surface`, delikatny odcień elewacji. */
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp
        ) {
            Text(
                text = "Surface — tonalElevation = 1.dp",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        /** B: Wyższa elewacja tonalna — bardziej nasycony odcień. */
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 8.dp
        ) {
            Text(
                text = "Surface — tonalElevation = 8.dp",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        /**
         * C: Niestandardowy kolor + shadowElevation.
         *
         * Podaj jawny `color`, aby całkowicie zastąpić kolor surface z motywu.
         * `shadowElevation` dodaje cień niezależnie od odcienia tonalnego.
         * `contentColor` jest automatycznie propagowany do dzieci przez `LocalContentColor`.
         */
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Surface — custom color + shadowElevation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "contentColor flows down to children via LocalContentColor.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        /**
         * D: Klikalna Surface — jak Card, ale bez gotowych ustawień stylowania.
         *
         * `Surface(onClick = …)` dodaje efekt ripple bez żadnych narzuconych styli Card.
         */
        var surfaceClicked by remember { mutableStateOf(false) }
        Surface(
            onClick = { surfaceClicked = !surfaceClicked },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (surfaceClicked)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Text(
                text = if (surfaceClicked) "Surface clicked! Tap again." else "Clickable Surface — tap me",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Bezstanowa funkcja kompozycyjna demonstrująca **wynoszenie stanu**.
 *
 * [HoistedCounterCard] nie posiada żadnego zmiennego stanu wewnętrznego.
 * Otrzymuje `count` (stan w **dół**) oraz `onIncrement` / `onReset` (zdarzenia w **górę**).
 *
 * ### Korzyści ze bezstanowości
 * - Łatwe podglądy
 * - Łatwe testowanie — znane dane wejściowe, przewidywalne dane wyjściowe
 * - Rodzic decyduje, co zrobić po naciśnięciu przycisku
 *
 * @param count aktualna wartość licznika — płynie W DÓŁ od właściciela stanu
 * @param onIncrement callback wywoływany po naciśnięciu „+ Increment" — zdarzenie W GÓRĘ
 * @param onReset callback wywoływany po naciśnięciu „Reset" — zdarzenie W GÓRĘ
 */
@Composable
fun HoistedCounterCard(
    count: Int,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    //var count by remember { mutableStateOf(0) }
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Count: $count",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onIncrement,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ Increment")
                }

                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
            }

            Text(
                text = "State lives in ComposableBasicsScreen, not here.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

/**
 *  Demonstracja **CounterCard** z własnym stanem wewnętrznym.
 *  Ten element jest samowystarczalny, ale jego stan nie jest dostępny dla rodzica ani innych elementów UI.
 *  Zmniejsza to elastyczność i ponowną używalność.
 */
@Composable
fun CounterCard(modifier: Modifier = Modifier) {
    var count by remember { mutableStateOf(0) }
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Count: $count",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        count++
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ Increment")
                }

                OutlinedButton(
                    onClick = {
                        count = 0
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
            }


        }
    }
}

/**
 * Pomocniczy composable — cienki, etykietowany separator używany między sekcjami ekranu.
 *
 * @param label tekst wyświetlany na środku separatora
 */
@Composable
fun SectionDivider(label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Podglądy
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Basics — Column Demo", group = "Basics", showBackground = true)
@Composable
fun ColumnDemoPreview() {
    QuizShowcaseTheme {
        ColumnDemo()
    }
}


@Preview(name = "Basics — Row Demo", group = "Basics", showBackground = true)
@Composable
fun RowDemoPreview() {
    QuizShowcaseTheme {
        RowDemo()
    }
}

@Preview(name = "Basics — Box Demo", group = "Basics", showBackground = true)
@Composable
fun BoxDemoPreview() {
    QuizShowcaseTheme {
        BoxDemo()
    }
}

@Preview(name = "Basics — Modifier Demo", group = "Basics", showBackground = true)
@Composable
fun ModifierDemoPreview() {
    QuizShowcaseTheme {
        ModifierDemo(modifier = Modifier.padding(top = 12.dp))
    }
}

@Preview(name = "Basics — Counter Card (count=3)", group = "Basics", showBackground = true)
@Composable
fun CounterCardPreview() {
    QuizShowcaseTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var count by remember { mutableStateOf(3) }
            HoistedCounterCard(
                count = count,
                onIncrement = { count++ },
                onReset = { count = 0 })

            CounterCard()
        }
    }
}


@Preview(name = "Basics — Card Demo", group = "Basics", showBackground = true)
@Composable
fun CardDemoPreview() {
    QuizShowcaseTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CardDemo()
        }
    }
}

@Preview(
    name = "Basics — Surface Demo",
    group = "Basics",
    showBackground = false,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun SurfaceDemoPreview() {
    QuizShowcaseTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            SurfaceDemo()
        }
    }
}


@Preview(
    name = "Basics — Full Screen", group = "Basics", showBackground = true,
    showSystemUi = false
)
@Composable
fun ComposableBasicsScreenPreview() {
    QuizShowcaseTheme {
        ComposableBasicsScreen()
    }
}