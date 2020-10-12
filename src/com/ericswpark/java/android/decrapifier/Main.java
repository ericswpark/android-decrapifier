package com.ericswpark.java.android.decrapifier;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final String DELIMITER = ",";

    public static void main(String[] args) {
        System.out.println("Android Decrapifier");
        System.out.print("What package list would you like to use? (format: codename.csv) ");

        Scanner scanner = new Scanner(System.in);
        String packageListDirectory = scanner.nextLine();

        BufferedReader bufferedReader = null;
        ArrayList<AndroidPackage> androidPackages = new ArrayList<>();

        try {
            bufferedReader = new BufferedReader(new FileReader(packageListDirectory));

            String line = "";
            // Skip CSV 1st line
            bufferedReader.readLine();

            while ((line = bufferedReader.readLine()) != null) {
                String[] rawInput = line.split(DELIMITER);

                if(rawInput.length > 0) {
                    AndroidPackage androidPackage = new AndroidPackage(rawInput[0], Boolean.parseBoolean(rawInput[1]),
                            rawInput[2]);
                    androidPackages.add(androidPackage);
                }
            }

            bufferedReader.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("Error: the specified package list file does not exist!");
            System.out.println("The program will now exit.");
            System.exit(1);
        } catch (IOException ioException) {
            System.out.println("Error: the file could not be read.");
            System.out.println("The program will now exit.");
            System.exit(1);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        // Ask before starting
        System.out.printf("Found %d packages to disable/remove. Start? (y/n) ", androidPackages.size());
        String startConfirm = scanner.nextLine();
        if(startConfirm.toUpperCase().equals("Y")) {
            // Start deletion process
            int success = 0;
            int failure = 0;

            for (AndroidPackage androidPackage : androidPackages) {
                boolean result = false;
                if (!androidPackage.isForceDelete()) {
                    result = disablePackage(androidPackage.getPackageIdentifier());
                } else {
                    result = removePackage(androidPackage.getPackageIdentifier());
                }

                if (result) {
                    System.out.printf("%s - successfully disabled/removed!%n", androidPackage.getPackageIdentifier());
                    success++;
                } else {
                    System.out.printf("%s - failed to disable/remove. It may be deleted/disabled already.%n",
                            androidPackage.getPackageIdentifier());
                    failure++;
                }
            }

            System.out.printf("Finished! Successfully disabled/removed %d packages, failed %d, total %d%n", success,
                    failure, androidPackages.size());
        } else {
            System.out.println("OK, deletion canceled.");
            System.out.println("The program will now exit.");
        }
    }

    private static boolean disablePackage(String packageName) {
        Runtime runtime = Runtime.getRuntime();

        String[] command = {"adb", "shell", "pm", "disable-user", "--user", "0", packageName};

        try {
            Process process = runtime.exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String response = reader.readLine();

            if(response != null && response.equals(String.format("Package %s new state: disabled-user", packageName)))
                return true;

            reader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean removePackage(String packageName) {
        Runtime runtime = Runtime.getRuntime();

        String[] command = {"adb", "shell", "pm", "uninstall", "-k", "--user", "0", packageName};

        try {
            Process process = runtime.exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String response = reader.readLine();

            if(response.equals("Success"))
                return true;

            reader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
