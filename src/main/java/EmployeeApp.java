import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EmployeeApp {
    static MongoCollection<Document> employeeCollection;

    public static void main(String[] args) {
        connectDB();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Employee Management Portal ---");
            System.out.println("1. Add Employee");
            System.out.println("2. Update Employee");
            System.out.println("3. Delete Employee");
            System.out.println("4. Search Employees");
            System.out.println("5. List with Pagination");
            System.out.println("6. Department Statistics");
            System.out.println("7. Exit");

            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> addEmployee(scanner);
                case 2 -> updateEmployee(scanner);
                case 3 -> deleteEmployee(scanner);
                case 4 -> searchEmployees(scanner);
                case 5 -> listWithPagination(scanner);
                case 6 -> departmentStatistics();
                case 7 -> {
                    System.out.println("👋 Exiting...");
                    System.exit(0);
                }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    static void connectDB() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase db = mongoClient.getDatabase("company");
        employeeCollection = db.getCollection("employees");
        System.out.println("✅ Connected to MongoDB.");
    }

    static void addEmployee(Scanner scanner) {
        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim().toLowerCase();

        if (employeeCollection.find(Filters.eq("email", email)).first() != null) {
            System.out.println("❌ Email already exists.");
            return;
        }

        System.out.print("Department: ");
        String dept = scanner.nextLine();

        System.out.print("Joining Date (yyyy-MM-dd): ");
        String joiningDate = scanner.nextLine().trim();
        if (!joiningDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("❌ Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        System.out.print("Skills (comma-separated): ");
        List<String> skills = Arrays.stream(scanner.nextLine().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        Document doc = new Document("name", name)
                .append("email", email)
                .append("department", dept)
                .append("joiningDate", joiningDate)
                .append("skills", skills);

        employeeCollection.insertOne(doc);
        System.out.println("✅ Employee added.");
    }

    static void updateEmployee(Scanner scanner) {
        System.out.print("Enter Email of Employee to Update: ");
        String email = scanner.nextLine().trim().toLowerCase();

        Document emp = employeeCollection.find(Filters.eq("email", email)).first();
        if (emp == null) {
            System.out.println("❌ Employee not found.");
            return;
        }

        System.out.print("New Department (or press Enter to skip): ");
        String dept = scanner.nextLine();

        System.out.print("New Skills (comma-separated or press Enter to skip): ");
        String skillsInput = scanner.nextLine();

        List<Bson> updates = new ArrayList<>();
        if (!dept.isBlank()) updates.add(Updates.set("department", dept));
        if (!skillsInput.isBlank()) {
            List<String> skills = Arrays.stream(skillsInput.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            updates.add(Updates.set("skills", skills));
        }

        if (!updates.isEmpty()) {
            employeeCollection.updateOne(Filters.eq("email", email), Updates.combine(updates));
            System.out.println("✅ Employee updated.");
        } else {
            System.out.println("⚠️ No changes provided.");
        }
    }

    static void deleteEmployee(Scanner scanner) {
        System.out.println("Delete by: 1. Email  2. ID");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim().toLowerCase();
            employeeCollection.deleteOne(Filters.eq("email", email));
        } else if (choice == 2) {
            System.out.print("Enter ID: ");
            String id = scanner.nextLine().trim();
            try {
                employeeCollection.deleteOne(Filters.eq("_id", new ObjectId(id)));
            } catch (Exception e) {
                System.out.println("❌ Invalid Object ID format.");
                return;
            }
        } else {
            System.out.println("❌ Invalid option.");
            return;
        }

        System.out.println("✅ Employee deleted.");
    }

    static void searchEmployees(Scanner scanner) {
        System.out.println("Search by: 1. Name 2. Department 3. Skill 4. Joining Date Range");
        int choice = scanner.nextInt();
        scanner.nextLine();

        List<Document> results = new ArrayList<>();
        switch (choice) {
            case 1 -> {
                System.out.print("Enter partial name: ");
                String name = scanner.nextLine().trim();
                results = employeeCollection.find(Filters.regex("name", Pattern.compile(name, Pattern.CASE_INSENSITIVE)))
                        .into(new ArrayList<>());
            }
            case 2 -> {
                System.out.print("Enter department: ");
                String dept = scanner.nextLine().trim();
                results = employeeCollection.find(Filters.eq("department", dept)).into(new ArrayList<>());
            }
            case 3 -> {
                System.out.print("Enter skill(s) (comma-separated): ");
                List<String> skillList = Arrays.stream(scanner.nextLine().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                List<Bson> skillFilters = skillList.stream()
                        .map(skill -> Filters.regex("skills", Pattern.compile(skill, Pattern.CASE_INSENSITIVE)))
                        .collect(Collectors.toList());

                results = employeeCollection.find(Filters.or(skillFilters)).into(new ArrayList<>());
            }
            case 4 -> {
                System.out.print("Start Date (yyyy-MM-dd): ");
                String start = scanner.nextLine().trim();
                System.out.print("End Date (yyyy-MM-dd): ");
                String end = scanner.nextLine().trim();

                if (!start.matches("\\d{4}-\\d{2}-\\d{2}") || !end.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    System.out.println("❌ Invalid date format. Use yyyy-MM-dd.");
                    return;
                }

                results = employeeCollection.find(
                        Filters.and(
                                Filters.gte("joiningDate", start),
                                Filters.lte("joiningDate", end)
                        )
                ).into(new ArrayList<>());
            }
            default -> System.out.println("❌ Invalid search choice.");
        }

        if (results.isEmpty()) {
            System.out.println("⚠️ No results found.");
        } else {
            results.forEach(doc -> System.out.println(doc.toJson()));
        }
    }

    static void listWithPagination(Scanner scanner) {
        System.out.print("Page number: ");
        int page = scanner.nextInt();

        System.out.println("Sort by: 1. Name 2. Joining Date");
        int sortChoice = scanner.nextInt();
        scanner.nextLine();

        int limit = 5;
        int skip = (page - 1) * limit;

        FindIterable<Document> results;
        if (sortChoice == 1) {
            results = employeeCollection.find()
                    .sort(Sorts.ascending("name"))
                    .skip(skip)
                    .limit(limit);
        } else {
            results = employeeCollection.find()
                    .sort(Sorts.ascending("joiningDate"))
                    .skip(skip)
                    .limit(limit);
        }

        System.out.println("\n--- Page " + page + " ---");
        boolean found = false;
        for (Document doc : results) {
            System.out.println(doc.toJson());
            found = true;
        }

        if (!found) {
            System.out.println("⚠️ No employees found on this page.");
        }
    }

    static void departmentStatistics() {
        AggregateIterable<Document> result = employeeCollection.aggregate(List.of(
                new Document("$group", new Document("_id", "$department")
                        .append("count", new Document("$sum", 1)))
        ));

        System.out.println("--- Department-wise Employee Count ---");
        for (Document doc : result) {
            System.out.println("Department: " + doc.getString("_id") + " | Count: " + doc.get("count"));
        }
    }
}
