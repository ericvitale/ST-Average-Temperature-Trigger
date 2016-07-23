/**
 *  Average Temperature Thermostat Controller
 *
 *  Copyright 2016 Eric Vitale
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Average Temperature Thermostat Controller",
    namespace: "ericvitale",
    author: "Eric Vitale",
    description: "Control a thermostat based on the average temperature of temperature sensors.",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Settable Sensor") {
    	input "settableSensor", "capability.temperatureMeasurement", title: "Virtual Settable Temperature Sensor", multiple: false, required: false
        input "setVirtualTemp", "bool", title: "Set virtual temp based on average temp?", required: true, defaultValue: false
    }

	section("Select your thermostat.") {
        input "thermostat", "capability.thermostat", multiple:false, title: "Thermostat", required: false
        input "setThermostat", "bool", title: "Set your thermostate temp based on average temp?", required: true, defaultValue: false
	}
    
    section("Select your temperature sensors.") {
    	input "temperatureSensors", "capability.temperatureMeasurement", multiple: true
    }
    
    section("Select the temperature at which you want to begin cooling.") {
    	input "maxTemp", "decimal", title: "Max Temperature", range: "*", required: false
    }
    
    section("Select the temperature at which you want to cool to.") {
    	input "coolingSetpoint", "decimal", title: "Cooling Setpoint", range: "*", required: false
    }
    
    section("Select the temperature at which you want to begin heating.") {
    	input "minTemp", "decimal", title: "Min Temperature", range: "*", required: false
    }
    
    section("Select the temperature at which you want to heat to.") {
    	input "heatingSetpoint", "decimal", title: "Heating Setpoint", range: "*", required: false
    }
}
def installed() {
	log.debug "ATTC - Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "ATTC - Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(temperatureSensors, "temperature", temperatureHandler)
    updateTemp()
    log.debug "AATC - Initialization complete."
}

def temperatureHandler(evt) {
	log.debug "ATTC - Temperature Event Description: ${evt.descriptionText}."
    log.debug "ATTC - Temperature Event Value: ${evt.doubleValue}."
    
    updateTemp()
}

def updateTemp() {
    def averageTemp = 0.0
    def currentState
    
    temperatureSensors.each() {
    	log.debug "ATTC - ${it.displayName} Temp: ${it.currentValue("temperature")}."	
        
        currentState = it.currentState("temperature")
        
        log.debug "ATTC - currentState.integerValue: ${currentState.integerValue}."

        try {
            averageTemp += currentState.integerValue
            log.debug "ATTC - Average Temperature: ${averageTemp}."
        } catch(e) {
        	log.debug e
        }
    }
   	
    try {
    	averageTemp = averageTemp / temperatureSensors.size()
    	log.debug "ATTC - Average Temperature: ${averageTemp}."
    } catch(e) {
    	log.debug e
    }
    
    if(setThermostat) {
    	log.debug "ATTC - Setting Thermostate"
        if(averageTemp > maxTemp) {
            beginCooling(coolingSetpoint)
        } else if(averageTemp < minTemp) {
            beginHeating(heatingSetpoint)
        } else {
            log.debug "ATTC - Temperature is just right."
        }
    }
    
    if(setVirtualTemp) {
    	log.debug "ATTC - Raw average ${averageTemp}."
        log.debug "ATTC - Updating virtual sensor to ${Math.round(averageTemp * 100) / 100}."
        
    	settableSensor.setTemperature((Math.round(averageTemp * 100) / 100).toString())
    }
}

def beginCooling(val) {
	log.debug "ATTC - Setting coolingSetpoint to: ${val}."
    thermostat.setCoolingSetpoint(val)
}

def beginHeating(val) {
	log.debug "ATTC - Setting heatingSetpoint to: ${val}."
	thermostat.setHeatingSetpoint(val)
}