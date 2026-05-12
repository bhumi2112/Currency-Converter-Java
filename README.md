# CurrencyFX — Real-Time Currency Converter 💱

**CurrencyFX** is a professional desktop application that enables users to convert currencies in real-time using live exchange rates, with full history tracking in a MySQL database.

## 🚀 Key Features
- **Live Exchange Rates:** Fetches data from external APIs using GSON.
- **150+ Currencies:** Supports all major global currency codes.
- **Conversion History:** Automatically saves the last 10 conversions to a MySQL database.
- **Professional UI:** GitHub-inspired dark theme for a modern 'coder' aesthetic.
- **MVC Architecture:** Built using a clean Layered Architecture (Controller, Service, DAO, Model).

## 🛠️ Technology Stack
- **Language:** Java 17
- **GUI:** JavaFX 21 (FXML & CSS)
- **Database:** MySQL 8.0
- **Build Tool:** Maven
- **External API:** ExchangeRate-API

## 📂 How to Run
1. Clone the repository.
2. Run `schema.sql` in your MySQL server.
3. Add your API key in `AppConstants.java`.
4. Build the project using Maven: `mvn clean install`.
5. Run the app: `mvn javafx:run`.

---
*Developed as a 2nd-year Computer Engineering project at SPPU.*
