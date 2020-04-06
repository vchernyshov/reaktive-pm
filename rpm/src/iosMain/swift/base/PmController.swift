//
//  PmController.swift
//
//  Created by Volodymyr Chernyshov on 30.03.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

open class PmController<PM: PresentationModel>: UIViewController, PmView {

    private lazy var delegate: PmUiViewControllerDelegate = {
        return PmUiViewControllerDelegate<PresentationModel, PmController>(pmController: self, pmView: self)
    }()

    public var presentationModel: PresentationModel {
        get {
            return self.delegate.presentationModel
        }
    }

    public func onBindPresentationModel(pm: PresentationModel) {
        onBindPm(pm as! PM)
    }

    public func onUnbindPresentationModel() {
        onUnbindPm()
    }

    public func providePresentationModel() -> PresentationModel {
        return providePm()
    }

    public var pm: PM {
        get {
            return presentationModel as! PM
        }
    }

    open func onBindPm(_ pm: PM) {

    }

    open func onUnbindPm() {

    }

    open func providePm() -> PM {
        fatalError("Not implemented")
    }

    override open func viewDidLoad() {
        super.viewDidLoad()
        delegate.viewDidLoad()
    }

    override open func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        delegate.viewWillAppear()
    }

    override open func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        delegate.viewDidAppear()
    }

    override open func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        delegate.viewWillDisappear()
    }

    override open func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        delegate.viewDidDisappear()
    }

    deinit {
        delegate.deinit()
    }
}
