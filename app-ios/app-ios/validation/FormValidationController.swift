//
//  FormValidationController.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 31.03.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryRpm
import SkyFloatingLabelTextField

class FormValidationController: PmController<FormValidationPm> {
    
    @IBOutlet weak var nameView: SkyFloatingLabelTextField!
    @IBOutlet weak var emailView: SkyFloatingLabelTextField!
    @IBOutlet weak var phoneView: SkyFloatingLabelTextField!
    @IBOutlet weak var passwordView: SkyFloatingLabelTextField!
    @IBOutlet weak var confirmPasswordView: SkyFloatingLabelTextField!
    @IBOutlet weak var acceptView: UISwitch!
    @IBOutlet weak var validateView: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Validation Sample"
        nameView.keyboardType = .namePhonePad
        emailView.keyboardType = .emailAddress
        phoneView.keyboardType = .phonePad
        passwordView.keyboardType = .namePhonePad
        confirmPasswordView.keyboardType = .namePhonePad
        passwordView.isSecureTextEntry = true
        confirmPasswordView.isSecureTextEntry = true
    }
    
    override func providePm() -> FormValidationPm {
        return FormValidationPm(phoneUtil: AppDelegate.mainComponent.phoneUtil)
    }
    
    override func onBindPm(_ pm: FormValidationPm) {
        pm.name.bindTo(layout: nameView)
        pm.email.bindTo(layout: emailView)
        pm.phone.bindTo(layout: phoneView)
        pm.password.bindTo(layout: passwordView)
        pm.confirmPassword.bindTo(layout: confirmPasswordView)
        pm.termsCheckBox.bindTo(acceptView)
        pm.acceptTermsOfUse.bindTo(consumer: { message in
            self.showToast(message: message as! String)
        })
        validateView.clicks().bindTo(pm.validateButtonClicks)
    }
    
    static func newInstance() -> FormValidationController {
        let storyboard = UIStoryboard.init(name: "FormValidationStoryboard", bundle: nil)
        return storyboard.instantiateViewController(identifier: "FormValidationStoryboard")
    }
}
