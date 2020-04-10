//
//  CellAdapter.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 10.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

class CellAdapter: NSObject, UICollectionViewDelegate, UICollectionViewDataSource {
    
    private var countries : [Country] = []
    private weak var view: UICollectionView!
    private var clickListener: ((Country) -> Void)? = nil
   
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return countries.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "CountryCell", for: indexPath) as! CountryCell
        let country = countries[indexPath.row]
        cell.bind(country)
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        clickListener?.self(countries[indexPath.item])
    }
    
    func attachToCollectionView(_ view: UICollectionView) {
        self.view = view
        self.view.dataSource = self
        self.view.delegate = self
    }
    
    func setClickListener(_ listener: @escaping (Country) -> Void) {
        self.clickListener = listener
    }
    
    func setCountries(_ countries: [Country]) {
        self.countries = countries
        self.view.reloadData()
    }
}
