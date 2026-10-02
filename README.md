# 🐾 PetStock: A Pet Supply Inventory Management System

**PetStock** is an inventory management system designed for pet supply businesses. It helps users manage products, monitor stock levels, track stock movement, and keep inventory records organized.

The system supports common inventory tasks such as adding and updating products, recording stock-in and stock-out transactions, monitoring low-stock items, and managing product information.

## 🛠️ Built With

PetStock was developed using:

- **Java** for the main application
- **MySQL** for the database
- **Maven** for dependency management and project building
- **JDBC** for database connectivity
- **IntelliJ IDEA** as the recommended development environment
- **XAMPP** for local MySQL and phpMyAdmin setup

## 🚀 Application Structure

The project follows a desktop-based Java application structure, with `App.java` serving as the main entry point of the system.

---
# Setup Guide

Follow the steps below to set up and run the project on your local machine.

## ✅ Requirements

Before setting up the project, make sure the following are installed:

- Java JDK
- Maven
- Git
- MySQL
- XAMPP
- IntelliJ IDEA, NetBeans, Eclipse, or another Java IDE

**Recommended IDE:** IntelliJ IDEA

---

## 📥 1. Clone the Repository

Clone the GitHub repository:

```bash
git clone <repository-url>
```

After cloning, locate the project folder and open it using your preferred Java IDE.

Recommended IDEs:

- IntelliJ IDEA
- Apache NetBeans
- Eclipse

---

## 🗄️ 2. Set Up the Database

The database file is located inside:

```text
db-configuration/
```

Import the provided database into MySQL.

### Recommended: XAMPP

1. Open **XAMPP Control Panel**.
2. Start **Apache**.
3. Start **MySQL**.
4. Make sure MySQL is running on port:

```text
3306
```

### If MySQL is not using port 3306

1. In XAMPP, locate **MySQL**.
2. Click **Config**.
3. Open **my.ini**.
4. Search for:

```ini
port=
```

5. Change it to:

```ini
port=3306
```

6. Save the file.
7. Restart MySQL.

After MySQL is running, import the database file from the `db-configuration` folder using **phpMyAdmin** or another MySQL management tool.

---

## 🔌 3. Test the Database Connection

Run the application after importing the database.

If the application starts successfully and can access the database, the database setup is working correctly.

Make sure the database connection uses:

```text
Host: localhost
Port: 3306
```

---

# Running the Application

There are two ways to run the project.

## Option 1: Run Using an IDE

Open the project using IntelliJ IDEA, NetBeans, Eclipse, or another compatible IDE.

Locate:

```text
App.java
```

`App.java` is the main entry point of the application.

In IntelliJ IDEA:

1. Open `App.java`.
2. Click the **Run** button beside the `main()` method.

The application should start normally.

---

## Option 2: Run Using the Terminal

Open a terminal inside the project root directory.

Build the project using Maven:

```bash
mvn clean package
```

After the build completes, Maven will generate the JAR file inside:

```text
target/
```

Run the generated JAR:

```bash
java -jar target/untitled-1.0-SNAPSHOT.jar
```

> **Note:** The exact JAR filename may vary depending on the Maven configuration.

You can check the `target` folder and run the generated JAR using:

```bash
java -jar target/<generated-file-name>.jar
```

## 💳 Business Rules

- **Only registered users can access the system.** The system is accessible only to registered Admin and Staff users.
- **Users must log in with valid credentials.** Users must provide the correct username and password before accessing inventory functions.
- **Admin can manage products.** Admin users can add, update, delete, and view product records.
- **Staff can access inventory functions.** Staff can view inventory, search for products, check stock, and view product details.
- **Product information must be complete.** All required product details must be provided before a product record can be saved.
- **Inventory changes must be saved.** Any changes to stock or inventory records must be saved to keep information updated.
- **Access depends on the user's role.** Users can only access the system functions permitted by their assigned role.
- **Users should log out after completing their tasks.** Logging out helps protect the system and prevent unauthorized access.

---

## Notes

If Maven generates both:

```text
untitled-1.0-SNAPSHOT.jar
original-untitled-1.0-SNAPSHOT.jar
```

run:

```bash
java -jar target/untitled-1.0-SNAPSHOT.jar
```

The `original-...jar` file is usually the version created before Maven finishes packaging the runnable application.