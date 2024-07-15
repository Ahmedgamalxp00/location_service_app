import 'package:flutter/material.dart';
import 'package:location/location.dart';
import 'package:location_service_app/services/location_service.dart';
import 'package:location_service_app/widgets/custom_card.dart';

class GetLocationBotton extends StatefulWidget {
  const GetLocationBotton({
    super.key,
  });

  @override
  State<GetLocationBotton> createState() => _GetLocationBottonState();
}

class _GetLocationBottonState extends State<GetLocationBotton> {
  @override
  void initState() {
    locationService = LocationService();
    locationService.requestLocationServiceAndLocationPermission();
    super.initState();
  }

  late LocationService locationService;
  LocationData? locationData;
  String? gpsLocationData;
  String? locationProvider;
  String? gnssStatus;
  bool isLoading = false;
  Map<String, dynamic>? currentLocationData;
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SizedBox(
          width: 450,
          height: 70,
          child: TextButton(
              style: const ButtonStyle(
                shape: WidgetStatePropertyAll(RoundedRectangleBorder(
                  borderRadius: BorderRadius.all(Radius.circular(10)),
                )),
                backgroundColor: WidgetStatePropertyAll(Colors.blue),
                foregroundColor: WidgetStatePropertyAll(Colors.white),
              ),
              onPressed: () async {
                setState(() {
                  isLoading = true;
                });

                locationData = await locationService.getMyLocation();
                locationProvider = await locationService.getLocationProvider();
                // await locationService.startGnssStatusMonitoring();
                // gpsLocationData = await locationService.getGPSLocation();
                // currentLocationData =
                //     await locationService.getCurrentLocation();
                // await locationService.startSpecificGnssMonitoring('GLONASS');
                // locationProviders =
                //     await locationService.getLocationProviders();
                setState(() {
                  isLoading = false;
                });

                print(
                    'lattitude: ${locationData?.latitude}\nlongtitude: ${locationData?.longitude}');
              },
              child: Padding(
                padding:
                    const EdgeInsets.symmetric(vertical: 5, horizontal: 20),
                child: isLoading
                    ? const CircularProgressIndicator(
                        color: Colors.white,
                      )
                    : const Text(
                        'Get My Location',
                        style: TextStyle(fontSize: 20),
                      ),
              )),
        ),
        const SizedBox(height: 5),
        if (locationData != null)
          CustomCard(
              text:
                  'lattitude: ${locationData?.latitude}\nlongtitude: ${locationData?.longitude}\n'
                  'Location Accuracy: ${locationData?.accuracy ?? 0}'),
        if (locationProvider != null)
          CustomCard(text: 'Location provider: $locationProvider'),
        const SizedBox(height: 20),
        // if (gpsLocationData != null)
        //   CustomCard(text: 'gps Location: \n$gpsLocationData'),
        // if (currentLocationData != null)
        //   CustomCard(
        //       text:
        //           'lattitude: ${currentLocationData!['latitude']}\nlongtitude: ${currentLocationData!['longitude']}\n'
        //           'Location Accuracy: ${currentLocationData!['accuracy']}'),
      ],
    );
  }
}
