//
//  ViewController.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 26.03.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

class ViewController: UIViewController {

    var pm: CounterPm = CounterPm()
    
    @IBOutlet weak var minusView: UIButton!
    @IBOutlet weak var plusView: UIButton!
    @IBOutlet weak var counterView: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
    }
}

