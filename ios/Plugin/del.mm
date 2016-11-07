//
//  del.m
//  Plugin
//
//  Created by Scott Harrrison2 on 11/4/16.
//
//

#import "del.h"
#import "CoronaLua.h"
#import "CoronaRuntime.h"
#import <UIKit/UIKit.h>
#import "Analytics/FirebaseAnalytics.h"
#import "Core/FirebaseCore.h"
#import "ID/FirebaseInstanceID.h"
@implementation myAppDelegate 
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // [START configure]
    [FIRApp configure];
    // [END configure]
    
    return YES;
}
@end
