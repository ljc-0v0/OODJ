
package com.mycompany.group28assignment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;

public class User {

    protected String userID;
    protected String userName;
    protected String userEmail;
    protected String userPassword;
    protected String userPhoneNum;
    protected String userRole;
    protected String createdBy;

    private static final Path USERS_FILE =
            Paths.get("users.txt");

    private static final Path COUNTER_FILE =
            Paths.get("data", "user_id_counter.txt");

    public User() {
    }

    public User(String userID) {
        this.userID = userID;

        try {
            if (!Files.exists(USERS_FILE)) {
                return;
            }

            List<String> lines = Files.readAllLines(
                    USERS_FILE, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);

                if (parts.length < 7) {
                    continue;
                }

                if (userID != null
                        && userID.equals(parts[0])) {

                    this.userName = parts[1];
                    this.userEmail = parts[2];
                    this.userPassword = parts[3];
                    this.userPhoneNum = parts[4];
                    this.userRole = parts[5];
                    this.createdBy = parts[6];
                    break;
                }
            }

        } catch (IOException ex) {
            System.err.println(
                    "Cannot load user: " + ex.getMessage());
        }
    }

    public User(String userName, String userEmail,
                String userPassword, String userPhoneNum,
                String userRole, String createdBy) {

        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword =
                DigestUtils.md5Hex(userPassword);
        this.userPhoneNum = userPhoneNum;
        this.userRole = userRole;
        this.createdBy = createdBy;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getPhoneNum() {
        return userPhoneNum;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    private static int getHighestExistingID()
            throws IOException {

        int highest = 0;

        if (!Files.exists(USERS_FILE)) {
            return highest;
        }

        List<String> lines = Files.readAllLines(
                USERS_FILE, StandardCharsets.UTF_8);

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            String[] parts = line.split("\\|", -1);

            if (parts.length == 0) {
                continue;
            }

            String id = parts[0].trim();

            if (id.matches("UID\\d+")) {
                try {
                    int number = Integer.parseInt(
                            id.substring(3));

                    highest = Math.max(highest, number);

                } catch (NumberFormatException ex) {
                    System.err.println(
                            "Invalid User ID: " + id);
                }
            }
        }

        return highest;
    }

    private static int getSavedCounter()
            throws IOException {

        if (!Files.exists(COUNTER_FILE)) {
            return 0;
        }

        String value = Files.readString(
                COUNTER_FILE,
                StandardCharsets.UTF_8).trim();

        if (value.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IOException(
                    "Invalid user ID counter file.", ex);
        }
    }

    private static void saveCounter(int number)
            throws IOException {

        Files.createDirectories(
                COUNTER_FILE.getParent());

        Files.writeString(
                COUNTER_FILE,
                String.valueOf(number),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    public synchronized void storeInFile(String role) {

        try {
            if (userName == null || userName.isBlank()
                    || userPassword == null
                    || userRole == null || userRole.isBlank()) {

                throw new IOException(
                        "Missing required user information.");
            }

            int highestExisting =
                    getHighestExistingID();

            int savedCounter =
                    getSavedCounter();

            int nextNumber = Math.max(
                    highestExisting, savedCounter) + 1;

            String nextUserID = String.format(
                    "UID%06d", nextNumber);

            String line = String.join("|",
                    nextUserID,
                    safe(userName),
                    safe(userEmail),
                    safe(userPassword),
                    safe(userPhoneNum),
                    safe(userRole),
                    safe(createdBy));

            Files.writeString(
                    USERS_FILE,
                    line + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            this.userID = nextUserID;

            saveCounter(nextNumber);

            System.out.println(
                    "User saved successfully: " + userID);

        } catch (IOException ex) {
            System.err.println(
                    "Failed to save user: "
                    + ex.getMessage());
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
