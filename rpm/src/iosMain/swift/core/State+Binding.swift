//
//  State+Binding.swift
//
//  Created by Volodymyr Chernyshov on 06.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

/// Extension to State that provide proxy access to binds method in common code.
/// Needed to fix issues with generics and hides explicit class casting.
public extension State {

    /// Binds this state to consumer
    /// ```
    /// pm.state.bindTo(consumer)
    /// ```
     @objc public func bindTo(_ consumer: ConsumerWrapper<T>) {
         bindTo(consumer__: consumer as! ConsumerWrapper<AnyObject>)
     }
}