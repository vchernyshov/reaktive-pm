//
//  MainCompoment.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 04.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import MultiPlatformLibrary


class MainComponent {
    
    private let notificationManager: NotificationManager
    private let serverApi: ServerApi
    private let tokenStorage: TokenStorage
    
    let authModel: AuthModel
    let phoneUtil: PhoneUtil
    let resources: Resources
    
    init() {
        self.notificationManager = IosNotificationManager()
        self.serverApi = ServerApiSimulator(notificationManager: notificationManager)
        self.tokenStorage = TokenStorage()
        self.authModel = AuthModel(api: serverApi, tokenStorage: tokenStorage)
        self.phoneUtil = IosPhoneUtil()
        self.resources = Resources()
    }
    
}
