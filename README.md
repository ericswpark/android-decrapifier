# android-decrapifier

Java program to help automate package removal/deletion

Thanks to XDA and Reddit users that found the ADB remove/disable commands!

## Usage

[Download the latest release](https://github.com/ericswpark/android-decrapifier/releases) and run:

    java -jar android_decrapifier.jar

Refer to the example package lists in `example/`. There are a couple of pre-made package lists that you may want to use. If there is no pre-made package list for your device, create your own CSV following the format from the example directory.

Force delete means the package should be removed, not disabled. It is best to leave this as false, unless your device re-enables those packages periodically. If this happens, set the force deletion option to true.

## FAQ

### Q: How do I re-enable packages?

A: Run

    adb shell pm enable package.identifier.here

Note that this is not possible for packages that were force-deleted. If you want to restore these, factory reset your device.

### Q: Some packages give me a failure result. Why?

A: While testing, I've found that Android Lollipop (or older) devices only support removing apps, not disabling them. If this is the case, try changing force delete to true.

### Q: After running, some apps still remain. Why?

Some apps have updates applied on top (such as stub apps from Tripadvisor or Flipboard). These apps require multiple runs to be deleted completely. Try running the removal program again until the app goes away completely.

### Q: Can you add support for my device too?

A: Make a .csv file (look at the example) and submit a pull request!