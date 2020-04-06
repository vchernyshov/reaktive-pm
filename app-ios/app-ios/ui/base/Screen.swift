//
//  Screen.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 01.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryRpm

class Screen<PM: ScreenPresentationModel>: PmController<PM>, BackHandler {
    
    override func onBindPm(_ pm: PM) {
        pm.errorDialog.bindTo(parent: self, createDialog: { message,_ in
            let alert = UIAlertController(
                title: "Error",
                message: message as! String,
                preferredStyle: .alert
            )
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertAction.Style.default, handler: nil))
            return alert
        })
    }
    
    func handleBack() -> Bool {
        pm.backAction.consumer.onNext(value: KotlinUnit.self)
        return true
    }
}
