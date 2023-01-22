package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.CabRepository;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.*;

import static com.driver.model.TripStatus.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository2;

    @Autowired
    DriverRepository driverRepository2;

    @Autowired
    TripBookingRepository tripBookingRepository2;
    @Autowired
    private CabRepository cabRepository;

    @Override
    public void register(Customer customer) {
        //Save the customer in database
        customerRepository2.save(customer);
    }

    @Override
    public void deleteCustomer(Integer customerId) {
        // Delete customer without using deleteById function
        Customer customer = customerRepository2.findById(customerId).get();
        customerRepository2.delete(customer);
    }

    @Override
    public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception {
        //Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
        //Avoid using SQL query

        List<Driver> drivers = driverRepository2.findAll();
        Collections.sort(drivers, (o1, o2) -> o1.getDriverId() - o2.getDriverId());
        Driver bookDriver = null;
        for (Driver driver : drivers) {
            if (driver.getCab().getAvailable()) {
                bookDriver = driver;
                break;
            }

        }
        if (bookDriver == null) {
            throw new Exception("No cab available!");
        }
        Customer customer = customerRepository2.findById(customerId).get();
        TripBooking tripBooking = new TripBooking(fromLocation, toLocation, distanceInKm, CONFIRMED);
        tripBooking.setCustomer(customer);
        tripBooking.setBill(bookDriver.getCab().getPerKmRate() * distanceInKm);
        tripBooking.setDriver(bookDriver);
        List<TripBooking> driverTripBookingList = bookDriver.getTripBookingList();
        if (driverTripBookingList == null) {
            driverTripBookingList = new ArrayList<>();
        }
        bookDriver.setTripBookingList(driverTripBookingList);

        List<TripBooking> customerTripBookingList = customer.getTripBookingList();
        if (customerTripBookingList == null) {
            customerTripBookingList = new ArrayList<>();
        }
        customer.setTripBookingList(customerTripBookingList);

        customerRepository2.save(customer);
        tripBookingRepository2.save(tripBooking);
        driverRepository2.save(bookDriver);
        return tripBooking;
    }

    @Override
    public void cancelTrip(Integer tripId) {
        //Cancel the trip having given trip Id and update TripBooking attributes accordingly
        TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
        tripBooking.setStatus(CANCELED);
        tripBooking.setBill(0);
        tripBooking.getDriver().getCab().setAvailable(true);
        tripBookingRepository2.save(tripBooking);
    }

    @Override
    public void completeTrip(Integer tripId) {
        //Complete the trip having given trip Id and update TripBooking attributes accordingly
        TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
        tripBooking.setStatus(COMPLETED);
        tripBooking.getDriver().getCab().setAvailable(true);
        tripBookingRepository2.save(tripBooking);
    }
}
