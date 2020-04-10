//
//  AppNavigationController.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 04.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

class AppNavigationController: UINavigationController, NavigationMessageHandler {
    
    func handleNavigationMessage(message: NavigationMessage) -> Bool {
        if message is AppNavigationMessage.Back {
            popViewController(animated: false)
        }
        
        if message is AppNavigationMessage.ChooseCountry {
            pushViewController(ChooseCountryScreen.newInstance(), animated: false)
        }
        
        if let m = message as? AppNavigationMessage.CountryChosen {
            popViewController(animated: false)
            findScreen(type: AuthByPhoneScreen.self)?.onCountryChoosen(m.country)
        }
        
        if message is AppNavigationMessage.LogoutCompleted {
            popBack(toControllerType: ViewController.self)
            pushViewController(AuthByPhoneScreen.newInstance(), animated: false)
        }
        
        if message is AppNavigationMessage.PhoneConfirmed {
            popBack(toControllerType: ViewController.self)
            pushViewController(MainScreen.newInstance(), animated: false)
        }
        
        if let m = message as? AppNavigationMessage.PhoneSentSuccessfully {
            pushViewController(CodeConfirmationScreen.newInstance(m.phone), animated: false)
        }
        
        return true
    }
    
    func findScreen<T: UIViewController>(type: T.Type) -> T? {
        if var viewControllers: [UIViewController] = viewControllers {
            viewControllers = viewControllers.reversed()
            for currentViewController in viewControllers {
                if currentViewController.isKind(of: type) {
                    return currentViewController as! T
                }
            }
        }
        return nil
    }
    
    func popBack<T: UIViewController>(toControllerType: T.Type) {
        if var viewControllers: [UIViewController] = viewControllers {
            viewControllers = viewControllers.reversed()
            for currentViewController in viewControllers {
                if currentViewController.isKind(of: toControllerType) {
                    popToViewController(currentViewController, animated: false)
                    break
                }
            }
        }
    }
}
