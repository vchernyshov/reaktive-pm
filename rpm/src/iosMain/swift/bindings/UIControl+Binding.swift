//
//  UIControl+Binding.swift
//
//  Created by Volodymyr Chernyshov on 06.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

public extension UIControl {
    public func clicks() -> ObservableWrapper<KotlinUnit> {
        return ObservableWrapper(inner: BindingsKt.clicks(self))
    }

    public func enabled() -> ConsumerWrapper<KotlinBoolean> {
        return BindingsKt.enabled(self)
    }
}
