Cordova-BlinkUpSample
===========
Sample application that demonstrates how to use the [BlinkUp Cordova plugin](https://github.com/Macadamian/Cordova-BlinkUpPlugin) to integrate Electric Imp's BlinkUp SDK with Cordova applications for iOS and Android. Refer to the [plugin repository](https://github.com/Macadamian/Cordova-BlinkUpPlugin) for more information about the plugin and how to install it in your own project.

**Note**: this repository simply shows an example of how you can integrate the plugin and interface with it from a Cordova project. Any modifications to the native plugin code should be done on the plugin repo at https://github.com/Macadamian/Cordova-BlinkUpPlugin.

Electric Imp is an Internet of Things platform used from prototyping to production. For more information about Electric Imp, visit: https://electricimp.com/. 

Prerequisites
===========
An Electric Imp Board development kit. For more information on how to obtain a development kit, please visit: https://electricimp.com/docs/gettingstarted/devkits/ 

An Electric Imp developer account. To register for an account, please visit: https://ide.electricimp.com/

A BlinkUp SDK and API Key. For more information on how to obtain a license, please visit: https://electricimp.com/docs/manufacturing/blinkup_faqs/

Installation
===========
Open `CordovaBlinkUpSample/www/js/index.js` and set your API key from Electric Imp. You can optionally set your development plan ID as well, this will let you see Imps you've blinked up with in your IDE. Please read [testing the plugin](#testing-the-plugin) before setting a development plan ID, as it can prevent users from connecting to wifi if it makes it into production code.

When building in Xcode or Android Studio, this `index.js` file overwrites the native platform's `index.js` file, propagating your changes down to both platforms.

**iOS Instructions**<br>
Copy the `BlinkUp.embeddedframework` folder to `path/to/Cordova-BlinkUpSample/platforms/ios/CordovaBlinkUpSample/Frameworks` folder.

**Android Instructions**<br>
Copy the `blinkup_sdk` folder from the BlinkUp SDK to `path/to/Cordova-BlinkUpSample/platforms/android`. 

Updating the Plugin
===========
To update the native plugin files to the latest from the [plugin repo](https://github.com/Macadamian/Cordova-BlinkUpPlugin), simply run the update script, `UpdatePlugin.sh`. This will overwrite all the native plugin files in `platforms/ios/CordovaBlinkUpSample/Plugins/com.macadamian.blinkup` and `platforms/android/src/com/macadmian/blinkup`. If you have made changes to any of those files, do not update. It is instead recommended that you clone the [plugin repo](https://github.com/Macadamian/Cordova-BlinkUpPlugin) and make changes off of that.

Testing the Plugin
===========
If you are testing devices for development, you can input your own development planID to see the Imps in the Electric Imp IDE. Just set it at the top of the `index.js` files and ensure you pass *false* for `generateNewPlanId`. The development plan ID you set will override a cached plan ID (if there is one) if `generateNewPlanId` is false. If `generateNewPlanId` is true however, a new plan ID will be generated every BlinkUp and the development plan ID will be ignored.

When you pass in a development plan ID, the plugin will not cache it. Caching is only done on production plan ID's, and is used to save user settings across BlinkUp's (e.g. when they change their wifi password).

IMPORTANT NOTE: if a development plan ID makes it into production, the consumer's device will not configure, and will be unable to connect to wifi. There is a check in the native code on each platform which will ignore a development plan ID if the build configuration is set to release, but it is best to remove all references to the plan ID and pass an empty string from the Javascript when you're done debugging. Please read http://electricimp.com/docs/manufacturing/planids/ for more info.


Project Structure
===========
Refer to `www/index.js` for an example of how to call the plugin that initiates the native BlinkUp process. 

The native code that interfaces with the plugin (and sends back the device info) can be found at `platforms/android/src/com/macadamian/blinkup` and `platforms/ios/CordovaBlinkUpExample/Plugins/com.macadamian.blinkup`.

JSON Format
-----------
The plugin will return a JSON string in the following format to the callback set in `www/index.js` (this is `blinkupCallback` for Cordova-BlinkUpSample). Footnotes in square brackets.
```
{
    "state": "started" | "completed" | "error", [1]
    "statusCode": "",                           [2]
    "error": {                                  [3]
        "errorType": "plugin" | "blinkup",      [4]
        "errorCode": "",                        [5]
        "errorMsg": ""                          [6]
    },
    "deviceInfo": {                             [7]
        "deviceId": "",
        "planId": "",
        "agentURL": "",
        "verificationDate": ""
    }
 }
```
[1] - *started*: flashing process has finished, waiting for device info from Electric Imp servers<br>
*completed*: Plugin done executing. This could be a clear-wifi completed or device info from servers has arrived<br>
[2] - Status of plugin. Null if state is "error". See "Status Codes" below for status codes.<br>
[3] - Stores error information if state is "error". Null if state is "started" or "completed".<br>
[4] - If error sent from SDK, "blinkup". If error handled within native code of plugin, "plugin".<br>
[5] - BlinkUp SDK error code if errorType is "blinkup". Custom error code if "plugin". See "Error Codes" below for custom error codes.<br>
[6] - If errorType is "blinkup", error message from BlinkUp SDK. Null if errorType "plugin".<br>
[7] - Stores the device info from the Electric Imp servers. Null if state is "started" or "error".

Status Codes
-----------
These codes can be used to debug your application, or to present the users an appropriate message on success.
```
0   - "Device Connected"
200 - "Gathering device info..."
201 - "Wireless configuration cleared."
202 - "Wireless configuration and cached Plan ID cleared."
```

Error Codes
----------
IMPORTANT NOTE: the following codes apply ONLY if `errorType` is "plugin". Errors from the BlinkUp SDK will have their own error codes (which may overlap with those below). If `errorType` is "blinkup", you must use the `errorMsg` field instead. The errors in the 300's range are android only.
```
100 - "Invalid arguments in call to invokeBlinkUp."
101 - "Could not gather device info. Process timed out."
102 - "Process cancelled by user."
300 - "Invalid API key. You must set your BlinkUp API key in www/index.js." 
301 - "Could not verify API key with Electric Imp servers."
302 - "Error generating JSON string."
```

