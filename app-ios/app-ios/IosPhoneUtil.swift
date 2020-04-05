//
//  IosPhoneUtil.swift
//  app-ios
//
//  Created by Volodymyr Chernyshov on 04.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import libPhoneNumber_iOS
import MultiPlatformLibrary

class IosPhoneUtil: PhoneUtil {
    
    private let phoneUtil = NBPhoneNumberUtil()
    private var countriesMap: [String: Country] = [:]
    
    init() {
        for region in phoneUtil.getSupportedRegions() {
            let regionString = region as! String
            let country = Country(
                region: regionString,
                countryCallingCode: Int32.init(exactly: phoneUtil.getCountryCode(forRegion: regionString))!
            )
            countriesMap[regionString] = country
        }
    }
    
    func countries() -> [Country] {
        return Array(countriesMap.values.map{ $0 })
    }
    
    func formatPhoneNumber(country: Country, phoneNumber: String) -> String {
        if Country.Companion().UNKNOWN == country {
            return phoneNumber.onlyDigits()
        }
        
        let code = "+\(country.countryCallingCode)"
        var formattedPhone = code + phoneNumber.onlyDigits()
        
        let asYouTypeFormat = NBAsYouTypeFormatter(regionCode: country.region)
        
        let codeWithNumber = code + phoneNumber.onlyDigits()
        for (_, c) in codeWithNumber.enumerated() {
            formattedPhone = asYouTypeFormat!.inputDigit("\(c)")
        }
        
        return formattedPhone.replace(code, "").trim()
    }
    
    func formatPhoneNumber(phoneNumberString: String) -> String {
        let phoneNumber = phoneNumberString.onlyPhone().take(Int(PhoneUtilKt.MAX_PHONE_LENGTH))
        var formattedPhone = phoneNumber
        
        let asYouTypeFormat = NBAsYouTypeFormatter(regionCode: "001")

        for (_, c) in phoneNumber.enumerated() {
            formattedPhone = asYouTypeFormat!.inputDigit("\(c)")
        }
        
        return formattedPhone.trim()
    }
    
    func getCountryForCountryCode(code: Int32) -> Country {
        return countriesMap[phoneUtil.getRegionCode(forCountryCode: code as NSNumber)] ?? Country.Companion().UNKNOWN
    }
    
    func isValidPhone(country: Country, phoneNumber: String) -> Bool {
        do {
            let number = NBPhoneNumber()
            number.countryCode = country.countryCallingCode as NSNumber
            number.nationalNumber = NSNumber.init(value: Int32.init(phoneNumber.onlyDigits())!)
            return try phoneUtil.isValidNumber(forRegion: number, regionCode: country.region)
        } catch let e as NSError {
            print(e)
        }
        return false
    }
    
    func isValidPhone(phoneNumber: String) -> Bool {
        do {
            return try phoneUtil.isValidNumber(phoneUtil.parse(phoneNumber.onlyPhone(), defaultRegion: "001"))
        } catch let e as NSError {
            print(e)
        }
        return false
    }
    
    func parsePhone(phone: String) -> PhoneNumber {
        do {
            let parsed = try phoneUtil.parse(phone, defaultRegion: "001")
            return PhoneNumber(nationalNumber: "\(parsed.nationalNumber)", countryCode: "\(parsed.countryCode)")
        } catch let e as NSError {
            print(e)
        }
        return PhoneNumber(nationalNumber: phone, countryCode: "")
    }
}


extension String {
    func onlyDigits() -> String {
        return PhoneUtilKt.onlyDigits(self)
    }
    
    func onlyPhone() -> String {
        return PhoneUtilKt.onlyPhone(phoneNumberString: self)
    }
    
    func take(_ count: Int) -> String {
        return String(self.prefix(count))
    }
    
    func trim() -> String {
        return self.trimmingCharacters(in: .whitespaces)
    }
    
    func replace(_ old: String, _ new: String) -> String {
        return self.replacingOccurrences(of: old, with: new)
    }
}
