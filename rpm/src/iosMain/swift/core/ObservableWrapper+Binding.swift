//
//  ObservableWrapper+Binding.swift
//
//  Created by Volodymyr Chernyshov on 06.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

public extension ObservableWrapper {
    @objc public func bindTo(_ action: Action<T>) {
        ActionKt.bindTo(self, action: action as! Action<AnyObject>)
    }
}