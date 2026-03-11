# QuizShowcase

Aplikacja quizowa dla Androida napisana w **Kotlin** z użyciem **Jetpack Compose**, stworzona jako projekt dydaktyczny demonstrujący kluczowe koncepcje nowoczesnego programowania na Androida.

---

## Cel projektu

QuizShowcase to projekt pokazowy (ang. *showcase*) przeznaczony do nauki i prezentacji następujących zagadnień:

| Temat | Co jest demonstrowane |
|---|---|
| **Cykl życia Activity** | Nadpisane metody `onCreate` → `onDestroy` z logowaniem w Logcat i widoczną historią zdarzeń |
| **Jetpack Compose – State Hoisting** | Stan współdzielony przepływa w dół jako parametry; zdarzenia wracają w górę przez lambdy |
| **`remember` vs `rememberSaveable`** | Celowa utrata stanu po obróceniu ekranu (stan quizu) vs zachowanie stanu (nazwa użytkownika) |
| **Zasoby Androida** | `stringResource`, `dimensionResource`, `painterResource`, lokalizacja, `R`-klasa |
| **Compose Layouts** | `Column`, `Row`, `LazyColumn`, `LazyRow`, `LazyVerticalGrid`, `Scaffold` |
| **Material 3** | `TopAppBar`, `Card`, `Button`, `Slider`, `Switch`, `LinearProgressIndicator`, `AlertDialog` |

---

## Struktura projektu

```
app/src/main/java/pl/marrod/quizshowcase/
├── MainActivity.kt          # Punkt wejścia; cykl życia Activity; State Hoisting motywu
├── data/
│   ├── Category.kt          # Data class kategorii quizu
│   ├── Question.kt          # Data class pytania quizowego
│   └── DataSource.kt        # Singleton – źródło danych (kategorie i pytania)
└── ui/
    ├── QuizScreen.kt        # Główny ekran quizu i wszystkie sekcje kompozytów
    └── theme/               # Motyw Material 3 (kolory, typografia, kształty)
```

---

## Ekrany aplikacji

### 1. Ekran konfiguracji (`SetupSection`)
- Pole tekstowe na nazwę użytkownika (zachowane przez `rememberSaveable`)
- Przełącznik ciemnego/jasnego motywu
- Suwak poziomu trudności (1–3)
- Siatka kategorii quizu (`LazyVerticalGrid`)
- Historia zdarzeń cyklu życia wyświetlana jako przewijana lista `LazyRow`

### 2. Ekran pytań (`QuestionSection` + `QuizProgressSection`)
- Pasek postępu z procentowym wskazaniem ukończenia
- Treść pytania i cztery przyciski odpowiedzi
- Przycisk wyjścia z quizu z dialogiem potwierdzenia (`AlertDialog`)

### 3. Ekran wyników (`ResultsSection`)
- Trofeum wczytywane przez `painterResource` (zasób XML drawable)
- Wynik końcowy w formacie `X / Y`
- Przycisk powrotu do wyboru kategorii

---

