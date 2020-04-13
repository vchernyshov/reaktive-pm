//
//  CountryCell.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 04.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

class CountryCell: UICollectionViewCell {

    @IBOutlet weak var countryTitle: UILabel!
    @IBOutlet weak var countryCode: UILabel!
    
    func bind(_ country: Country) {
        self.countryTitle.text = country.name
        self.countryCode.text = "+\(country.countryCallingCode)"
    }
}
