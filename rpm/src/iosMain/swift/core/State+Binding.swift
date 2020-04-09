//
//  State+Binding.swift
//
//  Created by Volodymyr Chernyshov on 06.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

public extension State {
     @objc public func bindTo(_ consumer: ConsumerWrapper<T>) {
         bindTo(consumer__: consumer as! ConsumerWrapper<AnyObject>)
     }
}