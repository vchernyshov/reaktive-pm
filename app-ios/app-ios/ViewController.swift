//
//  ViewController.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 26.03.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print(self)
        print(parent)
    }
    
    @IBAction func onCounterClicked(_ sender: UIButton) {
        self.navigationController?.pushViewController(CounterController.newInstance(), animated: false)
    }
    
    @IBAction func onMainClicked(_ sender: UIButton) {
        self.navigationController?.pushViewController(AuthByPhoneScreen.newInstance(), animated: false)
    }
    
    @IBAction func onFormClicked(_ sender: UIButton) {
        self.navigationController?.pushViewController(FormValidationController.newInstance(), animated: false)
    }
}


