//
//  CodeConfirmationScreen.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 03.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryRpm

class CodeConfirmationScreen: Screen<CodeConfirmationPm> {
    
    var phone: String = ""
    
    @IBOutlet weak var codeView: UITextField!
    @IBOutlet weak var sendCodeView: UIButton!
    @IBOutlet weak var progressView: UIActivityIndicatorView!
    
    override func providePm() -> CodeConfirmationPm {
        return CodeConfirmationPm(
            phone: self.phone,
            resources: AppDelegate.mainComponent.resources,
            authModel: AppDelegate.mainComponent.authModel
        )
    }
    
    override func onBindPm(_ pm: CodeConfirmationPm) {
        super.onBindPm(pm)
        pm.inProgress.bindTo(progressView.animation())
        pm.code.bindTo(codeView)
        pm.sendButtonEnabled.bindTo(sendCodeView.enabled())
        sendCodeView.clicks().bindTo(pm.sendClicks)
        // TODO: add merge click from
    }
    
    static func newInstance(_ phone: String) -> CodeConfirmationScreen {
        let storyboard = UIStoryboard.init(name: "CodeConfirmationStoryboard", bundle: nil)
        let controller = storyboard.instantiateViewController(identifier: "CodeConfirmationStoryboard") as CodeConfirmationScreen
        controller.phone = phone
        return controller
    }
}
