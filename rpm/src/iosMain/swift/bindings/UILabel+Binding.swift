//
//  UILabel+Binding.swift
//
//  Created by Volodymyr Chernyshov on 06.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

/// Extension to UILabel that provide reactive interface to its properties.
public extension UILabel {

    /// Allows to set text in reactive way.
    /// ```
    /// pm.textState.bindTo(labelView.text())
    /// ```
    /// - Returns: A ConsumerWrapper<KotlinBoolean>
    public func text() -> ConsumerWrapper<NSString> {
        return BindingsKt.text(self)
    }
}