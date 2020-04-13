//
//  UIActivityIndicatorView+Binding.swift
//
//  Created by Volodymyr Chernyshov on 06.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

/// Extension to UIActivityIndicatorView that provide reactive interface to its properties.
public extension UIActivityIndicatorView {

    /// Allows to start and stop animation in reactive way.
    /// ```
    /// pm.inProgress.bindTo(progressView.animation())
    /// ```
    /// - Returns: A ConsumerWrapper<KotlinBoolean>
    public func animation() -> ConsumerWrapper<KotlinBoolean> {
        return BindingsKt.animation(self)
    }
}