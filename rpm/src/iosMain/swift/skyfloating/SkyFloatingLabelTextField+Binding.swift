//
//  SkyFloatingLabelTextField+Binding.swift
//
//  Created by Volodymyr Chernyshov on 06.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary
import SkyFloatingLabelTextField

/// Extension of SkyFloatingLabelTextField that implements TextInputLayout interface(protocol) from common code.
/// Allows to use instance of SkyFloatingLabelTextField with InputControl.
///
/// ```
/// pm.inputControl.bindTo(layout: skyFloatingLabelTextField)
/// ```
extension SkyFloatingLabelTextField: TextInputLayout {
    public func getTextField() -> UITextField {
        return self
    }

    public func setError(error: String?) {
        self.errorMessage = error
    }
}