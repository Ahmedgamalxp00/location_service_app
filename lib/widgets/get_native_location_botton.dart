import 'package:flutter/material.dart';
import 'package:location/location.dart';
import 'package:location_service_app/services/location_service.dart';
import 'package:location_service_app/widgets/custom_card.dart';

class GetNativeLocationBotton extends StatefulWidget {
  const GetNativeLocationBotton({
    super.key,
  });

  @override
  State<GetNativeLocationBotton> createState() =>
      _GetNativeLocationBottonState();
}

class _GetNativeLocationBottonState extends State<GetNativeLocationBotton> {
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
  String selectedProvider = 'GPS';
  bool isLoading = false;
  Map<String, dynamic>? currentLocationData;
  List<Map<String, dynamic>>? gnssStatus;
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
                currentLocationData =
                    await locationService.getCurrentLocation();
                gnssStatus =
                    await locationService.getGnssStatus(selectedProvider);
                setState(() {
                  isLoading = false;
                });

                // locationData = await locationService.getMyLocation();
                // locationProvider = await locationService.getLocationProvider();
                // await locationService.startGnssStatusMonitoring();
                // gpsLocationData = await locationService.getGPSLocation();

                // await locationService.startSpecificGnssMonitoring('GLONASS');
                // locationProviders =
                //     await locationService.getLocationProviders();
              },
              child: Row(
                children: [
                  Padding(
                    padding:
                        const EdgeInsets.symmetric(vertical: 5, horizontal: 20),
                    child: isLoading
                        ? const CircularProgressIndicator(
                            color: Colors.white,
                          )
                        : const Text(
                            'Get Native Location',
                            style: TextStyle(fontSize: 20),
                          ),
                  ),
                  const Spacer(),
                  DropdownButton<String>(
                    value: selectedProvider,
                    items: <String>[
                      'GPS',
                      'GLONASS',
                      'BeiDou',
                      'Galileo',
                      'QZSS',
                      'SBAS',
                      'IRNSS'
                    ].map((String value) {
                      return DropdownMenuItem<String>(
                        value: value,
                        child: Text(value),
                      );
                    }).toList(),
                    onChanged: (newValue) {
                      setState(() {
                        selectedProvider = newValue!;
                      });
                    },
                  ),
                  const SizedBox(width: 20)
                ],
              )),
        ),
        const SizedBox(height: 5),
        // if (locationData != null)
        //   CustomCard(
        //       text:
        //           'lattitude: ${locationData?.latitude}\nlongtitude: ${locationData?.longitude}\n'
        //           'Location Accuracy: ${locationData?.accuracy ?? 0}'),
        // const SizedBox(height: 10),
        // if (locationProvider != null)
        //   CustomCard(text: 'Location provider: $locationProvider'),
        // const SizedBox(height: 10),
        // if (gpsLocationData != null)
        //   CustomCard(text: 'gps Location: \n$gpsLocationData'),
        if (currentLocationData != null)
          CustomCard(
              text:
                  'lattitude: ${currentLocationData!['latitude']}\nlongtitude: ${currentLocationData!['longitude']}\n'
                  'Location Accuracy: ${currentLocationData!['accuracy']}'),
        const SizedBox(height: 10),
        if (gnssStatus != null)
          SizedBox(
            height: 300,
            width: 450,
            child: ListView.builder(
              itemCount: gnssStatus!.length,
              itemBuilder: (context, index) {
                final satellite = gnssStatus![index];
                return Card(
                  child: ListTile(
                    title: Text(
                        'Constellation: ${satellite['constellationType']} - SVID: ${satellite['svid']}'),
                    subtitle: Text('CN0: ${satellite['cn0DbHz']} dBHz \n'
                        'Azimuth: ${satellite['azimuthDegrees']}° \n'
                        'Elevation: ${satellite['elevationDegrees']}°\n'
                        'Almanac Data: ${satellite['hasAlmanacData']}\n '
                        'Ephemeris Data: ${satellite['hasEphemerisData']}\n '
                        'Used in fix: ${satellite['usedInFix']}'),
                    trailing: Text(
                      '$index',
                    ),
                  ),
                );
              },
            ),
          ),
      ],
    );
  }
}
