//
//  NotificationManager.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 04.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

class IosNotificationManager: NotificationManager {
    
    func showNotification(notification: MultiPlatformLibrary.Notification) {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound], completionHandler: {granted,error in
            if granted == true && error == nil {
                let content = UNMutableNotificationContent()
                content.title = notification.title
                content.body = notification.message
                let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 5, repeats: false)
                let request = UNNotificationRequest(identifier: "\(notification.id)", content: content, trigger: trigger)
                UNUserNotificationCenter.current().add(request) { error in
                    guard error == nil else { return }
                    print("Scheduling notification with id: \(notification.id)")
                }
            }
        })
    }
    
    func cancelNotification(id: Int32) {
        UNUserNotificationCenter.current().removeDeliveredNotifications(withIdentifiers: ["\(id)"])
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: ["\(id)"])
    }
    
}
