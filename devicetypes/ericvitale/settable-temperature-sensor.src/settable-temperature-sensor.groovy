/**
 *  Copyright 2015 SmartThings
 *
 *
 */
metadata {
	definition (name: "Settable Temperature Sensor", namespace: "ericvitale", author: "ericvitale@gmail.com") {
		capability "Temperature Measurement"
		capability "Sensor"
        
        command "setTemperature", ["number"]
	}
    
    preferences {
    	section("Setting") {
			input "logging", "enum", title: "Log Level", required: true, defaultValue: "DEBUG", options: ["TRACE", "DEBUG", "INFO", "WARN", "ERROR"]
        }
    }

	// simulator metadata
	simulator {
		for (int i = 0; i <= 100; i += 10) {
			status "${i}F": "temperature: $i F"
		}

		for (int i = 0; i <= 100; i += 10) {
			status "${i}%": "humidity: ${i}%"
		}
	}

	// UI tile definitions
	tiles {
		valueTile("temperature", "device.temperature", width: 2, height: 2) {
			state("temperature", label:'${currentValue}°',
				backgroundColors:[
                    // Celsius Color Range
                    [value: 0, color: "#153591"],
                    [value: 7, color: "#1e9cbb"],
                    [value: 15, color: "#90d2a7"],
                    [value: 23, color: "#44b621"],
                    [value: 29, color: "#f1d801"],
                    [value: 33, color: "#d04e00"],
                    [value: 36, color: "#bc2323"],
                    // Fahrenheit Color Range
                    [value: 40, color: "#153591"],
                    [value: 44, color: "#1e9cbb"],
                    [value: 59, color: "#90d2a7"],
                    [value: 74, color: "#44b621"],
                    [value: 84, color: "#f1d801"],
                    [value: 92, color: "#d04e00"],
                    [value: 96, color: "#bc2323"]
				]
			)
		}
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		main(["temperature"])
		details(["temperature", "refresh"])
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
    
    data = "Settable Temp -- " + data
    
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
                log.error "Settable Temp -- Invalid Log Setting"
            }
        }
    } catch(e) {
    	log.error ${e}
    }
}


// Parse incoming device messages to generate events
def parse(String description) {
	def name = parseName(description)
	def value = parseValue(description)
	def unit = name == "temperature" ? getTemperatureScale() : null
	def result = createEvent(name: name, value: value, unit: unit)
	log.debug "Parse returned ${result?.descriptionText}"
	return result
}

private String parseName(String description) {
	
    if (description?.startsWith("temperature: ")) {
		return "temperature"
	}
    
	null
}

private String parseValue(String description) {
	if (description?.startsWith("temperature: ")) {
		return zigbee.parseHATemperatureValue(description, "temperature: ", getTemperatureScale())
	} 
    
	null
}

def setTemperature(val) {
	log("Setting temperature from external input, temperature = ${val}.", "DEBUG")
	sendEvent(name: "temperature", value: val, unit: getTemperatureScale())
}

def refresh() {
	log("Refresh", "DEBUG")
}

def updated() {
	log("Updated", "DEBUG")
}