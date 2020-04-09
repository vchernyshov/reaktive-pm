Pod::Spec.new do |spec|
    spec.name                     = 'MultiPlatformLibraryRpm'
    spec.version                  = '0.0.1'
    spec.homepage                 = 'https://github.com/vchernyshov/reaktive-pm'
    spec.source                   = { :git => "https://github.com/vchernyshov/reaktive-pm.git", :tag => "release/#{spec.version}" }
    spec.authors                  = 'Garage Development'
    spec.license                  = { :type => 'MIT', :file => 'LICENSE' }
    spec.summary                  = 'Swift additions to reaktive-pm Kotlin/Native library'
    spec.module_name              = "#{spec.name}"
    
    spec.dependency 'MultiPlatformLibrary'

    spec.ios.deployment_target  = '9.0'
    spec.swift_version = '4.2'

    spec.subspec 'base' do |sp|
      sp.source_files = "rpm/src/iosMain/swift/base/**/*.{h,m,swift}"
    end

    spec.subspec 'bindings' do |sp|
      sp.source_files = "rpm/src/iosMain/swift/bindings/**/*.{h,m,swift}"
    end

    spec.subspec 'core' do |sp|
      sp.source_files = "rpm/src/iosMain/swift/core/**/*.{h,m,swift}"
    end

    spec.subspec 'widget' do |sp|
      sp.source_files = "rpm/src/iosMain/swift/widget/**/*.{h,m,swift}"
    end

    spec.subspec 'skyfloating' do |sp|
      sp.source_files = "rpm/src/iosMain/swift/skyfloating/**/*.{h,m,swift}"
      sp.dependency 'SkyFloatingLabelTextField'
    end

    spec.pod_target_xcconfig = {
        'VALID_ARCHS' => '$(ARCHS_STANDARD_64_BIT)'
    }
end
