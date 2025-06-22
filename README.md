# 🧑‍💼 Employee Management Portal

This is a **Java console-based application** that manages employee records using **MongoDB** as the backend database.

---

## 🚀 Features

- ✅ Add new employee records
- 📝 Update existing employee information
- ❌ Delete employees by email or ID
- 🔍 Search employees by:
  - Name (partial match)
  - Department
  - Skills
  - Joining date range
- 📄 List employees with pagination and sorting
- 📊 Department-wise employee count (aggregation)

---

## 💾 Technologies Used

- **Java 17+**
- **MongoDB** (NoSQL Database)
- **MongoDB Java Driver**
- Java Collections, Regex, and Console I/O

---

## 📦 How to Run

1. Ensure MongoDB is installed and running on `localhost:27017`.
2. Compile the Java code:

   ```bash
   javac EmployeeApp.java

Run the program:
java EmployeeApp
⚠️ MongoDB must be running locally for the app to function properly.

🗃️ Sample MongoDB Document
json
Copy
Edit
{
  "name": "John Doe",
  "email": "john@example.com",
  "department": "HR",
  "joiningDate": "2024-06-10",
  "skills": ["Excel", "Recruitment"]
}

📂 Folder Structure
Copy
Edit
.
├── EmployeeApp.java
├── .gitignore
└── README.md

=======
🧑‍💼 Employee Management Portal
This is a Java application that manages employee records using MongoDB as the backend database.

🚀 Features
✅ Add new employee records
📝 Update existing employee information
❌ Delete employees by email or ID
🔍 Search employees by:
Name (partial match)
Department
Skills
Joining date range
📄 List employees with pagination and sorting
📊 Department-wise employee count (aggregation)
💾 Technologies Used
Java 17+
MongoDB (NoSQL Database)
MongoDB Java Driver
Java Collections, Regex, and Console I/O
📦 How to Run
Ensure MongoDB is installed and running on localhost:27017.
Compile the Java code:
javac EmployeeApp.java
Run the program: java EmployeeApp ⚠️ MongoDB must be running locally for the app to function properly.


