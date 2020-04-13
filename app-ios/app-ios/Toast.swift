//
//  Toast.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 10.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit

extension UIViewController {
    func showToast(message : String, duration: Duration = .short) {
        let alert = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        self.present(alert, animated: true)
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + duration.rawValue) {
            alert.dismiss(animated: true)
        }
    }
    
    enum Duration: Double {
        case short = 2.0
        case long = 3.5
    }
}
