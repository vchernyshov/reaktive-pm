//
//  ObservableWrapper+Binding.swift
//
//  Created by Volodymyr Chernyshov on 06.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

/// Extension to ObservableWrapper that provide proxy access to binds method in common code.
/// Needed to fix issues with generics and hides explicit class casting.
public extension ObservableWrapper {

    /// Binds this ObservableWrapper to action
    /// ```
    /// controlView.clicks().bindTo(pm.clicks)
    /// ```
    @objc public func bindTo(_ action: Action<T>) {
        ActionKt.bindTo(self, action: action as! Action<AnyObject>)
    }
}