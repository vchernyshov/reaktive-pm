//
//  CounterController.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 31.03.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryRpm

class CounterController: PmController<CounterPm> {
    
    @IBOutlet weak var minusView: UIButton!
    @IBOutlet weak var plusView: UIButton!
    @IBOutlet weak var counterView: UILabel!
    
    override func providePm() -> CounterPm {
        return CounterPm()
    }
    
    override func onBindPm(_ pm: CounterPm) {
        pm.count.bindTo(counterView.text())
        pm.minusButtonEnabled.bindTo(minusView.enabled())
        pm.plusButtonEnabled.bindTo(plusView.enabled())
        minusView.clicks().bindTo(pm.minusButtonClicks)
        plusView.clicks().bindTo(pm.plusButtonClicks)
    }
    
    static func newInstance() -> CounterController {
        let storyboard = UIStoryboard.init(name: "CounterStoryboard", bundle: nil)
        return storyboard.instantiateViewController(identifier: "CounterStoryboard")
    }
} 
