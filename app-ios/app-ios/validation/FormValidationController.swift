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

class FormValidationController: PmController<FormValidationPm> {
    
    @IBOutlet weak var nameView: UITextField!
    @IBOutlet weak var emailView: UITextField!
    @IBOutlet weak var phoneView: UITextField!
    @IBOutlet weak var passwordView: UITextField!
    @IBOutlet weak var confirmPasswordView: UITextField!
    @IBOutlet weak var acceptView: UISwitch!
    @IBOutlet weak var validateView: UIButton!
    
    override func providePm() -> FormValidationPm {
        return FormValidationPm(phoneUtil: AppDelegate.mainComponent.phoneUtil)
    }
    
    override func onBindPm(_ pm: FormValidationPm) {
        pm.name.bindTo(nameView)
        pm.email.bindTo(emailView)
        pm.phone.bindTo(phoneView)
        pm.password.bindTo(passwordView)
        pm.confirmPassword.bindTo(confirmPasswordView)
        pm.termsCheckBox.bindTo(acceptView)
        pm.acceptTermsOfUse.bindTo(consumer: { message in
            self.showToast(controller: self, message: message as! String, seconds: 2.0)
        })
        validateView.clicks().bindTo(pm.validateButtonClicks)
    }
    
    func showToast(controller: UIViewController, message : String, seconds: Double) {
        let alert = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        alert.view.backgroundColor = UIColor.black
        alert.view.alpha = 0.6
        alert.view.layer.cornerRadius = 15

        controller.present(alert, animated: true)

        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + seconds) {
            alert.dismiss(animated: true)
        }
    }
    
    static func newInstance() -> FormValidationController {
        let storyboard = UIStoryboard.init(name: "FormValidationStoryboard", bundle: nil)
        return storyboard.instantiateViewController(identifier: "FormValidationStoryboard")
    }
}
