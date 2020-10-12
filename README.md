# android-decrapifier

Java program to help automate package removal/deletion

## Usage

Refer to the example package lists in `example/`.

Force delete means the package should be removed, not disabled. It is best to leave this as false, unless your device re-enables those packages periodically. If this happens, set the force deletion option to true.

## FAQ

Q: How do I re-enable packages?

A: Run

    adb shell pm enable package.identifier.here

Note that this is not possible for packages that were force-deleted. If you want to restore these, factory reset your device.

