package pl.marrod.quizshowcase.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.marrod.quizshowcase.R
import pl.marrod.quizshowcase.ui.theme.QuizShowcaseTheme

// ─────────────────────────────────────────────────────────────────────────────
// COMPOSABLE BASICS SCREEN
//
// This screen is a teaching reference for the fundamental building blocks
// of Jetpack Compose. Each section demonstrates one concept with a label
// and a live, interactive example.
//
// Topics covered:
//   1. Column  — vertical stacking
//   2. Row     — horizontal stacking
//   3. Box     — overlapping / layering
//   4. Modifier — chaining layout, appearance and behaviour
//   5. Card    — elevated, clickable content container
//   6. Surface — background, shape and elevation for any region
//   7. State hoisting — counter button whose state lives in the CALLER
// ─────────────────────────────────────────────────────────────────────────────

// COMPOSABLE: State Hoisting
// ComposableBasicsScreen is the "hoist point" for the counter demo.
// It owns `count` and passes it DOWN to CounterCard, which passes
// the increment event UP via the `onIncrement` lambda.
//
//   ComposableBasicsScreen          ← state owner
//       │  count (Int)              ← flows DOWN as a parameter
//       ▼
//   CounterCard
//       │  onIncrement ()           ← event flows UP via lambda
//       └──► ComposableBasicsScreen updates count → recompose
@Composable
fun ComposableBasicsScreen() {
    // COMPOSABLE: State — `count` lives here, NOT inside CounterCard.
    // This is the "hoisted" state: owned at the lowest level that needs it.
    var count by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ── Screen title ────────────────────────────────────────────────────
        Text(
            text = "Composable Building Blocks",
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
        // COMPOSABLE: State Hoisting — we pass both the value AND the event handler.
        // CounterCard has no idea where `count` is stored or how it is updated.
        CounterCard(
            count = count,
            onIncrement = { count++ },
            onReset = { count = 0 }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1 · COLUMN
// ─────────────────────────────────────────────────────────────────────────────

// COMPOSABLE: Column
// Column places its children one below the other (main axis = vertical).
// Key parameters:
//   verticalArrangement — spacing / alignment along the vertical axis
//   horizontalAlignment — alignment of children on the horizontal axis
@Composable
fun ColumnDemo() {
    // COMPOSABLE: Modifier — .fillMaxWidth() expands the Column to use all available width.
    // .background() fills it with a surface-variant colour so the boundary is visible.
    // .padding() adds inner space between the Column's edge and its children.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),   // 8 dp gap between each child
        horizontalAlignment = Alignment.CenterHorizontally  // children centred horizontally
    ) {
        Text("First item", style = MaterialTheme.typography.bodyMedium)
        Text("Second item", style = MaterialTheme.typography.bodyMedium)

        // COMPOSABLE: UI Elements — Button inside a Column.
        // Modifier.fillMaxWidth() makes the button stretch to the Column's full width.
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Full-width button")
        }

        // COMPOSABLE: UI Elements — Image loaded from a vector drawable via painterResource.
        // RESOURCES: R.drawable.ic_quiz_trophy — resolved through the R class at runtime.
        Image(
            painter = painterResource(R.drawable.ic_quiz_trophy),
            contentDescription = "Trophy",
            modifier = Modifier
                .size(48.dp)            // fixed size
                .align(Alignment.End)   // override column's CenterHorizontally for this child
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2 · ROW
// ─────────────────────────────────────────────────────────────────────────────

// COMPOSABLE: Row
// Row places its children side by side (main axis = horizontal).
// Key parameters:
//   horizontalArrangement — spacing / alignment along the horizontal axis
//   verticalAlignment     — alignment of children on the vertical axis
@Composable
fun RowDemo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, // push children to opposite ends
        verticalAlignment = Alignment.CenterVertically    // align children on the midline
    ) {
        // COMPOSABLE: UI Elements — Icon from Material Icons (in-memory, no R class).
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

        // COMPOSABLE: Modifier — .weight(1f) on a Row child makes it fill remaining space.
        // Useful for making one child expand while others stay at intrinsic size.
        // (Not used here to keep the SpaceBetween demo clean, but shown as a comment.)
        //
        // Example:  Text("...", modifier = Modifier.weight(1f))

        Button(onClick = {}) {
            Text("Go")
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3 · BOX
// ─────────────────────────────────────────────────────────────────────────────

// COMPOSABLE: Box
// Box stacks its children on top of each other (like a frame / FrameLayout).
// The last child drawn is on top. Each child can position itself with
// Modifier.align(Alignment.*) relative to the Box bounds.
@Composable
fun BoxDemo() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // LAYER 1 — bottom-left label (drawn first = furthest back)
        Text(
            text = "Background layer",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .align(Alignment.BottomStart) // position within the Box
                .padding(8.dp)
        )

        // LAYER 2 — centred image (drawn second = on top of layer 1)
        Image(
            painter = painterResource(R.drawable.ic_quiz_trophy),
            contentDescription = "Trophy",
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.Center) // centre of the Box
        )

        // LAYER 3 — top-right badge (drawn last = on top of everything)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)  // top-right corner of the parent Box
                .padding(8.dp)
                .size(28.dp)
                .clip(CircleShape)        // COMPOSABLE: Modifier — clip() changes the shape
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

// ─────────────────────────────────────────────────────────────────────────────
// 4 · MODIFIER
// ─────────────────────────────────────────────────────────────────────────────

// COMPOSABLE: Modifier
// Modifier is an ordered chain of instructions applied to a composable.
// Order matters: .padding().background() ≠ .background().padding()
//
//   Modifier
//     .size(80.dp)          — constrain size
//     .clip(shape)          — clip to shape  (applied before background)
//     .background(color)    — fill clipped area
//     .padding(8.dp)        — inner space (applied inside the clipped area)
@Composable
fun ModifierDemo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── A: padding OUTSIDE background — padding area is NOT coloured ──
        Text(
            text = "A: padding → background",
            modifier = Modifier
                .border(2.dp, MaterialTheme.colorScheme.primary) // border shows the full size of the composable
                .padding(12.dp)         // space added BEFORE background — not coloured
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(8.dp),         // inner space inside the coloured box
            style = MaterialTheme.typography.bodyMedium
        )

        // ── B: background FIRST, then padding — background covers the padding area ──
        Text(
            text = "B: background → padding",
            modifier = Modifier
                .border(2.dp, MaterialTheme.colorScheme.primary) // border shows the full size of the composable
                .background(MaterialTheme.colorScheme.tertiaryContainer) // coloured first
                .padding(12.dp).padding(8.dp),        // inner space — sits inside the coloured area
            style = MaterialTheme.typography.bodyMedium
        )

        // ── C: clip + background + size — shows how clip affects the fill shape ──
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)                                        // clips to circle
                .background(MaterialTheme.colorScheme.primaryContainer),  // fills the circle
            contentAlignment = Alignment.Center
        ) {
            Text("C", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }

        Text(
            text = "Order: A has outer gap, B fills to edge, C shows clip.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5 · CARD
// ─────────────────────────────────────────────────────────────────────────────

// COMPOSABLE: Card
// Card is a Material3 container with built-in elevation (shadow), shape, and a
// background colour from the theme. It is the standard way to group related
// content into a visually distinct tile.
//
// Variants:
//   Card            — non-interactive (no ripple)
//   ElevatedCard    — same but with a drop-shadow elevation
//   OutlinedCard    — border instead of shadow
//   Card(onClick=…) — tappable with ripple feedback
@Composable
fun CardDemo() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ── A: Basic (filled) Card ───────────────────────────────────────────
        // Card uses colorScheme.surfaceContainerHigh as its default background.
        // Pass a Modifier to control its size and layout in the parent.
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

        // ── B: ElevatedCard — drop-shadow makes it float above the background ─
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

        // ── C: OutlinedCard — stroke instead of shadow ───────────────────────
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

        // ── D: Clickable Card — tappable tile with ripple ────────────────────
        // Pass onClick to make the whole card interactive.
        // Use CardDefaults.cardColors() to override the container colour.
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
                // COMPOSABLE: Modifier — size + clip turns any composable into a circle badge
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

// ─────────────────────────────────────────────────────────────────────────────
// 6 · SURFACE
// ─────────────────────────────────────────────────────────────────────────────

// COMPOSABLE: Surface
// Surface is the lower-level primitive that Card is built on top of.
// Use it when you want explicit control over:
//   • tonalElevation  — colour tint from the theme elevation overlay
//   • shadowElevation — drop-shadow depth in dp
//   • shape           — RoundedCornerShape, CircleShape, CutCornerShape, etc.
//   • color           — background colour (defaults to colorScheme.surface)
//   • contentColor    — colour passed to children via LocalContentColor
//
// Card vs Surface:
//   Card    → opinionated defaults (colours, click handling)  — use for content tiles
//   Surface → blank slate with full control                   — use for custom containers
@Composable
fun SurfaceDemo() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ── A: Default Surface — colorScheme.surface background ─────────────
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp       // slight tint from elevation overlay
        ) {
            Text(
                text = "Surface — tonalElevation = 1.dp",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // ── B: Higher tonal elevation — more pronounced tint ─────────────────
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 8.dp       // more saturated tint
        ) {
            Text(
                text = "Surface — tonalElevation = 8.dp",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // ── C: Custom colour + shadowElevation ───────────────────────────────
        // Provide an explicit color to override the theme surface colour entirely.
        // shadowElevation adds a drop shadow independent of the tonal tint.
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

        // ── D: Clickable Surface — like Card but unstyled ────────────────────
        // Surface(onClick = …) adds ripple feedback without any Card opinionation.
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

// ─────────────────────────────────────────────────────────────────────────────
// 7 · STATE HOISTING — CounterCard
// ─────────────────────────────────────────────────────────────────────────────

// COMPOSABLE: State Hoisting
// CounterCard is STATELESS — it owns no mutable state.
// It receives `count` (state DOWN) and `onIncrement` / `onReset` (events UP).
//
// Benefits of being stateless:
//   • Easy to preview with any count value.
//   • Easy to test — just call it with known inputs.
//   • The parent decides what to do when the button is tapped.
//
//   ✅  CounterCard(count = count, onIncrement = { count++ })
//   ❌  internal var count by remember { mutableStateOf(0) }  // hard to test / reuse
@Composable
fun CounterCard(
    count: Int,                 // state flows DOWN
    onIncrement: () -> Unit,    // event flows UP
    onReset: () -> Unit         // event flows UP
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                // COMPOSABLE: Modifier — .weight(1f) splits the Row evenly between the buttons.
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

// ─────────────────────────────────────────────────────────────────────────────
// Helper — thin labelled divider used between sections
// ─────────────────────────────────────────────────────────────────────────────
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
// PREVIEWS
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Basics — Full Screen", group = "Basics", showBackground = true, showSystemUi = true)
@Composable
fun ComposableBasicsScreenPreview() {
    QuizShowcaseTheme {
        ComposableBasicsScreen()
    }
}

@Preview(name = "Basics — Counter Card (count=3)", group = "Basics", showBackground = true)
@Composable
fun CounterCardPreview() {
    QuizShowcaseTheme {
        CounterCard(count = 3, onIncrement = {}, onReset = {})
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

@Preview(name = "Basics — Card Demo", group = "Basics", showBackground = true)
@Composable
fun CardDemoPreview() {
    QuizShowcaseTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CardDemo()
        }
    }
}

@Preview(name = "Basics — Surface Demo", group = "Basics", showBackground = false,
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

