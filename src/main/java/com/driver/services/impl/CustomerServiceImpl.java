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

        List<Driver> driverList=driverRepository2.findAll();

        if(driverList.size()==0){
            throw new Exception("No cab available!");
        }

        int min=Integer.MAX_VALUE;
        for(Driver driver:driverList){
            if(driver.getDriverId()<min && driver.getCab().getAvailable()){
                min=driver.getDriverId();
            }
        }

        if(min==Integer.MAX_VALUE)
        {
            throw new Exception("No cab available!");
        }

        int driverId=min;

        TripBooking tripBooking=new TripBooking(fromLocation,toLocation, distanceInKm,TripStatus.CONFIRMED);


        if(driverRepository2.findById(driverId).isPresent()) {
            Driver driver=driverRepository2.findById(driverId).get();
            driver.getCab().setAvailable(false);
            tripBooking.setBill(driver.getCab().getPerKmRate()*distanceInKm);
            Customer customer=customerRepository2.findById(customerId).get();

            tripBooking.setDriver(driver);
            tripBooking.setCustomer(customer);

//		driver.getTripBookingList().add(tripBooking);
//		customer.getTripBookingList().add(tripBooking);

//		driverRepository2.save(driver);
//		customerRepository2.save(customer);

            tripBookingRepository2.save(tripBooking);


            return tripBooking;
        }
        throw new Exception("No cab available!");
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
