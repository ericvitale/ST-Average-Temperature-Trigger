/**
 *  Average Temperature Thermostat Controller
 *
 *  Version 1.0.1 - 07/24/16
 *   -- Added proper logging to make the app less verbose.
 *   -- Added the active setting.
 *
 *  Version 1.0.0 - 07/05/16
 *   -- Initial Build
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
 *  You can find this SmartApp @ https://github.com/ericvitale/ST-Home-Notify/
 *  You can find my other device handlers & SmartApps @ https://github.com/ericvitale
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
    
    section("Setting") {
        	label(title: "Assign a name", required: false)
            input "active", "bool", title: "Rules Active?", required: true, defaultValue: true
            input "logging", "enum", title: "Log Level", required: true, defaultValue: "DEBUG", options: ["TRACE", "DEBUG", "INFO", "WARN", "ERROR"]
        }
}

def determineLogLevel(data) {
	if(data.toUpperCase() == "TRACE") {
    	return 0
    } else if(data.toUpperCase() == "DEBUG") {
    	return 1
    } else if(data.toUpperCase() == "INFO") {
    	return 2
    } else if(data.toUpperCase() == "WARN") {
    	return 3
    } else {
    	return 4
    }
}

def log(data, type) {
    
    data = "ATTC -- " + data
    
    try {
        if(determineLogLevel(type) >= determineLogLevel(logging)) {
            if(type.toUpperCase() == "TRACE") {
                log.trace "${data}"
            } else if(type.toUpperCase() == "DEBUG") {
                log.debug "${data}"
            } else if(type.toUpperCase() == "INFO") {
                log.info "${data}"
            } else if(type.toUpperCase() == "WARN") {
                log.warn "${data}"
            } else if(type.toUpperCase() == "ERROR") {
                log.error "${data}"
            } else {
                log.error "ATTC -- Invalid Log Setting -->${type}<--."
            }
        }
    } catch(e) {
    	log.error ${e}
    }
}

def installed() {
	log("Installed with settings: ${settings}", "INFO")
	initialize()
}

def updated() {
	log("Updated with settings: ${settings}", "INFO")
	unsubscribe()
	initialize()
}

def initialize() {
	if(active) {
		log("App is active.", "INFO")
        subscribe(temperatureSensors, "temperature", temperatureHandler)
	    updateTemp()
    } else {
    	log("App is not active.", "INFO")
    }

    log("Initialization complete.", "INFO")
}

def temperatureHandler(evt) {
	log("Temperature event ${evt.descriptionText} and value: ${evt.doubleValue}.", "INFO")
    updateTemp()
}

def updateTemp() {
    def averageTemp = 0.0
    def currentState
    
    temperatureSensors.each() {
    	log("${it.displayName} Temp: ${it.currentValue("temperature")}.", "TRACE")	
        
        currentState = it.currentState("temperature")
        
        log("currentState.integerValue: ${currentState.integerValue}.", "TRACE")

        try {
            averageTemp += currentState.integerValue
        } catch(e) {
        	log("ERROR -- ${e}", "ERROR")
        }
    }
   	
    try {
    	averageTemp = averageTemp / temperatureSensors.size()
    } catch(e) {
    	log("ERROR -- ${e}", "ERROR")
    }
    
    if(setThermostat) {
    	log("Evaluating thermostat rules...", "INFO")
        if(averageTemp > maxTemp) {
        	log("Begin cooling to ${coolingSetpoint}.", "INFO")
            beginCooling(coolingSetpoint)
        } else if(averageTemp < minTemp) {
	        log("Begin heating to ${heatingSetpoint}.", "INFO")
            beginHeating(heatingSetpoint)
        } else {
            log("Temperature is just right.", "INFO")
        }
    }
    
    if(setVirtualTemp) {
        log("Updating ${settableSensor.label} to ${Math.round(averageTemp * 100) / 100}.", "INFO")
    	settableSensor.setTemperature((Math.round(averageTemp * 100) / 100).toString())
    }
}

def beginCooling(val) {
	log("Setting coolingSetpoint to: ${val}.", "DEBUG")
    thermostat.setCoolingSetpoint(val)
}

def beginHeating(val) {
	log("Setting heatingSetpoint to: ${val}.", "DEBUG")
	thermostat.setHeatingSetpoint(val)
}