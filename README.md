# 🧨 Minesweeper Game

A fully functional **Minesweeper** desktop game built in Java using **Swing GUI components**, featuring multiple difficulty levels, flagging system, timer, and intuitive gameplay.

---

## 🎮 Gameplay Features

- 💣 Classic Minesweeper mechanics
- 🚩 Flagging system with visual icons
- 🕒 Game timer to track your performance
- 🎯 Difficulty selection (Easy, Medium, Hard)
- 🖱️ Mouse interactions (left-click reveal, right-click flag/unflag)
- 🟨 Smart flood-reveal for empty cells
- ✅ Win detection and Game Over handling
- 📷 Custom icons for mines and flags (with fallback to text if missing)

---

## 🧱 Game Structure

### `MineSweeper.java`

- Main game panel extending `JPanel`
- Handles:
  - Grid rendering
  - UI setup
  - Game timer
  - User interactions
  - Game state management
  - Difficulty selection and dynamic resizing

### `Cell.java`

- Model for individual grid cells
- Stores:
  - Mine presence
  - Reveal state
  - Flag state
  - Adjacent mine count

---

## 🖥️ How to Run

### Requirements

- Java JDK 8 or later

### Compile

```bash
javac MineSweeper.java Cell.java
