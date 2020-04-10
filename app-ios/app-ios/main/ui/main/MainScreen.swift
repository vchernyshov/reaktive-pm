//
//  MainScreen.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 03.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryRpm

class MainScreen: Screen<MainPm> {
    
    @IBOutlet weak var logoutView: UIButton!
    @IBOutlet weak var progressView: UIActivityIndicatorView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Main Sample"
    }
    
    override func providePm() -> MainPm {
        return MainPm(authModel: AppDelegate.mainComponent.authModel)
    }
    
    override func onBindPm(_ pm: MainPm) {
        super.onBindPm(pm)
        pm.inProgress.bindTo(progressView.animation())
        pm.logoutDialog.bindTo(parent: self, createDialog: { _, dc in
            let alert = UIAlertController(
                title: "Logout",
                message: "Are you sure you want to log out?",
                preferredStyle: .alert
            )
            alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { _ in dc.sendResult(result: MainPm.DialogResultOk())}))
            alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: { _ in dc.sendResult(result: MainPm.DialogResultCancel())}))
            
            return alert
        })
        logoutView.clicks().bindTo(pm.logoutClicks)
    }
    
    static func newInstance() -> MainScreen {
        let storyboard = UIStoryboard.init(name: "MainScreenStoryboard", bundle: nil)
        return storyboard.instantiateViewController(identifier: "MainScreenStoryboard")
    }
}
