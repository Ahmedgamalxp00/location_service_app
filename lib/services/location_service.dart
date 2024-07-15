import 'package:flutter/services.dart';
import 'package:location/location.dart';
import 'package:location_service_app/core/errors/exceptions.dart';

class LocationService {
  static const platform =
      MethodChannel('com.example.location_service_app/location');

  Location location = Location();
  Future<void> checkAndRequestLocationService() async {
    var isServiceEnabled = await location.serviceEnabled();
    if (!isServiceEnabled) {
      isServiceEnabled = await location.requestService();
      if (!isServiceEnabled) {
        throw LocationServiceException();
      }
    }
  }

  Future<void> checkAndRequestLocationPermission() async {
    var permissionStatus = await location.hasPermission();
    if (permissionStatus == PermissionStatus.deniedForever) {
      throw LocationPermissionException();
    }
    if (permissionStatus == PermissionStatus.denied) {
      permissionStatus = await location.requestPermission();
      if (permissionStatus != PermissionStatus.granted) {
        throw LocationPermissionException();
      }
    }
  }
  // Set accuracy to high

  Future<LocationData?> getMyLocation() async {
    await checkAndRequestLocationService();
    await checkAndRequestLocationPermission();
    location.changeSettings(accuracy: LocationAccuracy.high);
    return await location.getLocation();
  }

  Future<void> requestLocationServiceAndLocationPermission() async {
    await checkAndRequestLocationService();
    await checkAndRequestLocationPermission();
  }

  Future<String> getLocationProvider() async {
    String locationProvider;
    try {
      locationProvider = await platform.invokeMethod('getLocationProvider');
      return locationProvider;
    } on PlatformException catch (e) {
      return "Failed to get location provider: '${e.message}'.";
    }
  }

  Future<void> startGnssStatusMonitoring() async {
    try {
      await platform.invokeMethod('startGnssStatusMonitoring');
    } on PlatformException catch (e) {
      print("Failed to start GNSS status monitoring: '${e.message}'");
    }
  }

  Future<String> getLocationProviders() async {
    String providers;
    try {
      final String result = await platform.invokeMethod('getLocationProviders');
      providers = result;
      return providers;
    } on PlatformException catch (e) {
      return "Failed to get location providers: '${e.message}'";
    }
  }

  Future<void> startSpecificGnssMonitoring(String gnssType) async {
    try {
      await platform
          .invokeMethod('startSpecificGnssMonitoring', {'gnssType': gnssType});
    } on PlatformException catch (e) {
      print("Failed to start specific GNSS status monitoring: '${e.message}'");
    }
  }

  Future<String> getGPSLocation() async {
    final location =
        await platform.invokeMethod<Map<dynamic, dynamic>>('getGPSLocation');

    return 'lat: ${location!['latitude']}\nlon: ${location['longitude']}\nAccuracy: ${location['accuracy']}';
  }

  // Future<List<Map<String, dynamic>>> handleGnssStatusUpdate(
  //     MethodCall call) async {
  //   if (call.method == 'updateGnssStatus') {
  //     return List<Map<String, dynamic>>.from(call.arguments);
  //   }
  //   return [];
  // }

  Future<Map<String, dynamic>?> getCurrentLocation() async {
    final String? location = await platform.invokeMethod('getCurrentLocation');
    if (location != null) {
      final parts = location.split(',');
      final double latitude = double.parse(parts[0]);
      final double longitude = double.parse(parts[1]);
      final double accuracy = double.parse(parts[2]);
      return {
        'latitude': latitude,
        'longitude': longitude,
        'accuracy': accuracy
      };
    }
    return null;
  }

  Future<List<Map<String, dynamic>>> getGnssStatus(String provider) async {
    try {
      await getCurrentLocation();
      List<Map<String, dynamic>> gnssStatus0;
      final List<dynamic> gnssStatus =
          await platform.invokeMethod('GnssStatus', {'provider': provider});
      print('$gnssStatus[0]["constellationType"]');
      gnssStatus0 = List<Map<String, dynamic>>.from(
          gnssStatus.map((status) => Map<String, dynamic>.from(status as Map)));
      return gnssStatus0;
      // final List<Map<String, dynamic>> gnssStatus =
      //     await platform.invokeMethod('GnssStatus');
    } catch (e) {
      return [
        {'error': 'Failed to get GNSS status: ${e.toString()}'}
      ];
    }
  }
}
