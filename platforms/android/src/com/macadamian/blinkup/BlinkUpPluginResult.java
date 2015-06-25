/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by Stuart Douglas (sdouglas@macadamian.com) on June 18, 2015.
 * Copyright (c) 2015 Macadamian. All rights reserved.
 */

package com.macadamian.blinkup;

import android.util.Log;

import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

public class BlinkUpPluginResult {

    /******** JSON Format ******************************
    {
        "state": "started" | "completed" | "error",
        "statusCode": "",                           [1]
        "error": {                                  [2]
            "errorType": "plugin" | "blinkup",      [3]
            "errorCode": "",                        [4]
            "errorMsg": ""                          [5]
        },
        "deviceInfo": {                             [6]
            "deviceId": "",
            "planId": "",
            "agentURL": "",
            "verificationDate": ""
        }
    }
    // [1] - null if error, see readme for status codes
    // [2] - null if "started" or "completed"
    // [3] - if error from BUErrors.h, "blinkup",
             otherwise "plugin"
    // [4] - NSError code if "blinkup", custom error code
             if "plugin". See readme for custom errors.
    // [5] - null if errorType "plugin"
    // [6] - null if "started" or "error"
    ****************************************************/

    // possible states
    public enum BlinkUpPluginState {
        Started("started"),
        Completed("completed"),
        Error("error");

        private final String state;
        BlinkUpPluginState(String state) { this.state = state; }
        public String getKey() { return this.state; }
    }

    // possible error types
    private enum BlinkUpErrorType {
        BlinkUpSDKError("blinkup"),
        PluginError("plugin");

        private final String type;
        BlinkUpErrorType(String type) { this.type = type; }
        public String getType() { return this.type; }
    }

    //=====================================
    // JSON keys for results
    //=====================================
    private enum ResultKeys {
        STATE("state"),
        STATUS_CODE("statusCode"),

        ERROR("error"),
        ERROR_TYPE("errorType"),
        ERROR_CODE("errorCode"),
        ERROR_MSG("errorMsg"),

        DEVICE_INFO("deviceInfo"),
        DEVICE_ID("deviceId"),
        PLAN_ID("planId"),
        AGENT_URL("agentURL"),
        VERIFICATION_DATE("verificationDate");

        private final String key;
        ResultKeys(String key) { this.key = key; }
        public String getKey() { return this.key; }

    }

    //====================================
    // BlinkUp Results
    //====================================
    private BlinkUpPluginState state;
    private int statusCode;
    private BlinkUpErrorType errorType;
    private int errorCode;
    private String errorMsg;

    private String deviceId;
    private String planId;
    private String agentURL;
    private String verificationDate;

    //====================================
    // Setters for our Results
    //====================================
    public void setState(BlinkUpPluginState state) {
        this.state = state;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public void setPluginError(int errorCode) {
        this.errorType = BlinkUpErrorType.PluginError;
        this.errorCode = errorCode;
    }
    public void setBlinkUpError(String errorMsg) {
        this.errorType = BlinkUpErrorType.BlinkUpSDKError;
        this.errorCode = 1;
        this.errorMsg = errorMsg;
    }
    public void setDeviceInfoAsJson(JSONObject deviceInfo) {
        try {
            this.deviceId = (deviceInfo.getString("impee_id") != null) ? deviceInfo.getString("impee_id").trim() : null;
            this.planId = deviceInfo.getString("plan_id");
            this.agentURL = deviceInfo.getString("agent_url");
            this.verificationDate = deviceInfo.getString("claimed_at");
        } catch (JSONException e) {
            Log.e("BlinkUpPlugin", "Error parsing device info JSON.");
            e.printStackTrace();
        }
    }

    public void sendResultsToCallback() {
        JSONObject resultJSON = new JSONObject();

        // set result status
        PluginResult.Status resultStatus;
        if (this.state == BlinkUpPluginState.Error) {
            resultStatus = PluginResult.Status.ERROR;
        }
        else {
            resultStatus = PluginResult.Status.OK;
        }

        try {
            // set our state (never null)
            resultJSON.put(ResultKeys.STATE.getKey(), this.state.getKey());

            // error
            if (this.state == BlinkUpPluginState.Error) {
                JSONObject errorJson = new JSONObject();
                errorJson.put(ResultKeys.ERROR_CODE.getKey(), String.valueOf(this.errorCode));
                if (this.errorMsg != null) {
                    errorJson.put(ResultKeys.ERROR_MSG.getKey(), this.errorMsg);
                }
                resultJSON.put(ResultKeys.ERROR.getKey(), errorJson);
            }

            // success
            else {
                resultJSON.put(ResultKeys.STATUS_CODE.getKey(), String.valueOf(statusCode));

                if (this.deviceId != null && this.planId != null
                 && this.agentURL != null && this.verificationDate != null) {
                    JSONObject deviceInfoJson = new JSONObject();
                    deviceInfoJson.put(ResultKeys.DEVICE_ID.getKey(), this.deviceId);
                    deviceInfoJson.put(ResultKeys.PLAN_ID.getKey(), this.planId);
                    deviceInfoJson.put(ResultKeys.AGENT_URL.getKey(), this.agentURL);
                    deviceInfoJson.put(ResultKeys.VERIFICATION_DATE.getKey(), this.verificationDate);
                    resultJSON.put(ResultKeys.DEVICE_INFO.getKey(), deviceInfoJson);
                }
            }
        } catch (JSONException e) {
            Log.e("BlinkUpPlugin", "Error creating result JSON.");
            e.printStackTrace();
        }

        PluginResult pluginResult = new PluginResult(resultStatus, resultJSON.toString());
        pluginResult.setKeepCallback(this.state == BlinkUpPluginState.Started);
        BlinkUpPlugin.callbackContext.sendPluginResult(pluginResult);
    }
}