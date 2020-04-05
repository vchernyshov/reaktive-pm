//
//  ChooseCountryScreen.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 04.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

class ChooseCountryScreen: Screen<ChooseCountryPm>, UISearchResultsUpdating {
    
    @IBOutlet weak var countriesView: UICollectionView!
    private var adapter = CellAdapter()
        
    override func viewDidLoad() {
        super.viewDidLoad()
        adapter.attachToCollectionView(countriesView)
        adapter.setClickListener { country in
            self.pm.countryClicks.consumer.onNext(value: country)
        }
        let search = UISearchController(searchResultsController: nil)
        search.searchResultsUpdater = self
        search.obscuresBackgroundDuringPresentation = false
        search.searchBar.placeholder = "Type country name to search"
        navigationItem.searchController = search
    }
    
    override func providePm() -> ChooseCountryPm {
        return ChooseCountryPm(phoneUtil: AppDelegate.mainComponent.phoneUtil)
    }

    override func onBindPm(_ pm: ChooseCountryPm) {
        super.onBindPm(pm)
        pm.countries.bindTo(consumer: { countries in
            self.adapter.setCountries(countries as! [Country])
        })
    }
    
    func updateSearchResults(for searchController: UISearchController) {
        guard let text = searchController.searchBar.text else { return }
        pm.searchQueryInput.textChanges.consumer.onNext(value: text)
    }
    
    static func newInstance() -> ChooseCountryScreen {
        let storyboard = UIStoryboard.init(name: "ChooseCountryStoryboard", bundle: nil)
        return storyboard.instantiateViewController(identifier: "ChooseCountryStoryboard")
    }
}

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
