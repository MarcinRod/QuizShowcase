package pl.marrod.quizshowcase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import pl.marrod.quizshowcase.data.DataSource
import pl.marrod.quizshowcase.ui.QuizScreen
import pl.marrod.quizshowcase.ui.theme.QuizShowcaseTheme

/**
 * MainActivity (podklasa Activity) jest punktem wejścia do aplikacji, zarządzającym cyklem życia
 * i stanem UI. Aktywność jest odpowiedzialna za tworzenie i wyświetlanie interfejsu użytkownika,
 * obsługę zdarzeń użytkownika oraz zarządzanie stanem aplikacji. W tym przypadku MainActivity
 * wykorzystuje Jetpack Compose do definiowania interfejsu użytkownika. Każda aktywność musi być
 * zadeklarowana w pliku AndroidManifest.xml, aby system Android mógł ją rozpoznać i uruchomić.
 * Obecnym podejściem jesttworzenie aplikacji z pojedynczą aktywnością, a nawigację i różne ekrany
 * obsługuje się za pomocą bibliotek takich jak Jetpack Navigation Component.
 */
class MainActivity : ComponentActivity() {


    /** [lifecycleEvents] to lista przechowująca zdarzenia cyklu życia Activity, która jest aktualizowana
     * w każdym z metod cyklu życia (onCreate, onStart, onResume, onPause, onStop, onDestroy).
     * Jest to używane do demonstracji i śledzenia kolejności wywołań tych metod podczas działania aplikacji.
     */
    private var lifecycleEvents by mutableStateOf<List<String>>(emptyList())

    /** onCreate to metoda cyklu życia, która jest wywoływana, gdy system Android tworzy
     *  nową instancję tej aktywności. Musi być zawsze nadpisana i powinna wywoływać super.onCreate()
     *  jako pierwszą operację, aby system mógł poprawnie zainicjalizować stan aktywności. W tej
     *  metodzie zwykle wykonuje się podstawową konfigurację, taką jak ustawianie widoku za pomocą
     *  setContentView() lub, w przypadku Jetpack Compose, setContent { ... }. onCreate jest
     *  wywoływana tylko raz podczas całego życia aktywności, chyba że nastąpi zmiana konfiguracji
     *  (np. obrót ekranu), w tatkim przypadku system zniszczy i ponownie utworzy aktywność, wywołując
     *  onCreate ponownie. Dlatego ważne jest, aby zarządzać stanem w taki sposób, aby przetrwał te
     *  zmiany (np. rememberSaveable lub bardziej zaawansowanym rozwiązaniem jakim jest ViewModel).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifecycle("onCreate")

              enableEdgeToEdge()
        setContent {
            /** Zarządzanie stanem motywu (ciemny/jasny) na poziomie aktywności, który jest przekazywany
             *  do QuizScreen. Wykorzystanie remember pozwala na zachowanie tego stanu podczas
             *  rekompozycji, ale nie przetrwa zmian konfiguracji (np. obrót ekranu). Dla pełnej
             *  odporności na zmiany konfiguracji można by użyć rememberSaveable
            */
            var isDarkTheme by remember { mutableStateOf(false) }
            QuizShowcaseTheme(darkTheme = isDarkTheme) {
                QuizScreen(
                    isDarkTheme = isDarkTheme,
                    categories = DataSource.categories,
                    lifecycleEvents = lifecycleEvents,
                    onThemeToggle = { isDarkTheme = it },
                )
            }
        }
    }


    /**
     * onStart to metoda cyklu życia, która jest wywoływana, gdy aktywność staje się widoczna
     * dla użytkownika ale nie umożliwia jeszcze interakcji.
     */
    override fun onStart() {
        super.onStart()
        logLifecycle("onStart")
    }

    /**
     * onResume to metoda cyklu życia, która jest wywoływana, gdy aktywność staje się interaktywna
     * i użytkownik może z nią wchodzić w interakcje. W tej metodzie można uruchomić lub wznowić wszelkie operacje,
     * które zostały wstrzymane w onPause, takie jak nasłuchiwanie czujników, odtwarzanie wideo czy aktualizowanie UI.
     */
    override fun onResume() {
        super.onResume()
        logLifecycle("onResume")
    }

    /**
     * onPause to metoda cyklu życia, która jest wywoływana, gdy system Android nakazuje aktywności
     * przejść w stan wstrzymania, co oznacza, że użytkownik przestał z nią wchodzić w interakcje,
     * ale nadal jest częściowo widoczna (np. pojawił się dialog lub inna aktywność jest na wierzchu).
     * W tej metodzie należy wstrzymać wszelkie operacje, które nie powinny być aktywne, gdy użytkownik
     * nie jest bezpośrednio zaangażowany, takie jak nasłuchiwanie czujników, odtwarzanie wideo itp.
     */
    override fun onPause() {
        super.onPause()
        logLifecycle("onPause")
    }

    /**
     * onStop to metoda cyklu życia, która jest wywoływana, gdy aktywność przestaje być widoczna
     * dla użytkownika. Może to nastąpić, gdy użytkownik przechodzi do innej aktywności lub gdy
     * ekran jest wyłączany. W tej metodzie należy zwolnić zasoby, które nie są potrzebne, oraz
     * zapisać wszelkie dane, które powinny być zachowane, ponieważ aktywność może zostać zniszczona,
     * jeśli system potrzebuje zwolnić pamięć.
     */
    override fun onStop() {
        super.onStop()
        logLifecycle("onStop")
    }


    /**
     * onDestroy to metoda cyklu życia, która jest wywoływana, gdy aktywność jest niszczona.
     * Może to nastąpić, gdy użytkownik zamyka aktywność lub gdy system Android musi zwolnić pamięć.
     * W tej metodzie należy zwolnić wszelkie zasoby, które nie zostały zwolnione w onStop, oraz
     * wykonać ostateczne czyszczenie.
     */
    override fun onDestroy() {
        super.onDestroy()
        // flaga isFinishing pozwala odróżnić,
        // czy aktywność jest niszczona z powodu zamknięcia przez użytkownika (isFinishing == true) czy z
        // powodu zmiany konfiguracji (isFinishing == false)
        logLifecycle("onDestroy — isFinishing=$isFinishing")
   }

    /**
     * Loguje zdarzenia cyklu życia do logcat oraz aktualizuje listę lifecycleEvents
     */
    private fun logLifecycle(event: String) {
        Log.d("Lifecycle", event)
        // lifecycleEvents jest aktualizowane poprzez dodanie nowego zdarzenia do istniejącej listy.
        // Ponieważ lifecycleEvents jest typu List<String>, a List jest domyślnie tylko do odczytu, tworzymy nową listę
        // poprzez dodanie nowego zdarzenia do istniejącej listy.
        lifecycleEvents = lifecycleEvents + event
    }
}
