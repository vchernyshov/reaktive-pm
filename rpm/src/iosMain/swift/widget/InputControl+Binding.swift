//
//  InputControl+Binding.swift
//
//  Created by Volodymyr Chernyshov on 06.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

public extension InputControl {
    public func bindTo(_ field: UITextField) {
        self.bindTo(textField: field)
    }
}