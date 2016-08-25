# KonnektingSuite
Graphical desktop application to program a KONNEKTING Device



## How to program a device

With the "Suite" you can program an individual address (aka. physical address/PA) as well as group-addresses (GA) and device parameters (means: device settings). It's currently *not* possible to program the "firmware or sketch" of an arduino to a device. 

There are currently 4 programming modes in the suite available:

* red button: programs all: the individual address, groupaddress assignments and parameters. REQUIRES PROG-BUTTON PRESS ON DEVICE!
* orange button: like the red button, but without the individual address
* yellow button: just the group address assignments
* green button: just the parameters

The first time you program a device, the device needs an individual address. Otherwise you cannot do anything with it, as it is not "adressable". And as it is not adressable if it's not programmed at all, you need to press the "prog button" on the device, so that the suite is able to find it on the bus.

So, starting with your first programming of a device, follow this steps:

1. Configure a individual address for the device in the suite (Tab 'Device')
2. Give the required communication objects group addresses (Tab 'Communication Objects')
3. Check and set parameters of the device (Tab 'Parameters')
4. Press the PROG BUTTON on the device. The PROG LED on the device must turn ON.
5. Press the "red button" in the suite

That's all. The Suite will search for the device and programm it.
On further changes, you can use the other buttons to update the device programming. Unless you change the individual address, you don't habe to press the PROG BUTTON on your device. The Suite is able to directly address the device.


