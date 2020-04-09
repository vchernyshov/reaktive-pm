//
//  AuthByPhoneController.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 01.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryRpm

class AuthByPhoneScreen: Screen<AuthByPhonePm>, UITextFieldDelegate {
    
    @IBOutlet weak var nameView: UILabel!
    @IBOutlet weak var codeView: UITextField!
    @IBOutlet weak var phoneView: UITextField!
    @IBOutlet weak var sendView: UIButton!
    @IBOutlet weak var progressView: UIActivityIndicatorView!
    
    override func providePm() -> AuthByPhonePm {
        return AuthByPhonePm(
            phoneUtil: AppDelegate.mainComponent.phoneUtil,
            resources: AppDelegate.mainComponent.resources,
            authModel: AppDelegate.mainComponent.authModel
        )
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        nameView.isUserInteractionEnabled = true
        let guestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(nameViewClicked(_:)))
        nameView.addGestureRecognizer(guestureRecognizer)
    }
    
    override func onBindPm(_ pm: AuthByPhonePm) {
        super.onBindPm(pm)
        pm.countryCode.bindTo(codeView)
        pm.phoneNumber.bindTo(phoneView)
        pm.chosenCountry.bindTo(ConsumerWrapper(inner_: { country in
            self.nameView.text = country?.name
        }))
        pm.inProgress.bindTo(progressView.animation())
        pm.sendButtonEnabled.bindTo(sendView.enabled())
        sendView.clicks().bindTo(pm.sendClicks)
    }
    
    func onCountryChoosen(_ country: Country) {
        pm.chooseCountry.consumer.onNext(value: country)
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.pm.sendClicks.consumer.onNext(value: KotlinUnit())
        return true
    }
    
    @objc func nameViewClicked(_ sender: Any) {
        self.pm.countryClicks.consumer.onNext(value: KotlinUnit())
    }
    
    static func newInstance() -> AuthByPhoneScreen {
        let storyboard = UIStoryboard.init(name: "AuthByPhoneStoryboard", bundle: nil)
        return storyboard.instantiateViewController(identifier: "AuthByPhoneStoryboard")
    }
}
