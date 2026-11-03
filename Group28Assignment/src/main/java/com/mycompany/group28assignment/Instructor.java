
package com.mycompany.group28assignment;

import java.util.ArrayList;
import java.util.Scanner;

public class Instructor extends User {

    private final Scanner input = new Scanner(System.in);
    private final ArrayList<String> schedules = new ArrayList<>();
    private final ArrayList<String> facilityProblems = new ArrayList<>();

    public Instructor(String userID) {
        super(userID);
    }

    // Temporary constructor for testing
    public Instructor(int id, String name, String email, String password) {
        super();
        this.userID = String.valueOf(id);
        this.userName = name;
        this.userEmail = email;
        this.userPassword = password;
        this.userRole = "Instructor";
    }

    // ================= SCHEDULE =================

    public void createSchedule() {
        System.out.println("\n=== Create Schedule ===");

        System.out.print("Enter Class Name: ");
        String className = input.nextLine();

        System.out.print("Enter Date: ");
        String date = input.nextLine();

        System.out.print("Enter Time: ");
        String time = input.nextLine();

        schedules.add(className + " | " + date + " | " + time);
        System.out.println("Schedule created successfully!");
    }

    public void viewSchedule() {
        System.out.println("\n=== View Schedules ===");

        if (schedules.isEmpty()) {
            System.out.println("No schedules found.");
            return;
        }

        for (int i = 0; i < schedules.size(); i++) {
            System.out.println((i + 1) + ". " + schedules.get(i));
        }
    }

    public void searchSchedule() {
        System.out.println("\n=== Search Schedule ===");

        System.out.print("Enter Class Name to Search: ");
        String keyword = input.nextLine();

        boolean found = false;

        for (String schedule : schedules) {
            if (schedule.toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println("Found: " + schedule);
                found = true;
            }
        }

        if (!found) {
            System.out.println("Schedule not found.");
        }
    }

    public void editSchedule() {
        System.out.println("\n=== Edit Schedule ===");

        if (schedules.isEmpty()) {
            System.out.println("No schedules to edit.");
            return;
        }

        viewSchedule();
        System.out.print("Enter Schedule Number to Edit: ");
        int number = readNumber();

        if (number < 1 || number > schedules.size()) {
            System.out.println("Invalid schedule number.");
            return;
        }

        System.out.print("Enter New Class Name: ");
        String className = input.nextLine();

        System.out.print("Enter New Date: ");
        String date = input.nextLine();

        System.out.print("Enter New Time: ");
        String time = input.nextLine();

        schedules.set(number - 1, className + " | " + date + " | " + time);
        System.out.println("Schedule updated successfully!");
    }

    public void cancelSchedule() {
        System.out.println("\n=== Cancel Schedule ===");

        if (schedules.isEmpty()) {
            System.out.println("No schedules to cancel.");
            return;
        }

        viewSchedule();
        System.out.print("Enter Schedule Number to Cancel: ");
        int number = readNumber();

        if (number < 1 || number > schedules.size()) {
            System.out.println("Invalid schedule number.");
            return;
        }

        schedules.remove(number - 1);
        System.out.println("Schedule cancelled successfully!");
    }

    // ================= FACILITY PROBLEMS =================

    public void createFacilityProblem() {
        System.out.println("\n=== Create Facility Problem ===");

        System.out.print("Enter Facility Name: ");
        String facility = input.nextLine();

        System.out.print("Enter Problem Description: ");
        String description = input.nextLine();

        System.out.print("Enter Problem Date: ");
        String date = input.nextLine();

        facilityProblems.add(facility + " | " + description + " | " + date);
        System.out.println("Facility problem created successfully!");
    }

    public void viewFacilityProblem() {
        System.out.println("\n=== View Facility Problems ===");

        if (facilityProblems.isEmpty()) {
            System.out.println("No facility problems found.");
            return;
        }

        for (int i = 0; i < facilityProblems.size(); i++) {
            System.out.println((i + 1) + ". " + facilityProblems.get(i));
        }
    }

    public void searchFacilityProblem() {
        System.out.println("\n=== Search Facility Problem ===");

        System.out.print("Enter Facility or Problem to Search: ");
        String keyword = input.nextLine();

        boolean found = false;

        for (String problem : facilityProblems) {
            if (problem.toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println("Found: " + problem);
                found = true;
            }
        }

        if (!found) {
            System.out.println("Facility problem not found.");
        }
    }

    public void editFacilityProblem() {
        System.out.println("\n=== Edit Facility Problem ===");

        if (facilityProblems.isEmpty()) {
            System.out.println("No facility problems to edit.");
            return;
        }

        viewFacilityProblem();
        System.out.print("Enter Problem Number to Edit: ");
        int number = readNumber();

        if (number < 1 || number > facilityProblems.size()) {
            System.out.println("Invalid problem number.");
            return;
        }

        System.out.print("Enter New Facility Name: ");
        String facility = input.nextLine();

        System.out.print("Enter New Problem Description: ");
        String description = input.nextLine();

        System.out.print("Enter New Problem Date: ");
        String date = input.nextLine();

        facilityProblems.set(number - 1,
                facility + " | " + description + " | " + date);

        System.out.println("Facility problem updated successfully!");
    }

    public void deleteFacilityProblem() {
        System.out.println("\n=== Delete Facility Problem ===");

        if (facilityProblems.isEmpty()) {
            System.out.println("No facility problems to delete.");
            return;
        }

        viewFacilityProblem();
        System.out.print("Enter Problem Number to Delete: ");
        int number = readNumber();

        if (number < 1 || number > facilityProblems.size()) {
            System.out.println("Invalid problem number.");
            return;
        }

        facilityProblems.remove(number - 1);
        System.out.println("Facility problem deleted successfully!");
    }

    // ================= RATINGS =================

    public void viewRating() {
        System.out.println("\n=== View Ratings ===");
        System.out.println("Ratings feature will be connected later.");
    }

    public void searchRating() {
        System.out.println("\n=== Search Ratings ===");
        System.out.println("Ratings feature will be connected later.");
    }

    // ================= HELPER =================

    private int readNumber() {
        try {
            return Integer.parseInt(input.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
