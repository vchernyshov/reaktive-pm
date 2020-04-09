//
//  UIActivityIndicatorView+Binding.swift
//
//  Created by Volodymyr Chernyshov on 06.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

public extension UIActivityIndicatorView {
    public func animation() -> ConsumerWrapper<KotlinBoolean> {
        return BindingsKt.animation(self)
    }
}