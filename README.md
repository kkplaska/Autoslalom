# 🏁 Autoslalom

**Autoslalom** to odtworzenie klasycznej radzieckiej gry elektronicznej typu LCD, wydanej przez firmę Elektronika w latach 80. Celem gry jest sterowanie samochodem poruszającym się po torze pełnym przeszkód, unikanie kolizji oraz zdobycie jak największej liczby punktów. Sterowanie odbywa się za pomocą dwóch przycisków umożliwiających ruch w lewo i w prawo.

## 🎮 Zasady gry

- **Cel gry**: Gracz steruje samochodem jadącym po torze z przeszkodami. Zadaniem jest unikanie kolizji i zdobywanie punktów za jak najdłuższą, bezpieczną jazdę.
- **Sterowanie**: Samochód kontrolowany jest za pomocą dwóch klawiszy – lewego `a` i prawego `d`. Gracz musi reagować w odpowiednim momencie, aby omijać przeszkody.
- **Przeszkody**: Na trasie pojawiają się przeszkody w postaci barier. Zderzenie z którąkolwiek z nich kończy grę.
- **Punktacja**: Za każdy bezkolizyjnie przejechany rząd przeszkód gracz zdobywa punkty. Im dłużej utrzyma się na torze, tym wyższy wynik zdobędzie.
- **Poziom trudności**: Wraz z postępem gry zwiększa się prędkość poruszania się przeszkód, co wymaga szybszych reakcji od gracza.


## 🎮 Obsługa gry i sterowanie:
Gra się uruchamia po wciśnięciu klawisza `s` na klawiaturze.

Sterowanie odbywa się przy pomocy klawiszy:
- `a` (zmiana pasa na najbliższy po lewej),
- `d` (zmiana pasa na najbliższy po prawej).

## 🛠️ Wymagania systemowe

- **Java Development Kit (JDK)**: Wersja 8 lub nowsza
- **System operacyjny**: Windows, macOS lub Linux

## 🚀 Uruchamianie gry

1. **Sklonuj repozytorium**:
   ```bash
   git clone https://github.com/kkplaska/Autoslalom.git
   cd Autoslalom
   ```

2. **Skompiluj projekt**:
   ```bash
   javac Main.java
   ```

3. **Uruchom grę**:
   ```bash
   java Main
   ```

## 📁 Struktura katalogów

- `Main.java` – główny plik zawierający uruchamiający pozostałe.
- `game/` – katalog zawierający klasy związane z mechaniką gry.
- `pres/` – katalog zawierający klasy związane z zasobami graficznymi i prezentacyjnymi.
- `res/` – zasoby graficzne.

## 📄 Licencja

Projekt jest udostępniony na licencji MIT. Szczegóły znajdują się w pliku `LICENSE`.

## 👤 Autor

Projekt został stworzony przez [kkplaska](https://github.com/kkplaska).