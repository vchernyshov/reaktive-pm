//
//  PmController.swift
//
//  Created by Volodymyr Chernyshov on 30.03.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

class PmController<PM: PresentationModel>: UIViewController, PmView {

    private lazy var delegate: PmUiViewControllerDelegate = {
        return PmUiViewControllerDelegate<PresentationModel, PmController>(pmController: self, pmView: self)
    }()

    internal var presentationModel: PresentationModel {
        get {
            return self.delegate.presentationModel
        }
    }

    internal func onBindPresentationModel(pm: PresentationModel) {
        onBindPm(pm as! PM)
    }

    internal func onUnbindPresentationModel() {
        onUnbindPm()
    }

    internal func providePresentationModel() -> PresentationModel {
        return providePm()
    }

    var pm: PM {
        get {
            return presentationModel as! PM
        }
    }

    func onBindPm(_ pm: PM) {

    }

    func onUnbindPm() {

    }

    func providePm() -> PM {
        fatalError("Not implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        delegate.viewDidLoad()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        delegate.viewWillAppear()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        delegate.viewDidAppear()
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        delegate.viewWillDisappear()
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        delegate.viewDidDisappear()
    }


    deinit {
        delegate.deinit()
    }
}
