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
 * Created by Stuart Douglas (sdouglas@macadamian.com) on June 11, 2015.
 * Copyright (c) 2015 Macadamian. All rights reserved.
 */

#import <Cordova/CDV.h>

@class BUBasicController;

@interface BlinkUpPlugin : CDVPlugin

//------------------------------------------------------
// Shows BlinkUp UI for user to enter wifi details and
// perform the screen flash process to connect to an Imp
//------------------------------------------------------
- (void)invokeBlinkUp:(CDVInvokedUrlCommand *)command;
- (void)abortBlinkUp:(CDVInvokedUrlCommand *)command;
- (void)clearBlinkUpData:(CDVInvokedUrlCommand *)command;

// instance variables
@property BUBasicController *blinkUpController;
@property NSString *apiKey;
@property NSString *callbackId;
@property NSString *developerPlanId;
@property NSInteger timeoutMs;
@property BOOL generatePlanId;

@end
