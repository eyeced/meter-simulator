# IOT Device Simulator
A Simulator Project, which manages the live communication with an IOT device. The project would use Play framework and Akka for managing these meters.
All reads from the devices would be pushed into a Kafka topic for further management.

### Some examples of IOT devices
In this project I am going to make simulation of Devices Configurable, there would be few types of meters
* Electric
* Gas
* Water

In Electric for simulation there would be different sub types as well
* Generation
    * Wind
    * Solar
    * Backup
* Consumption
    * Home
    * Work
    
In the simulation, user should be able to change measurement values for the meter.


