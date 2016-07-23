# ST-AverageTemperatureThermostatController
This SmartThings SmartApp uses the average temperature of your temperature sensors and decides based on input to either set your thermostat or simply update the temperature of a virtual temperature sensor. 

If using the virtual sensor, you will need to create a virtual device and use this custom device type.
https://github.com/ericvitale/ST-AverageTemperatureThermostatController/blob/master/devicetypes/ericvitale/settable-temperature-sensor.src/settable-temperature-sensor.groovy

Use this app at your own risk.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at: 

http://www.apache.org/licenses/LICENSE-2.0 
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. 

#Manual Installation

1. Copy the raw text of the "average-temperature-thermostat-controller.groovy" file.
2. Login at http://graph.api.smartthings.com
3. Click "My SmartApps"
4. Select "New SmartApp".
5. Select "From Code".
6. Paste the raw code in and Save.
7. Select Publish, For Me.
8. The app should appear in the Marketplace under "My Apps" on your phone / tablet.

#Integrated Installation
1. Login at http://graph.api.smartthings.com
2. Click "My SmartApps"
3. Click "Settings"
4. Click "Add new repository"
5. Owner: ericvitale
6. Name: ST-AverageTemperatureThermostatController
7. Branch: master
8. Click "Save"
9. Click "Update from Repo"
10. Click "ST-AverageTemperatureThermostatController (master)"
11. Select "Publish"
12. Click "Execute Update"
13. The app should appear in the Marketplace under "My Apps" on your phone / tablet.
