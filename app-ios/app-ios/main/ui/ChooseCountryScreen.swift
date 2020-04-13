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
        title = "Choose country"
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
