/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi.store;

import com.mycompany.smartcampusapi.exception.LinkedResourceNotFoundException;
import com.mycompany.smartcampusapi.exception.RoomNotEmptyException;
import com.mycompany.smartcampusapi.exception.SensorUnavailableException;
import com.mycompany.smartcampusapi.model.Room;
import com.mycompany.smartcampusapi.model.Sensor;
import com.mycompany.smartcampusapi.model.SensorReading;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
/**
 *
 * @author user
 */
public class DataStore {
    
    private static final Map<String, Room> ROOMS =
            Collections.synchronizedMap(new HashMap<String, Room>());

    private static final Map<String, Sensor> SENSORS =
            Collections.synchronizedMap(new HashMap<String, Sensor>());

    private static final Map<String, List<SensorReading>> SENSOR_READINGS =
            Collections.synchronizedMap(new HashMap<String, List<SensorReading>>());

    static {
        seedDemoData();
    }

    private DataStore() {
    }

    private static void seedDemoData() {
        Room room1 = new Room("LIB-301", "Library Quiet Study", 120, new ArrayList<String>());
        Room room2 = new Room("LAB-201", "AI Laboratory", 40, new ArrayList<String>());

        ROOMS.put(room1.getId(), room1);
        ROOMS.put(room2.getId(), room2);

        Sensor sensor1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 24.5, "LIB-301");
        Sensor sensor2 = new Sensor("CO2-001", "CO2", "MAINTENANCE", 410.0, "LAB-201");

        SENSORS.put(sensor1.getId(), sensor1);
        SENSORS.put(sensor2.getId(), sensor2);

        room1.getSensorIds().add(sensor1.getId());
        room2.getSensorIds().add(sensor2.getId());

        List<SensorReading> tempReadings = new ArrayList<SensorReading>();
        tempReadings.add(new SensorReading(UUID.randomUUID().toString(), System.currentTimeMillis(), 24.5));

        List<SensorReading> co2Readings = new ArrayList<SensorReading>();
        co2Readings.add(new SensorReading(UUID.randomUUID().toString(), System.currentTimeMillis(), 410.0));

        SENSOR_READINGS.put(sensor1.getId(), tempReadings);
        SENSOR_READINGS.put(sensor2.getId(), co2Readings);
    }

    public static synchronized List<Room> getAllRooms() {
        return new ArrayList<Room>(ROOMS.values());
    }

    public static synchronized Room getRoom(String roomId) {
        return ROOMS.get(roomId);
    }

    public static synchronized boolean addRoom(Room room) {
        if (ROOMS.containsKey(room.getId())) {
            return false;
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<String>());
        }

        ROOMS.put(room.getId(), room);
        return true;
    }

    public static synchronized boolean deleteRoom(String roomId) {
        Room room = ROOMS.get(roomId);

        if (room == null) {
            return false;
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                    "Room '" + roomId + "' cannot be deleted because sensors are still assigned to it.");
        }

        ROOMS.remove(roomId);
        return true;
    }

    public static synchronized List<Sensor> getAllSensors() {
        return new ArrayList<Sensor>(SENSORS.values());
    }

    public static synchronized Sensor getSensor(String sensorId) {
        return SENSORS.get(sensorId);
    }

    public static synchronized List<Sensor> getSensorsByType(String type) {
        List<Sensor> result = new ArrayList<Sensor>();

        if (type == null || type.trim().isEmpty()) {
            result.addAll(SENSORS.values());
            return result;
        }

        for (Sensor sensor : SENSORS.values()) {
            if (sensor.getType() != null && sensor.getType().equalsIgnoreCase(type.trim())) {
                result.add(sensor);
            }
        }

        return result;
    }

    public static synchronized boolean addSensor(Sensor sensor) {
        Room room = ROOMS.get(sensor.getRoomId());

        if (room == null) {
            throw new LinkedResourceNotFoundException(
                    "Cannot create sensor because room '" + sensor.getRoomId() + "' does not exist.");
        }

        if (SENSORS.containsKey(sensor.getId())) {
            return false;
        }

        SENSORS.put(sensor.getId(), sensor);

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<String>());
        }

        room.getSensorIds().add(sensor.getId());
        SENSOR_READINGS.put(sensor.getId(), new ArrayList<SensorReading>());
        return true;
    }

    public static synchronized List<SensorReading> getReadings(String sensorId) {
        if (!SENSORS.containsKey(sensorId)) {
            return null;
        }

        List<SensorReading> readings = SENSOR_READINGS.get(sensorId);

        if (readings == null) {
            return new ArrayList<SensorReading>();
        }

        return new ArrayList<SensorReading>(readings);
    }

    public static synchronized SensorReading addReading(String sensorId, SensorReading reading) {
        Sensor sensor = SENSORS.get(sensorId);

        if (sensor == null) {
            return null;
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Sensor '" + sensorId + "' is currently in MAINTENANCE and cannot accept new readings.");
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        if (reading.getTimestamp() == 0L) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        List<SensorReading> readings = SENSOR_READINGS.get(sensorId);

        if (readings == null) {
            readings = new ArrayList<SensorReading>();
            SENSOR_READINGS.put(sensorId, readings);
        }

        readings.add(reading);
        sensor.setCurrentValue(reading.getValue());

        return reading;
    }
    
    
}
