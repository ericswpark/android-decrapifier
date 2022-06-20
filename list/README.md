# Creating a new list

## Using ADB

1. Enable ADB on the device you wish to create a list for. From now on, we will simply refer to this device as the target device.
2. Connect the device to your computer.
3. When prompted, authorize the connection on the target device. (You may wish to remember the authorization if you have connected your target device to a trusted computer that only you have access to.)
4. Run `adb shell pm list packages`

This prints out all the apps installed on your target device. You will have to correlate them with which apps you want removed from your system, which may be done with the second method...

## Using an application info app

There are many apps available on the Play Store that lists what apps are installed in your system partition.

For an open source alternative, there is the [App Manager available on F-Droid.](https://f-droid.org/en/packages/io.github.muntashirakon.AppManager/) (Note that the author has not personally tested this app, so users beware!)

# Guidelines

Please keep the following in mind when creating a new list:
- Test on the latest firmware available for a given device
- Do not include apps that can be disabled/deleted from the application manager.
- Do not include apps that do not come installed by default, _unless_ a system stub app installs the application after
  initial setup.


# Supported devices

- LG (lge)
  - Q9 (LGU+, falcon)
- Samsung (samsung)
  - Galaxy Note 3 (SKT, hlteskt)
  - Galaxy Note 3 Neo (KT, frescoltektt)
  - Galaxy J5 (2015, j5ltekx)
  - Galaxy S8 Plus (KT, dream2lteks)
  - Galaxy Note 20 Ultra 5G (c2q)
  - Galaxy Tab A 10.1 (2016, gtaxlltekx)
