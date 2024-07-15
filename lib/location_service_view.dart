import 'package:flutter/material.dart';
import 'package:location_service_app/widgets/get_location_botton.dart';
import 'package:location_service_app/widgets/get_native_location_botton.dart';

class LocationServiceView extends StatelessWidget {
  const LocationServiceView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.blue,
        elevation: 0,
        title: const Text(
          'Location Service',
          style: TextStyle(color: Colors.white),
        ),
      ),
      body: const SafeArea(
        child: SingleChildScrollView(
          child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
            SizedBox(
              height: 50,
            ),
            Center(
              child: GetLocationBotton(),
            ),
            Center(
              child: GetNativeLocationBotton(),
            ),
          ]),
        ),
      ),
    );
  }
}
