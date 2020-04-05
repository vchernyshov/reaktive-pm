//
//  Extensions.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 31.03.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

extension UIActivityIndicatorView {
    func visibility() -> ConsumerWrapper<KotlinBoolean> {
        return BindingsKt.visibility(self)
    }
}

extension UILabel {
    func text() -> ConsumerWrapper<NSString> {
        return BindingsKt.text(self)
    }
}

extension UIControl {
    func clicks() -> ReaktiveObservableWrapper<KotlinUnit> {
        return WbindingsKt.wclicks(self)
    }
    
    func enabled() -> ConsumerWrapper<KotlinBoolean> {
        return BindingsKt.enabled(self)
    }
}

extension ReaktiveObservableWrapper {
    @objc func bindTo(_ action: Action<T>) {
        ActionKt.bindTo(self, action: action as! Action<AnyObject>)
    }
}

extension State {
    @objc func bindTo(_ consumer: ConsumerWrapper<T>) {
        bindTo(consumer__: consumer as! ConsumerWrapper<AnyObject>)
    }
}

extension Command {
    @objc func bindTo(_ consumer: ConsumerWrapper<T>) {
        bindTo(consumer__: consumer as! ConsumerWrapper<AnyObject>)
    }
}

extension InputControl {
    func bindTo(_ field: UITextField) {
        self.bindTo(textField: field)
    }
}

extension CheckControl {
    func bindTo(_ sw: UISwitch) {
        self.bindTo(switch: sw)
    }
}
