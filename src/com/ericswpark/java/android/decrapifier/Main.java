package com.ericswpark.java.android.decrapifier;

import java.io.*;
import java.util.*;

public class Main {

    private static final String DELIMITER = ",";

    private static final ArrayList<AndroidPackage> androidPackages = new ArrayList<>();
    private static Scanner scanner;

    private static boolean verbose = false;
    private static boolean auto = false;

    public static void main(String[] args) {
        if (!checkADBInstalled()) {
            System.out.println("Error: Failed ADB install check.");
            exit(1);
        }

        scanner = new Scanner(System.in);
        String packageListFile;

        if (args.length != 0) {
            ArrayList<String> arguments = new ArrayList<>();
            Collections.addAll(arguments, args);

            // Parse arguments
            if (arguments.contains("-v")) {
                verbose = true;
                arguments.remove("-v");
            }
            if (arguments.contains("-a")) {
                auto = true;
                arguments.remove("-a");
            }
            if (!arguments.isEmpty()){
                System.out.println("Invalid arguments.");
                System.exit(1);
            }
        }

        if (auto) {
            try {
                packageListFile = autoDetectDevice();
            } catch (ADBException adbException) {
                System.out.println(adbException.getMessage());
                return;
            }
        } else {
            System.out.print("What package list would you like to use? (format: codename.csv) ");
            packageListFile = scanner.nextLine();
        }

        Objects.requireNonNull(packageListFile);
        readFile(packageListFile);
        processPackages();
    }

    private static void readFile(String filename) {
        try (FileReader fileReader = new FileReader(filename);
             BufferedReader bufferedReader = new BufferedReader(fileReader)){

            String line;
            // Skip CSV 1st line
            bufferedReader.readLine();

            while ((line = bufferedReader.readLine()) != null) {
                // Check if line is empty, skip if it is
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] rawInput = line.split(DELIMITER);

                if(rawInput.length == 3) {
                    AndroidPackage androidPackage = new AndroidPackage(rawInput[0], Boolean.parseBoolean(rawInput[1]),
                            rawInput[2]);
                    androidPackages.add(androidPackage);
                } else {
                    System.out.println("Skipped potentially malformed line: " + line);
                    System.out.println("Please check the list file for any formatting problems.");
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("Error: the specified package list file does not exist!");
            exit(1);
        } catch (IOException ioException) {
            System.out.println("Error: the file could not be read.");
            exit(1);
        }
    }

    private static String autoDetectDevice() throws ADBException {
        System.out.println("Detecting device using ADB...");

        // Get device manufacturer
        String[] command = {"adb", "shell", "getprop", "ro.product.brand"};
        String manufacturer = runCommand(command);
        if (manufacturer == null) {
            throw new ADBException("No devices detected. Make sure ADB is enabled in Developer Options.");
        }

        // Get device codename
        // Both ro.product.device and ro.product.name return the codename
        command = new String[]{"adb", "shell", "getprop", "ro.product.device"};
        String codename = runCommand(command);
        Objects.requireNonNull(codename);

        System.out.printf("Detected device %s/%s.%n", manufacturer, codename);

        return String.format("list/%s/%s.csv", manufacturer, codename);
    }

    private static String runCommand(String[] command) {
        Runtime runtime = Runtime.getRuntime();

        try {
            Process process = runtime.exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String response = reader.readLine();

            if(response != null) {
                if (verbose) {
                    System.out.println("|-->| " + commandArrayToString(command));
                    System.out.println("|<--| " + response);
                }
                return response;
            }

            reader.close();
        } catch (IOException ioException) {
            if (ioException.getMessage().contains("error=2")) {
                System.out.println("Error: adb was not found.");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String commandArrayToString(String[] command) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : command) {
            stringBuilder.append(s);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    private static void processPackages() {
        // Ask before starting
        System.out.printf("Found %d packages to disable/remove. Start? (y/n) ", androidPackages.size());
        String startConfirm = scanner.nextLine();
        if(startConfirm.equalsIgnoreCase("Y")) {
            // Start deletion process
            int success = 0;
            int failure = 0;

            for (AndroidPackage androidPackage : androidPackages) {
                boolean result;
                if (!androidPackage.isForceDelete()) {
                    result = disablePackage(androidPackage.getPackageIdentifier());
                } else {
                    result = removePackage(androidPackage.getPackageIdentifier());
                }

                if (result) {
                    System.out.printf("%s - successfully disabled/removed!%n", androidPackage.getPackageIdentifier());
                    success++;
                } else {
                    System.out.printf("%s - failed to disable/remove. It may have been deleted/disabled already.%n",
                            androidPackage.getPackageIdentifier());
                    failure++;
                }
            }

            System.out.printf("Finished! Successfully disabled/removed %d packages, failed %d, total %d%n", success,
                    failure, androidPackages.size());
        } else {
            System.out.println("OK, deletion canceled.");
            exit(0);
        }
    }

    private static boolean disablePackage(String packageName) {
        String[] command = {"adb", "shell", "pm", "disable-user", "--user", "0", packageName};
        String response = runCommand(command);

        return response != null &&
                response.equals(String.format("Package %s new state: disabled-user", packageName));
    }

    private static boolean removePackage(String packageName) {
        String[] command = {"adb", "shell", "pm", "uninstall", "-k", "--user", "0", packageName};
        String response = runCommand(command);

        return response != null &&
                response.equals("Success");
    }

    private static boolean checkADBInstalled() {
        String[] command = {"adb", "--version"};
        String response = runCommand(command);

        return response != null && response.contains("Android Debug Bridge");
    }

    private static void exit(int statusCode) {
        System.out.println("The program will now exit.");
        System.exit(statusCode);
    }
}
