# Online Food Ordering System

📌 **Academic Project**  
Semester: 2-2 | Year: 2023

This project was developed as part of the 2-2 semester coursework in 2023.  
It is built using **Java Swing** for GUI and **MySQL (via JDBC)** for database connectivity.


## Features (basic)
- User login (simple username/password)
- Browse menu items from database
- Add items to cart and place an order
- Simple admin panel to add/remove menu items
- Uses JDBC (MySQL) for persistence

## Technology
- Java (SE) 8+
- Java Swing (GUI)
- MySQL (database)
- JDBC

## Project Structure
```
online-food-ordering-system/
├─ src/com/ofos/         # Java source files
├─ resources/            # SQL schema and sample data
└─ README.md
```

## Setup

1. **Install MySQL** and create a database:
   - Database name: `ofos_db` (or change in `DBConnection.java`)

2. **Run SQL script** `resources/ofos_schema.sql` to create tables and sample data.
   ```sql
   -- from resources/ofos_schema.sql
   ```

3. **Edit DB credentials**
   - File: `src/com/ofos/DBConnection.java`
   - Update `DB_URL`, `DB_USER`, `DB_PASS` as needed.

4. **Compile & Run**
   Using command line:
   ```bash
   javac -d out src/com/ofos/*.java
   java -cp out com.ofos.Main
   ```
   Or import the project into your favorite IDE (Eclipse/IntelliJ) as a Java project.

## Notes
- Passwords are stored in plaintext for simplicity — **do not** use this approach in production.
- This project is meant as a starting point. You should add input validation, better error handling, and secure authentication before using in real scenarios.

## Author
Your Name — change as required before uploading to GitHub.
