//
//  UIControl+Binding.swift
//
//  Created by Volodymyr Chernyshov on 06.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

/// Extension to UIControl that provide reactive interface to its properties.
public extension UIControl {

    /// Allows to get clicks in reactive way.
    /// ```
    /// controlView.clicks().bindTo(pm.clicks)
    /// ```
    /// - Returns: A ObservableWrapper<KotlinUnit>
    public func clicks() -> ObservableWrapper<KotlinUnit> {
        return ObservableWrapper(inner: BindingsKt.clicks(self))
    }

    /// Allows to change enable/disable state in reactive way.
    /// ```
    /// pm.buttonEnabled.bindTo(controlView.enabled())
    /// ```
    /// - Returns: A ConsumerWrapper<KotlinBoolean>
    public func enabled() -> ConsumerWrapper<KotlinBoolean> {
        return BindingsKt.enabled(self)
    }
}
