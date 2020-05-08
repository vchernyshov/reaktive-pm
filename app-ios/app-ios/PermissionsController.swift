//
//  PermissionsController.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 08.05.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryRpm

class PermissionsController: PmController<PermissionsPm> {
        
    @IBOutlet weak var cameraItemView: UIStackView!
    @IBOutlet weak var galletyItemView: UIStackView!
    @IBOutlet weak var storageItemView: UIStackView!
    @IBOutlet weak var locationItemView: UIStackView!
    @IBOutlet weak var coarseLocationItemView: UIStackView!
    @IBOutlet weak var bleItemView: UIStackView!
    @IBOutlet weak var remoteNotification: UIStackView!
    
    override func providePm() -> PermissionsPm {
        return PermissionsPm()
    }
    
    override func onBindPm(_ pm: PermissionsPm) {
        bindItem(cameraItemView, pm.cameraCheckAction, pm.cameraStatus, pm.cameraPermission)
        bindItem(galletyItemView, pm.galleryCheckAction, pm.galleryStatus, pm.galleryPermission)
        bindItem(storageItemView, pm.storageCheckAction, pm.storageStatus, pm.storagePermission)
        bindItem(locationItemView, pm.locationCheckAction, pm.locationStatus, pm.locationPermission)
        bindItem(coarseLocationItemView, pm.coarseLocationCheckAction , pm.coarseLocationStatus, pm.coarseLocationPermission)
        bindItem(bleItemView, pm.bleCheckAction, pm.bleStatus, pm.blePermission)
        bindItem(remoteNotification, pm.remoteNotificationCheckAction, pm.remoteNotificationStatus, pm.remoteNotificationPermission)
    }
    
    private func bindItem(_ view: UIStackView, _ action: Action<KotlinUnit>, _ status: State<NSString>, _ control: PermissionControl) {
        control.bind()
        (view.arrangedSubviews[0] as! UIButton).clicks().bindTo(action)
        status.bindTo((view.arrangedSubviews[1] as! UILabel).text())
    }
    
    static func newInstance() -> PermissionsController {
        let storyboard = UIStoryboard.init(name: "PermissionsStoryboard", bundle: nil)
        return storyboard.instantiateViewController(identifier: "PermissionsStoryboard")
    }
}
